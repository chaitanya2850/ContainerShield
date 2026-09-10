package com.example.containershield.service;

import com.example.containershield.dto.TrivyVulnerability;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class TrivyService {

    private final ObjectMapper objectMapper;

    public List<TrivyVulnerability> scanImage(String imageName) {

        List<TrivyVulnerability> vulnerabilities = new ArrayList<>();

        try {

            ProcessBuilder processBuilder = new ProcessBuilder(
                    "trivy",
                    "image",
                    "--format",
                    "json",
                    imageName
            );

            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            StringBuilder output = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new RuntimeException("Trivy scan failed");
            }

            JsonNode root = objectMapper.readTree(output.toString());

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

                    String fixedVersion = "";
                    System.out.println(vulnerability.toPrettyString());
                    if (vulnerability.has("FixedVersion")) {
                        fixedVersion =
                                vulnerability.get("FixedVersion").asString();
                    }

                    vulnerabilities.add(
                            new TrivyVulnerability(
                                    vulnerability.path("VulnerabilityID").asString(),
                                    vulnerability.path("PkgName").asString(),
                                    vulnerability.path("InstalledVersion").asString(),
                                    vulnerability.path("FixedVersion").asString(),
                                    vulnerability.path("Severity").asString(),
                                    vulnerability.path("Title").asString(),
                                    vulnerability.path("Description").asString()
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