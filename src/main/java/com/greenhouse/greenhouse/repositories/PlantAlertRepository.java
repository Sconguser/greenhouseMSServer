package com.greenhouse.greenhouse.repositories;

import com.greenhouse.greenhouse.models.PlantAlert;
import com.greenhouse.greenhouse.models.PlantAlertStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PlantAlertRepository extends JpaRepository<PlantAlert, Long> {

    /** The single open (PENDING or ACTIVE) breach for a plant/requirement, if any. */
    Optional<PlantAlert> findFirstByPlantIdAndFlowerpotIdAndRequirementNameAndStatusIn(
            Long plantId, Long flowerpotId, String requirementName, List<PlantAlertStatus> statuses);

    List<PlantAlert> findByGreenhouseIdAndStatusOrderByRaisedAtDesc(
            Long greenhouseId, PlantAlertStatus status);

    List<PlantAlert> findByGreenhouseIdAndStatusInOrderByLastSeenAtDesc(
            Long greenhouseId, List<PlantAlertStatus> statuses);

    long countByGreenhouseIdAndStatus(Long greenhouseId, PlantAlertStatus status);

    @Modifying
    @Query("DELETE FROM PlantAlert a WHERE a.status = 'RESOLVED' AND a.resolvedAt < :cutoff")
    int deleteResolvedBefore(@Param("cutoff") LocalDateTime cutoff);

    @Modifying
    @Query("DELETE FROM PlantAlert a WHERE a.greenhouseId = :greenhouseId")
    int deleteByGreenhouseId(@Param("greenhouseId") Long greenhouseId);
}
