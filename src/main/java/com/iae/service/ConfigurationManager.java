package com.iae.service;

import com.iae.model.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class ConfigurationManager {

    private static ConfigurationManager instance;
    private final File configsDir;

    private ConfigurationManager() {
        configsDir = new File(System.getenv("APPDATA") + "/IAE/configs");
        configsDir.mkdirs();
    }

    public static ConfigurationManager getInstance() {
        if (instance == null) instance = new ConfigurationManager();
        return instance;
    }

    public List<Configuration> listAll() {
        List<Configuration> list = new ArrayList<>();
        File[] files = configsDir.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) return list;
        for (File f : files) {
            try {
                list.add(Configuration.fromFile(f));
            } catch (IOException e) {
                System.err.println("Config okunamadı: " + f.getName() + " - " + e.getMessage());
            }
        }
        return list;
    }

    public void save(Configuration config) throws IOException {
        File target = new File(configsDir, sanitizeFileName(config.getName()) + ".json");
        Configuration.toFile(config, target);
    }

    public boolean delete(Configuration config) {
        File target = new File(configsDir, sanitizeFileName(config.getName()) + ".json");
        return target.delete();
    }

    public void importFrom(File sourceFile) throws IOException {
        File target = new File(configsDir, sourceFile.getName());
        Files.copy(sourceFile.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public void exportTo(Configuration config, File targetFile) throws IOException {
        Configuration.toFile(config, targetFile);
    }

    private String sanitizeFileName(String name) {
        return name.replaceAll("[^a-zA-Z0-9_\\-]", "_");
    }
}