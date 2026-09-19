package com.example.containershield.service;

import com.example.containershield.entity.SourceCodeFinding;
import com.example.containershield.entity.VulnerabilityFinding;
import com.example.containershield.repository.SourceCodeFindingRepository;
import com.example.containershield.repository.VulnerabilityFindingRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    private final VulnerabilityFindingRepository vulnerabilityFindingRepository;
    private final SourceCodeFindingRepository sourceCodeFindingRepository;
    private final RestTemplate restTemplate;

    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private static final String MODEL = "qwen2.5-coder:7b";

    public ChatService(
            VulnerabilityFindingRepository vulnerabilityFindingRepository,
            SourceCodeFindingRepository sourceCodeFindingRepository,
            RestTemplate restTemplate) {
        this.vulnerabilityFindingRepository = vulnerabilityFindingRepository;
        this.sourceCodeFindingRepository = sourceCodeFindingRepository;
        this.restTemplate = restTemplate;
    }

    public String answerQuestion(String projectOrImageName, String question) {

        List<VulnerabilityFinding> dependencyFindings =
                vulnerabilityFindingRepository.findByImageName(projectOrImageName);

        List<SourceCodeFinding> sourceFindings =
                sourceCodeFindingRepository.findByProjectName(projectOrImageName);

        String dependencyContext = buildDependencyContext(dependencyFindings);
        String sourceContext = buildSourceContext(sourceFindings);

        String prompt = """
                You are a security assistant helping a developer understand and fix vulnerabilities in their project.
                Use only the information provided below. Be specific — mention exact file names, line numbers, package names, and versions where available.
                Do not skip the dependency vulnerabilities section — include it in your answer alongside the source code findings.
                
                === DEPENDENCY & IMAGE VULNERABILITIES (%d total) ===
                %s
                
                === SOURCE CODE FINDINGS (%d total) ===
                %s
                
                Developer's question: %s
                """.formatted(
                dependencyFindings.size(),
                dependencyContext,
                sourceFindings.size(),
                sourceContext,
                question
        );

        return callOllama(prompt);
    }

    private String buildDependencyContext(List<VulnerabilityFinding> findings) {
        if (findings.isEmpty()) {
            return "None found.";
        }
        StringBuilder sb = new StringBuilder();
        for (VulnerabilityFinding f : findings) {
            sb.append("- ").append(f.getPackageName())
                    .append(" (").append(f.getSeverity()).append("): ")
                    .append("installed ").append(f.getInstalledVersion())
                    .append(" → fix: ").append(
                            f.getFixedVersion() == null || f.getFixedVersion().isBlank()
                                    ? "no fix available yet"
                                    : f.getFixedVersion())
                    .append("\n");
        }
        return sb.toString();
    }

    private String buildSourceContext(List<SourceCodeFinding> findings) {
        if (findings.isEmpty()) {
            return "None found.";
        }
        StringBuilder sb = new StringBuilder();
        for (SourceCodeFinding f : findings) {
            sb.append("- File: ").append(f.getFilePath())
                    .append(", Line: ").append(f.getStartLine())
                    .append(", Severity: ").append(f.getSeverity())
                    .append(", Issue: ").append(f.getMessage())
                    .append("\n");
        }
        return sb.toString();
    }

    private String callOllama(String prompt) {

        Map<String, Object> requestBody = Map.of(
                "model", MODEL,
                "prompt", prompt,
                "stream", false,
                "options", Map.of("num_ctx", 8192)
        );

        Map<String, Object> response = restTemplate.postForObject(
                OLLAMA_URL, requestBody, Map.class
        );

        return response != null ? (String) response.get("response") : "No response from model.";
    }
}