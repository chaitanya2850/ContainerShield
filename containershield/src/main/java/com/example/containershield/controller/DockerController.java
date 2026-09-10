package com.example.containershield.controller;

import com.example.containershield.service.DockerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/docker/containers")
public class DockerController {
    private final DockerService dockerService;

    @Autowired
    DockerController(DockerService dockerService){
        this.dockerService = dockerService;
    }

    @GetMapping("/running")
    public ResponseEntity<?> getRunningContainers(){
        return ResponseEntity.ok().body(dockerService.getRunningContainers());
    }

    /*
    To be implemented later!!
    @GetMapping("/stopped")
    public ResponseEntity<?> getStoppedContainers(){
        return ResponseEntity.ok().body(dockerService.getContainers("-a --filter \"status=exited\""));
    }
    */

    @GetMapping("/all")
    public ResponseEntity<?> getAllContainers(){
        return ResponseEntity.ok().body(dockerService.getContainers("-a"));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> getAllContainers(@PathVariable String id){
        return ResponseEntity.ok().body(dockerService.getContainers("-a"));
    }

}
