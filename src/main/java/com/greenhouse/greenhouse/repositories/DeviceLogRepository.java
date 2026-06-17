package com.greenhouse.greenhouse.repositories;

import com.greenhouse.greenhouse.models.DeviceLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DeviceLogRepository extends JpaRepository<DeviceLog, Long> {

    /** Newest-first logs for a greenhouse after {@code since} (paged to cap the result size). */
    List<DeviceLog> findByGreenhouseIdAndReceivedAtAfterOrderByReceivedAtDesc(
            Long greenhouseId, LocalDateTime since, Pageable pageable);

    long countByGreenhouseId(Long greenhouseId);

    /**
     * Newest-first logs for a greenhouse (paged). Used by the row-cap cleanup to
     * locate the timestamp of the Nth-newest row.
     */
    List<DeviceLog> findByGreenhouseIdOrderByReceivedAtDesc(Long greenhouseId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM DeviceLog d WHERE d.receivedAt < :cutoff")
    int deleteByReceivedAtBefore(@Param("cutoff") LocalDateTime cutoff);

    @Modifying
    @Query("DELETE FROM DeviceLog d WHERE d.greenhouseId = :greenhouseId AND d.receivedAt < :cutoff")
    int deleteByGreenhouseIdAndReceivedAtBefore(@Param("greenhouseId") Long greenhouseId,
                                                @Param("cutoff") LocalDateTime cutoff);

    @Modifying
    @Query("DELETE FROM DeviceLog d WHERE d.greenhouseId = :greenhouseId")
    int deleteByGreenhouseId(@Param("greenhouseId") Long greenhouseId);
}
