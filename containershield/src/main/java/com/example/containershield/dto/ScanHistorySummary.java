package com.example.containershield.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ScanHistorySummary {

    private Long id;
    private String imageName;
    private int totalVulnerabilities;
    private int critical;
    private int high;
    private int medium;
    private int low;
    private int unknown;
    private LocalDateTime scanTime;
}