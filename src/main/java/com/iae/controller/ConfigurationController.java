package com.iae.controller;

import com.iae.model.Configuration;
import com.iae.service.ConfigurationManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ConfigurationController {

    @FXML private ListView<Configuration> configListView;
    @FXML private TextField nameField;
    @FXML private TextField compilerPathField;
    @FXML private TextField compilerArgsField;
    @FXML private TextField binaryPathField;
    @FXML private TextField binaryArgsField;

    private final ConfigurationManager configManager = ConfigurationManager.getInstance();

    @FXML
    public void initialize() {
        loadConfigurations();

        configListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) populateForm(newVal);
                }
        );
    }

    private void loadConfigurations() {
        List<Configuration> configs = configManager.listAll();
        configListView.setItems(FXCollections.observableArrayList(configs));
    }

    private void populateForm(Configuration config) {
        nameField.setText(config.getName());
        compilerPathField.setText(config.getCompilerPath());
        compilerArgsField.setText(config.getCompilerArgs());
        binaryPathField.setText(config.getBinaryPath());
        binaryArgsField.setText(config.getBinaryArgs());
    }

    private void clearForm() {
        nameField.clear();
        compilerPathField.clear();
        compilerArgsField.clear();
        binaryPathField.clear();
        binaryArgsField.clear();
        configListView.getSelectionModel().clearSelection();
    }

    @FXML
    private void onNew() {
        clearForm();
        nameField.requestFocus();
    }

    @FXML
    private void onSave() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Config name cannot be empty.");
            return;
        }

        Configuration config = new Configuration(
                name,
                compilerPathField.getText().trim(),
                compilerArgsField.getText().trim(),
                binaryPathField.getText().trim(),
                binaryArgsField.getText().trim()
        );

        try {
            configManager.save(config);
            loadConfigurations();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Configuration saved.");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save: " + e.getMessage());
        }
    }

    @FXML
    private void onDelete() {
        Configuration selected = configListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Select a configuration to delete.");
            return;
        }

        boolean deleted = configManager.delete(selected);
        if (deleted) {
            clearForm();
            loadConfigurations();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Configuration deleted.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete.");
        }
    }

    @FXML
    private void onImport() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Configuration File");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File file = chooser.showOpenDialog(getStage());
        if (file != null) {
            try {
                configManager.importFrom(file);
                loadConfigurations();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Configuration imported.");
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to import: " + e.getMessage());
            }
        }
    }

    @FXML
    private void onExport() {
        Configuration selected = configListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Select a configuration to export.");
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Save Location");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        chooser.setInitialFileName(selected.getName() + ".json");
        File file = chooser.showSaveDialog(getStage());
        if (file != null) {
            try {
                configManager.exportTo(selected, file);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Configuration exported.");
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to export: " + e.getMessage());
            }
        }
    }

    private Stage getStage() {
        return (Stage) nameField.getScene().getWindow();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}