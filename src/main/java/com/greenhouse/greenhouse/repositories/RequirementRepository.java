package com.greenhouse.greenhouse.repositories;

import com.greenhouse.greenhouse.models.RequirementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequirementRepository extends JpaRepository<RequirementEntity, Long> {
}
