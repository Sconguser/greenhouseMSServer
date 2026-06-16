package com.greenhouse.greenhouse.repositories;

import com.greenhouse.greenhouse.models.ParameterHistoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ParameterHistoryRepository extends JpaRepository<ParameterHistoryEntry, Long> {

    List<ParameterHistoryEntry> findByParameterIdAndRecordedAtBetweenOrderByRecordedAtAsc(
            Long parameterId, LocalDateTime from, LocalDateTime to);

    long countByParameterIdAndRecordedAtBetween(
            Long parameterId, LocalDateTime from, LocalDateTime to);

    @Modifying
    @Query("DELETE FROM ParameterHistoryEntry p WHERE p.recordedAt < :cutoff")
    int deleteByRecordedAtBefore(@Param("cutoff") LocalDateTime cutoff);

    @Modifying
    @Query("DELETE FROM ParameterHistoryEntry p WHERE p.greenhouseId = :greenhouseId")
    int deleteByGreenhouseId(@Param("greenhouseId") Long greenhouseId);
}