package com.greenhouse.greenhouse.repositories;

import com.greenhouse.greenhouse.models.AnalyticsSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalyticsSettingsRepository extends JpaRepository<AnalyticsSettings, Long> {
    // Fetch the singleton: analyticsSettingsRepository.findById(1L)
}