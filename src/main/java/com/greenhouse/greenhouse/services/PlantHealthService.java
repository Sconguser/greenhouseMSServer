package com.greenhouse.greenhouse.services;

import com.greenhouse.greenhouse.dtos.PlantAlertDTO;
import com.greenhouse.greenhouse.models.*;
import com.greenhouse.greenhouse.repositories.AnalyticsSettingsRepository;
import com.greenhouse.greenhouse.repositories.GreenhouseRepository;
import com.greenhouse.greenhouse.repositories.PlantAlertRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Periodically evaluates every plant's requirements against the live conditions
 * and maintains {@link PlantAlert} records. A breach must persist for
 * {@link #DEBOUNCE_MINUTES} before it becomes an ACTIVE alert (and the user is
 * notified), so transient spikes during normal regulation don't raise false
 * alarms. Conditions returning to range resolve the alert.
 */
@Service
public class PlantHealthService {

    /** Fallback when no settings row exists yet. */
    private static final int DEFAULT_DEBOUNCE_MINUTES = 10;
    /** Resolved alerts older than this are pruned. */
    private static final int RESOLVED_RETENTION_DAYS = 7;

    private static final List<PlantAlertStatus> OPEN =
            List.of(PlantAlertStatus.PENDING, PlantAlertStatus.ACTIVE);

    private final GreenhouseRepository greenhouseRepository;
    private final PlantAlertRepository alertRepository;
    private final NotificationService notificationService;
    private final AnalyticsSettingsRepository settingsRepository;

    public PlantHealthService(GreenhouseRepository greenhouseRepository,
                              PlantAlertRepository alertRepository,
                              NotificationService notificationService,
                              AnalyticsSettingsRepository settingsRepository) {
        this.greenhouseRepository = greenhouseRepository;
        this.alertRepository = alertRepository;
        this.notificationService = notificationService;
        this.settingsRepository = settingsRepository;
    }

    private int debounceMinutes() {
        return settingsRepository.findById(1L)
                .map(s -> s.getPlantCheckDebounceMinutes())
                .orElse(DEFAULT_DEBOUNCE_MINUTES);
    }

    @Transactional
    public void checkAllPlants() {
        LocalDateTime now = LocalDateTime.now();
        int debounce = debounceMinutes();
        for (Greenhouse gh : greenhouseRepository.findAll()) {
            if (gh.getZones() == null) continue;
            for (Zone zone : gh.getZones()) {
                if (zone.getFlowerpots() == null) continue;
                for (Flowerpot fp : zone.getFlowerpots()) {
                    if (fp.getPlants() == null) continue;
                    for (Plant plant : fp.getPlants()) {
                        for (RequirementEntity req : plant.getRequirements()) {
                            evaluate(gh, zone, fp, plant, req, now, debounce);
                        }
                    }
                }
            }
        }
        alertRepository.deleteResolvedBefore(now.minusDays(RESOLVED_RETENTION_DAYS));
    }

    private void evaluate(Greenhouse gh, Zone zone, Flowerpot fp,
                          Plant plant, RequirementEntity req, LocalDateTime now, int debounce) {
        // Only THRESHOLD is implemented today; other kinds are skipped until added.
        if (req.getKind() != RequirementKind.THRESHOLD) return;

        Double value = resolveCurrentValue(gh, zone, fp, req.getName());
        // No matching parameter or no reading yet → can't judge; leave state untouched.
        if (value == null) return;

        boolean breached = isOutOfRange(value, req.getLowerThreshold(), req.getUpperThreshold());

        PlantAlert open = alertRepository
                .findFirstByPlantIdAndFlowerpotIdAndRequirementNameAndStatusIn(
                        plant.getId(), fp.getId(), req.getName(), OPEN)
                .orElse(null);

        if (breached) {
            if (open == null) {
                open = newPending(gh, fp, plant, req, value, now);
                alertRepository.save(open);
            } else {
                open.setLastValue(value);
                open.setLastSeenAt(now);
                open.setMessage(buildMessage(plant, fp, req, value));
                if (open.getStatus() == PlantAlertStatus.PENDING
                        && open.getFirstDetectedAt().plusMinutes(debounce).isBefore(now)) {
                    // Sustained long enough → promote and notify once.
                    open.setStatus(PlantAlertStatus.ACTIVE);
                    open.setRaisedAt(now);
                    notificationService.broadcast("Plant needs attention", open.getMessage());
                }
                alertRepository.save(open);
            }
        } else if (open != null) {
            if (open.getStatus() == PlantAlertStatus.PENDING) {
                // Never alerted — just drop it.
                alertRepository.delete(open);
            } else {
                open.setStatus(PlantAlertStatus.RESOLVED);
                open.setResolvedAt(now);
                open.setLastValue(value);
                open.setLastSeenAt(now);
                alertRepository.save(open);
            }
        }
    }

