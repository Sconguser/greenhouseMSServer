package com.greenhouse.greenhouse.repositories;

import com.greenhouse.greenhouse.models.GreenhouseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GreenhouseStatusRepository extends JpaRepository<GreenhouseStatus, Long> {
}
