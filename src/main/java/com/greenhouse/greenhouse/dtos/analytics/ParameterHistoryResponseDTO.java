package com.greenhouse.greenhouse.dtos.analytics;

import java.util.List;

public record ParameterHistoryResponseDTO(
        Long parameterId,
        String parameterName,
        String unit,
        List<ParameterHistoryPointDTO> points
) {}