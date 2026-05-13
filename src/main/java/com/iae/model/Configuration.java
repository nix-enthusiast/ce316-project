package com.iae.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Configuration {

    private String name;
    private String compilerPath;
    private String compilerArgs;
    private String binaryPath;
    private String binaryArgs;

    public Configuration() {}

    public Configuration(String name, String compilerPath, String compilerArgs, String binaryPath, String binaryArgs) {
        this.name = name;
        this.compilerPath = compilerPath;
        this.compilerArgs = compilerArgs;
        this.binaryPath = binaryPath;
        this.binaryArgs = binaryArgs;
    }

    public static Configuration fromFile(File f) throws IOException {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(f)) {
            return gson.fromJson(reader, Configuration.class);
        }
    }

    public static void toFile(Configuration c, File f) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(f)) {
            gson.toJson(c, writer);
        }
    }

    public boolean isInterpreted() {
        return compilerPath == null || compilerPath.isBlank();
    }

    public String getName() { return name; }
    public String getCompilerPath() { return compilerPath; }
    public String getCompilerArgs() { return compilerArgs; }
    public String getBinaryPath() { return binaryPath; }
    public String getBinaryArgs() { return binaryArgs; }

    public void setName(String name) { this.name = name; }
    public void setCompilerPath(String compilerPath) { this.compilerPath = compilerPath; }
    public void setCompilerArgs(String compilerArgs) { this.compilerArgs = compilerArgs; }
    public void setBinaryPath(String binaryPath) { this.binaryPath = binaryPath; }
    public void setBinaryArgs(String binaryArgs) { this.binaryArgs = binaryArgs; }

    @Override
    public String toString() {
        return name;
    }
}
