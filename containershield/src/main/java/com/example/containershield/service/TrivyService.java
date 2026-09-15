package com.example.containershield.service;

import com.example.containershield.dto.TrivyVulnerability;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class TrivyService implements ContainerScanner {

    private final ObjectMapper objectMapper;

    @Value("${trivy.server.url}")
    private String trivyServerUrl;

    public TrivyService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public List<TrivyVulnerability> scanImage(String imageName) {

        List<TrivyVulnerability> vulnerabilities = new ArrayList<>();

        try {

            ProcessBuilder processBuilder = new ProcessBuilder(
                    "docker",
                    "run",
                    "--rm",

                    "--network",
                    "containershield-network",

                    "-v",
                    "/var/run/docker.sock:/var/run/docker.sock",

                    "aquasec/trivy:0.74.0",

                    "image",

                    "--docker-host",
                    "unix:///var/run/docker.sock",

                    "--server",
                    trivyServerUrl,

                    "--format",
                    "json",

                    imageName
            );

            Process process = processBuilder.start();

            // Read Trivy JSON output from stdout
            BufferedReader outputReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            // Read Trivy logs/errors separately from stderr
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

            // If Trivy failed, show the actual error
            if (exitCode != 0) {
                throw new RuntimeException(
                        "Trivy scan failed: " + errorOutput
                );
            }

            // Parse only stdout because it contains the JSON
            JsonNode root = objectMapper.readTree(
                    output.toString()
            );

            JsonNode results = root.get("Results");

            if (results == null || !results.isArray()) {
                return vulnerabilities;
            }

            for (JsonNode result : results) {

                JsonNode vulnerabilityList =
                        result.get("Vulnerabilities");

                if (vulnerabilityList == null ||
                        !vulnerabilityList.isArray()) {
                    continue;
                }

                for (JsonNode vulnerability : vulnerabilityList) {

                    vulnerabilities.add(
                            new TrivyVulnerability(
                                    vulnerability
                                            .path("VulnerabilityID")
                                            .asString(),

                                    vulnerability
                                            .path("PkgName")
                                            .asString(),

                                    vulnerability
                                            .path("InstalledVersion")
                                            .asString(),

                                    vulnerability
                                            .path("FixedVersion")
                                            .asString(),

                                    vulnerability
                                            .path("Severity")
                                            .asString(),

                                    vulnerability
                                            .path("Title")
                                            .asString(),

                                    vulnerability
                                            .path("Description")
                                            .asString()
                            )
                    );
                }
            }

            return vulnerabilities;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to scan Docker image: " + imageName,
                    e
            );
        }
    }
}
