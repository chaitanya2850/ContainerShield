package com.example.containershield.controller;

import com.example.containershield.dto.ScanHistoryDetailsDTO;
import com.example.containershield.dto.ScanHistorySummary;
import com.example.containershield.dto.ScanVulnerabilityDTO;
import com.example.containershield.service.ScanHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scans")
public class ScanHistoryController {

    private final ScanHistoryService scanHistoryService;

    public ScanHistoryController(
            ScanHistoryService scanHistoryService) {

        this.scanHistoryService = scanHistoryService;
    }

    @GetMapping
    public ResponseEntity<List<ScanHistorySummary>> getAllScans() {

        List<ScanHistorySummary> scans =
                scanHistoryService.getAllScans()
                        .stream()
                        .map(scan -> new ScanHistorySummary(
                                scan.getId(),
                                scan.getImageName(),
                                scan.getTotalVulnerabilities(),
                                scan.getCritical(),
                                scan.getHigh(),
                                scan.getMedium(),
                                scan.getLow(),
                                scan.getUnknown(),
                                scan.getScanTime()
                        ))
                        .toList();

        return ResponseEntity.ok(scans);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScanHistoryDetailsDTO> getScan(
            @PathVariable Long id) {

        var scan = scanHistoryService.getScan(id);

        List<ScanVulnerabilityDTO> vulnerabilities =
                scan.getVulnerabilities()
                        .stream()
                        .map(vulnerability ->
                                new ScanVulnerabilityDTO(
                                        vulnerability.getVulnerabilityId(),
                                        vulnerability.getPackageName(),
                                        vulnerability.getInstalledVersion(),
                                        vulnerability.getFixedVersion(),
                                        vulnerability.getSeverity(),
                                        vulnerability.getTitle(),
                                        vulnerability.getDescription()
                                )
                        )
                        .toList();

        ScanHistoryDetailsDTO response =
                new ScanHistoryDetailsDTO(
                        scan.getId(),
                        scan.getImageName(),
                        scan.getTotalVulnerabilities(),
                        scan.getCritical(),
                        scan.getHigh(),
                        scan.getMedium(),
                        scan.getLow(),
                        scan.getUnknown(),
                        scan.getScanTime(),
                        vulnerabilities
                );

        return ResponseEntity.ok(response);
    }
}