    private PlantAlert newPending(Greenhouse gh, Flowerpot fp, Plant plant,
                                  RequirementEntity req, Double value, LocalDateTime now) {
        PlantAlert a = new PlantAlert();
        a.setGreenhouseId(gh.getId());
        a.setGreenhouseName(gh.getName());
        a.setFlowerpotId(fp.getId());
        a.setFlowerpotName(fp.getName());
        a.setPlantId(plant.getId());
        a.setPlantName(plant.getName());
        a.setRequirementName(req.getName());
        a.setParameterName(req.getName());
        a.setUnit(req.getUnit());
        a.setKind(req.getKind());
        a.setLowerThreshold(req.getLowerThreshold());
        a.setUpperThreshold(req.getUpperThreshold());
        a.setLastValue(value);
        a.setStatus(PlantAlertStatus.PENDING);
        a.setFirstDetectedAt(now);
        a.setLastSeenAt(now);
        a.setMessage(buildMessage(plant, fp, req, value));
        return a;
    }

    /** Resolve the live value for a requirement: flowerpot → zone → greenhouse, matched by name. */
    private Double resolveCurrentValue(Greenhouse gh, Zone zone, Flowerpot fp, String name) {
        Double v = findValue(fp.getParameters(), name);
        if (v != null) return v;
        v = findValue(zone.getParameters(), name);
        if (v != null) return v;
        return findValue(gh.getParameters(), name);
    }

    private Double findValue(List<ParameterEntity> params, String name) {
        if (params == null) return null;
        return params.stream()
                .filter(p -> p.getName() != null && p.getName().equalsIgnoreCase(name))
                .map(ParameterEntity::getCurrentValue)
                .filter(java.util.Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private boolean isOutOfRange(double value, Double lower, Double upper) {
        if (lower != null && value < lower) return true;
        return upper != null && value > upper;
    }

    private String buildMessage(Plant plant, Flowerpot fp, RequirementEntity req, double value) {
        String unit = req.getUnit() == null ? "" : req.getUnit();
        String where;
        if (req.getLowerThreshold() != null && value < req.getLowerThreshold()) {
            where = "below";
        } else {
            where = "above";
        }
        return String.format("%s in %s: %s is %.1f%s — %s the safe range %.1f–%.1f%s.",
                plant.getName(), fp.getName(), req.getName(), value, unit, where,
                req.getLowerThreshold() == null ? Double.NEGATIVE_INFINITY : req.getLowerThreshold(),
                req.getUpperThreshold() == null ? Double.POSITIVE_INFINITY : req.getUpperThreshold(),
                unit);
    }

    // ─── Query API ────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<PlantAlertDTO> getAlerts(Long greenhouseId, boolean includeResolved) {
        List<PlantAlert> rows = includeResolved
                ? alertRepository.findByGreenhouseIdAndStatusInOrderByLastSeenAtDesc(
                        greenhouseId, List.of(PlantAlertStatus.ACTIVE, PlantAlertStatus.RESOLVED))
                : alertRepository.findByGreenhouseIdAndStatusOrderByRaisedAtDesc(
                        greenhouseId, PlantAlertStatus.ACTIVE);
        return rows.stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public long countActive(Long greenhouseId) {
        return alertRepository.countByGreenhouseIdAndStatus(greenhouseId, PlantAlertStatus.ACTIVE);
    }

    @Transactional
    public void dismiss(Long alertId) {
        alertRepository.deleteById(alertId);
    }

    @Transactional
    public void purgeGreenhouse(Long greenhouseId) {
        alertRepository.deleteByGreenhouseId(greenhouseId);
    }

    private PlantAlertDTO toDto(PlantAlert a) {
        return new PlantAlertDTO(
                a.getId(), a.getGreenhouseId(), a.getGreenhouseName(),
                a.getFlowerpotId(), a.getFlowerpotName(),
                a.getPlantId(), a.getPlantName(),
                a.getRequirementName(), a.getParameterName(), a.getUnit(), a.getKind(),
                a.getLowerThreshold(), a.getUpperThreshold(), a.getLastValue(),
                a.getStatus().name(), a.getMessage(),
                a.getFirstDetectedAt(), a.getRaisedAt(), a.getResolvedAt(), a.getLastSeenAt());
    }
}
