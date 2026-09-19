package com.example.containershield.controller;

import com.example.containershield.dto.DockerImageDTO;
import com.example.containershield.service.DockerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/docker")
public class DockerController {
    private final DockerService dockerService;

    @Autowired
    DockerController(DockerService dockerService){
        this.dockerService = dockerService;
    }

    //Docker Images
    @GetMapping("/images")
    public List<DockerImageDTO> getImages() throws Exception {
        return dockerService.getDockerImages();
    }

    //Running Containers
    @GetMapping("containers/running")
    public ResponseEntity<?> getRunningContainers(){
        return ResponseEntity.ok().body(dockerService.getRunningContainers());
    }

    //All Containers
    @GetMapping("containers/all")
    public ResponseEntity<?> getAllContainers(){
        return ResponseEntity.ok().body(dockerService.getContainers("-a"));
    }
}
