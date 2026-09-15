package com.example.containershield.service;

import com.example.containershield.dto.TrivyVulnerability;

import java.util.List;

public interface ContainerScanner {

    List<TrivyVulnerability> scanImage(String imageName);

}