package com.greenhouse.greenhouse.repositories;

import com.greenhouse.greenhouse.models.GreenhouseEvent;
import com.greenhouse.greenhouse.models.GreenhouseEventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface GreenhouseEventRepository extends JpaRepository<GreenhouseEvent, Long> {

    List<GreenhouseEvent> findByGreenhouseIdAndOccurredAtBetweenOrderByOccurredAtDesc(
            Long greenhouseId, LocalDateTime from, LocalDateTime to);

    long countByGreenhouseIdAndEventTypeAndOccurredAtBetween(
            Long greenhouseId, GreenhouseEventType eventType,
            LocalDateTime from, LocalDateTime to);

    @Modifying
    @Query("DELETE FROM GreenhouseEvent e WHERE e.occurredAt < :cutoff")
    int deleteByOccurredAtBefore(@Param("cutoff") LocalDateTime cutoff);

    @Modifying
    @Query("DELETE FROM GreenhouseEvent e WHERE e.greenhouse.id = :greenhouseId")
    int deleteByGreenhouseId(@Param("greenhouseId") Long greenhouseId);
}