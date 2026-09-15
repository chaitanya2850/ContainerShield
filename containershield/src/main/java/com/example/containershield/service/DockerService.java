package com.example.containershield.service;

import com.example.containershield.entity.DockerContainerEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class DockerService {
    ObjectMapper objectMapper;

    public List<DockerContainerEntity> getRunningContainers() {

        List<DockerContainerEntity> containers = new ArrayList<>();

        try {

            ProcessBuilder processBuilder = new ProcessBuilder(
                    "docker",
                    "ps",
                    "--format",
                    "{{json .}}"
            );

            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;

            while ((line = reader.readLine()) != null) {

                JsonNode jsonNode = objectMapper.readTree(line);

                DockerContainerEntity container =
                        new DockerContainerEntity(
                                jsonNode.get("ID").asString(),
                                jsonNode.get("Names").asString(),
                                jsonNode.get("Image").asString(),
                                jsonNode.get("Status").asString()
                        );

                containers.add(container);
            }

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new RuntimeException("Docker command failed");
            }

            return containers;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to retrieve running Docker containers",
                    e
            );
        }
    }
    public List<DockerContainerEntity> getContainers(String flag) {

        List<DockerContainerEntity> containers = new ArrayList<>();

        try {

            ProcessBuilder processBuilder = new ProcessBuilder(
                    "docker",
                    "ps",
                    flag,
                    "--format",
                    "{{json .}}"
            );

            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;

            while ((line = reader.readLine()) != null) {

                JsonNode jsonNode = objectMapper.readTree(line);

                DockerContainerEntity container =
                        new DockerContainerEntity(
                                jsonNode.get("ID").asString(),
                                jsonNode.get("Names").asString(),
                                jsonNode.get("Image").asString(),
                                jsonNode.get("Status").asString()
                        );

                containers.add(container);
            }

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new RuntimeException("Docker command failed");
            }

            return containers;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to retrieve running Docker containers",
                    e
            );
        }
    }

    public DockerContainerEntity getImages(String id){
        try {

            ProcessBuilder processBuilder = new ProcessBuilder(
                    "docker",
                    "images",
                    "--format",
                    "{{json .}}"
            );

            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line = reader.readLine();
                JsonNode jsonNode = objectMapper.readTree(line);
                return
                        new DockerContainerEntity(
                                jsonNode.get("ID").asString(),
                                jsonNode.get("Names").asString(),
                                jsonNode.get("Image").asString(),
                                jsonNode.get("Status").asString()
                        );
            } catch (IOException ex) {
            throw new RuntimeException("Image Not found");
        }

    }

}


