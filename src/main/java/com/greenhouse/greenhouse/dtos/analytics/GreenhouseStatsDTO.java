package com.greenhouse.greenhouse.dtos.analytics;

import java.time.LocalDateTime;

public record GreenhouseStatsDTO(
        Long greenhouseId,
        long bootCount,
        long crashCount,
        LocalDateTime from,
        LocalDateTime to
) {}