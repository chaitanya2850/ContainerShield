package com.example.containershield.service;

import com.example.containershield.dto.TrivyVulnerability;
import com.example.containershield.entity.ScanHistoryEntity;
import com.example.containershield.entity.ScanVulnerabilityEntity;
import com.example.containershield.repository.ScanHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScanHistoryService {

    private final ScanHistoryRepository scanHistoryRepository;

    public ScanHistoryService(ScanHistoryRepository scanHistoryRepository) {
        this.scanHistoryRepository = scanHistoryRepository;
    }

    public ScanHistoryEntity saveScan(
            String imageName,
            List<TrivyVulnerability> vulnerabilities) {

        ScanHistoryEntity scan = new ScanHistoryEntity();

        scan.setImageName(imageName);
        scan.setScanTime(LocalDateTime.now());
        scan.setTotalVulnerabilities(vulnerabilities.size());

        int critical = 0;
        int high = 0;
        int medium = 0;
        int low = 0;
        int unknown = 0;

        for (TrivyVulnerability vulnerability : vulnerabilities) {

            String severity = vulnerability.getSeverity();

            if (severity == null) {
                unknown++;
            } else {
                switch (severity.toUpperCase()) {
                    case "CRITICAL" -> critical++;
                    case "HIGH" -> high++;
                    case "MEDIUM" -> medium++;
                    case "LOW" -> low++;
                    default -> unknown++;
                }
            }

            ScanVulnerabilityEntity vulnerabilityEntity =
                    new ScanVulnerabilityEntity();

            vulnerabilityEntity.setVulnerabilityId(
                    vulnerability.getVulnerabilityID());

            vulnerabilityEntity.setPackageName(
                    vulnerability.getPackageName());

            vulnerabilityEntity.setInstalledVersion(
                    vulnerability.getInstalledVersion());

            vulnerabilityEntity.setFixedVersion(
                    vulnerability.getFixedVersion());

            vulnerabilityEntity.setSeverity(
                    vulnerability.getSeverity());

            vulnerabilityEntity.setTitle(
                    vulnerability.getTitle());

            vulnerabilityEntity.setDescription(
                    vulnerability.getDescription());

            vulnerabilityEntity.setScan(scan);

            scan.getVulnerabilities().add(vulnerabilityEntity);
        }

        scan.setCritical(critical);
        scan.setHigh(high);
        scan.setMedium(medium);
        scan.setLow(low);
        scan.setUnknown(unknown);

        return scanHistoryRepository.save(scan);
    }

    public List<ScanHistoryEntity> getAllScans() {
        return scanHistoryRepository.findAll();
    }

    public ScanHistoryEntity getScan(Long id) {
        return scanHistoryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Scan not found: " + id));
    }
}