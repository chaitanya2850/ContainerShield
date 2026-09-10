package com.example.containershield.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DockerImageEntity {
    private String id;
    private String name;
    private String diskUsage;
    private String contentSize;
}
