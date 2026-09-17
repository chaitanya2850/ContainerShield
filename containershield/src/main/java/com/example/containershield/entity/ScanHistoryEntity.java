package com.example.containershield.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "scan_history")
public class ScanHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageName;

    private int totalVulnerabilities;

    private int critical;

    private int high;

    private int medium;

    private int low;

    private int unknown;

    private LocalDateTime scanTime;

    @OneToMany(
            mappedBy = "scan",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ScanVulnerabilityEntity> vulnerabilities = new ArrayList<>();
}