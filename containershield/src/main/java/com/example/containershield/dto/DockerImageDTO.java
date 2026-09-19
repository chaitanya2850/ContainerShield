package com.example.containershield.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DockerImageDTO {

    private String id;
    private String name;
    private String tag;
    private String size;
    private String created;
}