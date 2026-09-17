package com.example.containershield.repository;

import com.example.containershield.entity.ScanHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScanHistoryRepository
        extends JpaRepository<ScanHistoryEntity, Long> {
}