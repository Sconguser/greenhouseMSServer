package com.greenhouse.greenhouse.repositories;

import com.greenhouse.greenhouse.models.ParameterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParameterRepository extends JpaRepository<ParameterEntity, Long> {
    List<ParameterEntity> findByGreenhouseId (Long greenhouseId);

    List<ParameterEntity> findByZoneId (Long zoneId);

    List<ParameterEntity> findByFlowerpotId (Long flowerpotId);

}
