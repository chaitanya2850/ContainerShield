package com.example.containershield.controller;

import com.example.containershield.dto.SemgrepFinding;
import com.example.containershield.service.SemgrepService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/semgrep")
public class SemgrepController {

    private final SemgrepService semgrepService;

    public SemgrepController(SemgrepService semgrepService) {
        this.semgrepService = semgrepService;
    }

    @GetMapping("/scan")
    public ResponseEntity<List<SemgrepFinding>> scanProject(
            @RequestParam String path,
            @RequestParam String projectName) {

        return ResponseEntity.ok(semgrepService.scanProject(path, projectName));
    }
}