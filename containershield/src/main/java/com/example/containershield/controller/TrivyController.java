package com.example.containershield.controller;

import com.example.containershield.dto.TrivyVulnerability;
import com.example.containershield.entity.ScanHistoryEntity;
import com.example.containershield.service.ScanHistoryService;
import com.example.containershield.service.TrivyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trivy")
public class TrivyController {

    private final TrivyService trivyService;
    private final ScanHistoryService scanHistoryService;

    public TrivyController(
            TrivyService trivyService,
            ScanHistoryService scanHistoryService) {

        this.trivyService = trivyService;
        this.scanHistoryService = scanHistoryService;
    }

    @GetMapping("/scan")
    public ResponseEntity<List<TrivyVulnerability>> scanImage(
            @RequestParam String image) {

        // Run Trivy scan
        List<TrivyVulnerability> vulnerabilities =
                trivyService.scanImage(image);

        // Save scan result to PostgreSQL
        scanHistoryService.saveScan(
                image,
                vulnerabilities
        );

        // Return vulnerabilities to the client
        return ResponseEntity.ok(vulnerabilities);
    }
}