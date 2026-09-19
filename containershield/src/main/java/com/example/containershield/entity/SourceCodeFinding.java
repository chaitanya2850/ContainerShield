package com.example.containershield.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "source_code_findings")
public class SourceCodeFinding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String projectName;   // which project/scan this belongs to
    private LocalDateTime scannedAt;

    private String checkId;
    private String filePath;
    private int startLine;
    private int endLine;

    @Column(columnDefinition = "TEXT")
    private String message;

    private String severity;
    private String vulnerabilityClass;
}