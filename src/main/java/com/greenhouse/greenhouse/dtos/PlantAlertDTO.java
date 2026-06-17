package com.greenhouse.greenhouse.dtos;

import com.greenhouse.greenhouse.models.RequirementKind;

import java.time.LocalDateTime;

public record PlantAlertDTO(
        Long id,
        Long greenhouseId,
        String greenhouseName,
        Long flowerpotId,
        String flowerpotName,
        Long plantId,
        String plantName,
        String requirementName,
        String parameterName,
        String unit,
        RequirementKind kind,
        Double lowerThreshold,
        Double upperThreshold,
        Double lastValue,
        String status,
        String message,
        LocalDateTime firstDetectedAt,
        LocalDateTime raisedAt,
        LocalDateTime resolvedAt,
        LocalDateTime lastSeenAt
) {}
