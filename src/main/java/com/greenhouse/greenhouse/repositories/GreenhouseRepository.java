package com.greenhouse.greenhouse.repositories;

import com.greenhouse.greenhouse.models.Greenhouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GreenhouseRepository extends JpaRepository<Greenhouse, Long> {
    Optional<Greenhouse> findByIpAddress(String ipAddress);
}
