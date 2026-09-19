package com.example.containershield.service;

import com.example.containershield.dto.SemgrepFinding;
import com.example.containershield.entity.SourceCodeFinding;
import com.example.containershield.repository.SourceCodeFindingRepository;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SemgrepService {

    private final ObjectMapper objectMapper;
    private final SourceCodeFindingRepository findingRepository;

    public SemgrepService(ObjectMapper objectMapper, SourceCodeFindingRepository findingRepository) {
        this.objectMapper = objectMapper;
        this.findingRepository = findingRepository;
    }

    public List<SemgrepFinding> scanProject(String projectPath, String projectName) {

        List<SemgrepFinding> findings = new ArrayList<>();

        try {

            ProcessBuilder processBuilder = new ProcessBuilder(
                    "docker",
                    "run",
                    "--rm",

                    "-v",
                    projectPath + ":/src",

                    "semgrep/semgrep",

                    "semgrep",
                    "--config=p/java",
                    "--json",
                    "/src"
            );

            Process process = processBuilder.start();

            BufferedReader outputReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );
            BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream())
            );

            StringBuilder output = new StringBuilder();
            StringBuilder errorOutput = new StringBuilder();

            String line;

            while ((line = outputReader.readLine()) != null) {
                output.append(line).append("\n");
            }

            while ((line = errorReader.readLine()) != null) {
                errorOutput.append(line).append("\n");
            }

            int exitCode = process.waitFor();

            // Semgrep exits with code 1 when it finds blocking issues — that's expected, not a failure
            if (exitCode != 0 && exitCode != 1) {
                throw new RuntimeException("Semgrep scan failed: " + errorOutput);
            }

            JsonNode root = objectMapper.readTree(output.toString());
            JsonNode results = root.get("results");

            if (results == null || !results.isArray()) {
                return findings;
            }

            LocalDateTime scanTime = LocalDateTime.now();

            for (JsonNode result : results) {

                JsonNode extra = result.get("extra");
                JsonNode metadata = extra != null ? extra.get("metadata") : null;
                JsonNode vulnClassArray = metadata != null ? metadata.get("vulnerability_class") : null;

                String vulnerabilityClass = (vulnClassArray != null && vulnClassArray.isArray() && !vulnClassArray.isEmpty())
                        ? vulnClassArray.get(0).asString()
                        : "";

                SemgrepFinding finding = new SemgrepFinding(
                        result.path("check_id").asString(),
                        result.path("path").asString(),
                        result.path("start").path("line").asInt(),
                        result.path("end").path("line").asInt(),
                        extra != null ? extra.path("message").asString() : "",
                        extra != null ? extra.path("severity").asString() : "",
                        vulnerabilityClass
                );

                findings.add(finding);

                findingRepository.save(new SourceCodeFinding(
                        null,
                        projectName,
                        scanTime,
                        finding.getCheckId(),
                        finding.getFilePath(),
                        finding.getStartLine(),
                        finding.getEndLine(),
                        finding.getMessage(),
                        finding.getSeverity(),
                        finding.getVulnerabilityClass()
                ));
            }

            return findings;

        } catch (Exception e) {
            throw new RuntimeException("Failed to scan project: " + projectName, e);
        }
    }
}