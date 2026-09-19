package com.example.containershield.controller;

import com.example.containershield.dto.FixSuggestion;
import com.example.containershield.dto.TrivyVulnerability;
import com.example.containershield.entity.ScanHistoryEntity;
import com.example.containershield.service.ManifestMatcherService;
import com.example.containershield.service.ScanHistoryService;
import com.example.containershield.service.TrivyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/trivy")
public class TrivyController {

    private final TrivyService trivyService;
    private final ScanHistoryService scanHistoryService;
    private final ManifestMatcherService manifestMatcherService;

    public TrivyController(
            TrivyService trivyService,
            ScanHistoryService scanHistoryService,
            ManifestMatcherService manifestMatcherService) {
        this.manifestMatcherService = manifestMatcherService;
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

    @PostMapping("/scan-with-manifest")
    public ResponseEntity<List<FixSuggestion>> scanWithManifest(
            @RequestParam String image,
            @RequestParam("manifest") MultipartFile manifestFile) throws IOException {

        List<TrivyVulnerability> findings = trivyService.scanImage(image);

        String manifestContent = new String(manifestFile.getBytes());

        List<FixSuggestion> suggestions =
                manifestMatcherService.matchAgainstPomXml(manifestContent, findings);

        return ResponseEntity.ok(suggestions);
    }
}