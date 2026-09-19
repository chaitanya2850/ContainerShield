package com.example.containershield.repository;

import com.example.containershield.entity.SourceCodeFinding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SourceCodeFindingRepository extends JpaRepository<SourceCodeFinding, Long> {
    List<SourceCodeFinding> findByProjectName(String projectName);
}