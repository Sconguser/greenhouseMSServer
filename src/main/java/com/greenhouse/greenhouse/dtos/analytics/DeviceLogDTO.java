package com.greenhouse.greenhouse.dtos.analytics;

import java.time.LocalDateTime;

/**
 * Unified log line for the per-greenhouse "serial monitor" view. Carries both
 * device-emitted logs ({@code source = "DEVICE"}) and lifecycle events
 * ({@code source = "EVENT"}, i.e. BOOT/CRASH) so the client renders one timeline.
 */
public record DeviceLogDTO(
        Long id,
        String source,        // "DEVICE" | "EVENT"
        String level,         // "INFO" | "WARN" | "ERROR"
        String code,          // device code, or "BOOT" / "CRASH" for events
        String message,
        Integer freeHeap,     // device logs only; null for events
        Long deviceUptime,    // device logs only; null for events
        LocalDateTime timestamp
) {}
