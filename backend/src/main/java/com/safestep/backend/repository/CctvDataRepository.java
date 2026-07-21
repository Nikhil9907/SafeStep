package com.safestep.backend.repository;

import com.safestep.backend.model.CctvData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CctvDataRepository extends JpaRepository<CctvData, Long> {
    List<CctvData> findByStatus(String status);
}
