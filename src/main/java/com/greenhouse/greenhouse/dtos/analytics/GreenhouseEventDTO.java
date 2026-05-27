package com.greenhouse.greenhouse.dtos.analytics;

import java.time.LocalDateTime;

public record GreenhouseEventDTO(
        Long id,
        Long greenhouseId,
        String eventType,      // "BOOT" | "CRASH"
        LocalDateTime occurredAt,
        String details
) {}