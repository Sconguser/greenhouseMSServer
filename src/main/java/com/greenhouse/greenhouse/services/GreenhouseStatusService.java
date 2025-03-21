package com.greenhouse.greenhouse.services;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.greenhouse.greenhouse.exceptions.GreenhouseOffException;
import com.greenhouse.greenhouse.exceptions.GreenhouseStatusNotFoundException;
import com.greenhouse.greenhouse.exceptions.PlantRequirementsNotMetException;
import com.greenhouse.greenhouse.mappers.GreenhouseStatusMapper;
import com.greenhouse.greenhouse.models.Greenhouse;
import com.greenhouse.greenhouse.models.GreenhouseStatus;
import com.greenhouse.greenhouse.models.Status;
import com.greenhouse.greenhouse.repositories.GreenhouseStatusRepository;
import com.greenhouse.greenhouse.requests.GreenhouseStatusRequest;
import com.greenhouse.greenhouse.responses.GreenhouseStatusResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GreenhouseStatusService {
    private final GreenhouseStatusRepository greenhouseStatusRepository;
    private final GreenhouseManager greenhouseManager;
    private final NotificationService notificationService;
    private final GreenhouseStatusMapper greenhouseStatusMapper;


    public GreenhouseStatusService (GreenhouseStatusRepository greenhouseStatusRepository,
                                    GreenhouseManager greenhouseManager, NotificationService notificationService,
                                    GreenhouseStatusMapper greenhouseStatusMapper)
    {
        this.greenhouseStatusRepository = greenhouseStatusRepository;
        this.greenhouseManager = greenhouseManager;
        this.notificationService = notificationService;
        this.greenhouseStatusMapper = greenhouseStatusMapper;
    }

    @Deprecated
    public GreenhouseStatusResponse getGreenhouseStatus (Long id) {
        return greenhouseStatusRepository.findById(id)
                .map(greenhouseStatusMapper::toResponse)
                .orElseThrow(() -> new GreenhouseStatusNotFoundException("Greenhouse status not found in database"));
    }

    @Deprecated
    public void setTemperature (Long id, double temperature) {
        GreenhouseStatus greenhouseStatus = fetchGreenhouseStatus(id);
        greenhouseStatus.setTemperature(temperature);
        greenhouseStatusRepository.save(greenhouseStatus);
    }

    @Deprecated
    public void setHumidity (Long id, double humidity) {
        GreenhouseStatus greenhouseStatus = fetchGreenhouseStatus(id);
        greenhouseStatus.setHumidity(humidity);
        greenhouseStatusRepository.save(greenhouseStatus);
    }

    private GreenhouseStatus updateAndReturnGreenhouseStatus (Long id, GreenhouseStatusRequest greenhouseStatusRequest)
    {
        GreenhouseStatus greenhouseStatus = fetchGreenhouseStatus(id);
        Double requestHumidity = greenhouseStatusRequest.getHumidity();
        if (requestHumidity != null) {
            greenhouseStatus.setHumidity(requestHumidity);
        }
        Double requestTemperature = greenhouseStatusRequest.getTemperature();
        if (requestTemperature != null) {
            greenhouseStatus.setTemperature(requestTemperature);
        }
        Status requestStatus = greenhouseStatusRequest.getStatus();
        if (requestStatus != null) {
            greenhouseStatus.setStatus(requestStatus);
        }
        greenhouseStatusRepository.save(greenhouseStatus);
        return greenhouseStatus;
    }


    ///TODO: potencjalnie do wyrzucenia razem z innymi metodami tutaj
    @Deprecated
    public void setStatus (Long id, Status status) {
        GreenhouseStatus greenhouseStatus = fetchGreenhouseStatus(id);
        greenhouseStatus.setStatus(status);
        greenhouseStatusRepository.save(greenhouseStatus);
    }

    private GreenhouseStatus fetchGreenhouseStatus (Long id) {
        return greenhouseStatusRepository.findById(id)
                .orElseThrow(() -> new GreenhouseStatusNotFoundException("Greenhouse status not found in database"));
    }

    public void processGreenhouseStatus (Long greenhouseId, GreenhouseStatusRequest greenhouseStatusRequest)
    {
        Greenhouse greenhouse = greenhouseManager.getGreenhouseEntity(greenhouseId);
        GreenhouseStatus newStatus = updateAndReturnGreenhouseStatus(greenhouse.getStatus()
                .getId(), greenhouseStatusRequest);
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
