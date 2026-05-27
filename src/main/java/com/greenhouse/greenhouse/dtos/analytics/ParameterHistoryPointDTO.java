package com.greenhouse.greenhouse.dtos.analytics;

import java.time.LocalDateTime;

public record ParameterHistoryPointDTO(
        LocalDateTime timestamp,
        Double value
) {}