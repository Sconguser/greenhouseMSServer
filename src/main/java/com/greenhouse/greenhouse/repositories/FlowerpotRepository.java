package com.greenhouse.greenhouse.repositories;

import com.greenhouse.greenhouse.models.Flowerpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlowerpotRepository  extends JpaRepository<Flowerpot,Long> {
}
