package com.greenhouse.greenhouse.repositories;

import com.greenhouse.greenhouse.models.ZoneStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZoneStatusRepository extends JpaRepository<ZoneStatus, Long> {
}
