package com.greenhouse.greenhouse.services;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.greenhouse.greenhouse.exceptions.GreenhouseOffException;
import com.greenhouse.greenhouse.exceptions.GreenhouseStatusNotFoundException;
import com.greenhouse.greenhouse.exceptions.PlantRequirementsNotMetException;
import com.greenhouse.greenhouse.mappers.ZoneStatusMapper;
import com.greenhouse.greenhouse.models.Greenhouse;
import com.greenhouse.greenhouse.models.Status;
import com.greenhouse.greenhouse.repositories.ZoneStatusRepository;
import com.greenhouse.greenhouse.requests.ZoneStatusRequest;
import com.greenhouse.greenhouse.responses.ZoneStatusResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ZoneStatusService {
    private final ZoneStatusRepository zoneStatusRepository;
    private final GreenhouseManager greenhouseManager;
    private final NotificationService notificationService;
    private final ZoneStatusMapper zoneStatusMapper;


    public ZoneStatusService (ZoneStatusRepository zoneStatusRepository,
                              GreenhouseManager greenhouseManager, NotificationService notificationService,
                              ZoneStatusMapper zoneStatusMapper)
    {
        this.zoneStatusRepository = zoneStatusRepository;
        this.greenhouseManager = greenhouseManager;
        this.notificationService = notificationService;
        this.zoneStatusMapper = zoneStatusMapper;
    }

    @Deprecated
    public ZoneStatusResponse getGreenhouseStatus (Long id) {
        return zoneStatusRepository.findById(id)
                .map(zoneStatusMapper::toResponse)
                .orElseThrow(() -> new GreenhouseStatusNotFoundException("Greenhouse status not found in database"));
    }

    @Deprecated
    public void setTemperature (Long id, double temperature) {
        GreenhouseStatus greenhouseStatus = fetchGreenhouseStatus(id);
        greenhouseStatus.setTemperature(temperature);
        zoneStatusRepository.save(greenhouseStatus);
    }

    @Deprecated
    public void setHumidity (Long id, double humidity) {
        GreenhouseStatus greenhouseStatus = fetchGreenhouseStatus(id);
        greenhouseStatus.setHumidity(humidity);
        zoneStatusRepository.save(greenhouseStatus);
    }

    private GreenhouseStatus updateAndReturnGreenhouseStatus (Long id, ZoneStatusRequest zoneStatusRequest)
    {
        GreenhouseStatus greenhouseStatus = fetchGreenhouseStatus(id);
        Double requestHumidity = zoneStatusRequest.getHumidity();
        if (requestHumidity != null) {
            greenhouseStatus.setHumidity(requestHumidity);
        }
        Double requestTemperature = zoneStatusRequest.getTemperature();
        if (requestTemperature != null) {
            greenhouseStatus.setTemperature(requestTemperature);
        }
        Double requestSoilHumidity = zoneStatusRequest.getSoilHumidity();
        if (requestSoilHumidity != null) {
            greenhouseStatus.setSoilHumidity(requestSoilHumidity);
        }
        Status requestStatus = zoneStatusRequest.getStatus();
        if (requestStatus != null) {
            greenhouseStatus.setStatus(requestStatus);
        }
        zoneStatusRepository.save(greenhouseStatus);
        return greenhouseStatus;
    }


    ///TODO: potencjalnie do wyrzucenia razem z innymi metodami tutaj
    @Deprecated
    public void setStatus (Long id, Status status) {
        GreenhouseStatus greenhouseStatus = fetchGreenhouseStatus(id);
        greenhouseStatus.setStatus(status);
        zoneStatusRepository.save(greenhouseStatus);
    }

    private GreenhouseStatus fetchGreenhouseStatus (Long id) {
        return zoneStatusRepository.findById(id)
                .orElseThrow(() -> new GreenhouseStatusNotFoundException("Greenhouse status not found in database"));
    }

    public void processGreenhouseStatus (Long greenhouseId, ZoneStatusRequest zoneStatusRequest)
    {
        Greenhouse greenhouse = greenhouseManager.getGreenhouseEntity(greenhouseId);
        GreenhouseStatus newStatus = updateAndReturnGreenhouseStatus(greenhouse.getStatus()
                .getId(), zoneStatusRequest);
        if (Status.OFF.equals(newStatus.getStatus())) {
            try {
                notificationService.sendNotification("Something wrong with greenhouse",
                        new GreenhouseOffException("Greenhouse is off").getMessage(), List.of(""));
            } catch (FirebaseMessagingException e) {
                /// TODO implement custom exception
                throw new RuntimeException("Firebase messaging expception");
            }
        }
        try {
            greenhouseManager.checkPlantRequirements(greenhouse, newStatus);
        } catch (PlantRequirementsNotMetException e) {
            try {

                notificationService.sendNotification("Something wrong with greenhouse", e.getMessage(), List.of(""));
            } catch (FirebaseMessagingException ex) {
                throw new RuntimeException("Firebase messaging expception");

            }
        }

    }
}
