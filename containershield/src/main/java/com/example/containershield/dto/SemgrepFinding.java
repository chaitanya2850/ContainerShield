package com.example.containershield.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SemgrepFinding {
    private String checkId;
    private String filePath;
    private int startLine;
    private int endLine;
    private String message;
    private String severity;
    private String vulnerabilityClass;
}