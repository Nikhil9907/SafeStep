package com.safestep.backend.repository;

import com.safestep.backend.model.LightingData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LightingDataRepository extends JpaRepository<LightingData, Long> {
    List<LightingData> findByStatus(String status);
}
