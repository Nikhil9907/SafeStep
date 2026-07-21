package com.safestep.backend.repository;

import com.safestep.backend.model.CrimeData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrimeDataRepository extends JpaRepository<CrimeData, Long> {
}
