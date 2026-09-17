package com.example.containershield.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ScanHistoryDetailsDTO {

    private Long id;
    private String imageName;
    private int totalVulnerabilities;
    private int critical;
    private int high;
    private int medium;
    private int low;
    private int unknown;
    private LocalDateTime scanTime;
    private List<ScanVulnerabilityDTO> vulnerabilities;
}