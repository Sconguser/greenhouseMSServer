package com.greenhouse.greenhouse.repositories;

import com.greenhouse.greenhouse.models.Greenhouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GreenhouseRepository extends JpaRepository<Greenhouse, Long> {
}
