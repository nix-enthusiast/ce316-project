package com.iae.model;

import java.time.LocalDateTime;

public class Project {

    private int id;
    private String name;
    private String description;
    private String configurationName;
    private String zipDirectory;
    private String binaryArgs;
    private String expectedOutputPath;
    private LocalDateTime createdAt;

    public Project(int id, String name, String description, String configurationName,
                   String zipDirectory, String binaryArgs, String expectedOutputPath, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.configurationName = configurationName;
        this.zipDirectory = zipDirectory;
        this.binaryArgs = binaryArgs;
        this.expectedOutputPath = expectedOutputPath;
        this.createdAt = createdAt;
    }

    // GETTERS
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getConfigurationName() { return configurationName; }
    public String getZipDirectory() { return zipDirectory; }
    public String getBinaryArgs() { return binaryArgs; }
    public String getExpectedOutputPath() { return expectedOutputPath; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // SETTERS
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setConfigurationName(String configurationName) { this.configurationName = configurationName; }
    public void setZipDirectory(String zipDirectory) { this.zipDirectory = zipDirectory; }
    public void setBinaryArgs(String binaryArgs) { this.binaryArgs = binaryArgs; }
    public void setExpectedOutputPath(String expectedOutputPath) { this.expectedOutputPath = expectedOutputPath; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}