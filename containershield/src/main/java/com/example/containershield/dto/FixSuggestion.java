package com.example.containershield.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixSuggestion {
    private String packageName;
    private String currentVersionInManifest;
    private String vulnerableVersionDetected;
    private String fixedVersion;
    private String severity;
    private boolean foundInManifest; // true = direct dependency match, false = likely transitive
}