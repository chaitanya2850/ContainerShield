package com.example.containershield.controller;

import com.example.containershield.dto.TrivyVulnerability;
import com.example.containershield.service.TrivyService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trivy")
public class TrivyController {

    private final TrivyService trivyService;

    public TrivyController(TrivyService trivyService) {
        this.trivyService = trivyService;
    }

    @GetMapping("/scan")
    public ResponseEntity<List<TrivyVulnerability>> scanImage(
            @RequestParam String image) {

        return ResponseEntity.ok(
                trivyService.scanImage(image)
        );
    }
}