package com.iae.controller;

import com.iae.model.Configuration;
import com.iae.model.Project;
import com.iae.service.ConfigurationManager;
import com.iae.service.ProjectService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ProjectListController {

    @FXML private ListView<Project> projectListView;
    private final ProjectService projectService = new ProjectService();

    @FXML
    public void initialize() {
        projectListView.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Project p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? null :
                        p.getName() + "  [" + p.getConfigurationName() + "]");
            }
        });
        refresh();
    }

    private void refresh() {
        try {
            List<Project> all = projectService.getAllProjects();
            projectListView.setItems(FXCollections.observableArrayList(all));
        } catch (SQLException e) {
            alert(Alert.AlertType.ERROR, "DB Error", e.getMessage());
        }
    }

    @FXML
    private void onNewProject() {
        List<Configuration> configs = ConfigurationManager.getInstance().listAll();
        if (configs.isEmpty()) {
            alert(Alert.AlertType.WARNING, "No configuration",
                    "Önce Tools > Configuration Manager'dan bir configuration oluştur.");
            return;
        }

        Dialog<Project> dialog = new Dialog<>();
        dialog.setTitle("New Project");
        dialog.setHeaderText("Yeni proje oluştur");

        ButtonType okType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okType, ButtonType.CANCEL);

        TextField nameField = new TextField();
        nameField.setPromptText("Project name");
        TextField descField = new TextField();
        descField.setPromptText("Description (optional)");
        ComboBox<Configuration> configBox = new ComboBox<>(FXCollections.observableArrayList(configs));
        configBox.setConverter(new StringConverter<>() {
            @Override public String toString(Configuration c) { return c == null ? "" : c.getName(); }
            @Override public Configuration fromString(String s) { return null; }
        });
        configBox.getSelectionModel().selectFirst();

        javafx.scene.layout.VBox box = new javafx.scene.layout.VBox(10,
                new Label("Name:"),  nameField,
                new Label("Description:"), descField,
                new Label("Configuration:"), configBox);
        box.setPadding(new javafx.geometry.Insets(10));
        dialog.getDialogPane().setContent(box);

        dialog.setResultConverter(bt -> {
            if (bt != okType) return null;
            String name = nameField.getText() == null ? "" : nameField.getText().trim();
            Configuration cfg = configBox.getValue();
            if (name.isEmpty() || cfg == null) return null;
            return new Project(0, name, descField.getText(), cfg.getName(),
                    null, null, null, LocalDateTime.now());
        });

        Optional<Project> result = dialog.showAndWait();
        result.ifPresent(p -> {
            try {
                Project created = projectService.createProject(p);
                refresh();
                openInWorkspace(created);
            } catch (SQLException e) {
                alert(Alert.AlertType.ERROR, "DB Error", e.getMessage());
            }
        });
    }

    @FXML
    private void onOpenProject() {
        Project p = projectListView.getSelectionModel().getSelectedItem();
        if (p == null) {
            alert(Alert.AlertType.WARNING, "Seçim yok", "Bir proje seç.");
            return;
        }
        openInWorkspace(p);
    }

    @FXML
    private void onDeleteProject() {
        Project p = projectListView.getSelectionModel().getSelectedItem();
        if (p == null) return;
        try {
            projectService.deleteProject(p.getId());
            refresh();
        } catch (SQLException e) {
            alert(Alert.AlertType.ERROR, "DB Error", e.getMessage());
        }
    }

    private void openInWorkspace(Project p) {
        ProjectWorkspaceController ctrl = (ProjectWorkspaceController) MainController.getInstance()
                .loadViewAndGetController("/com/iae/controller/project-workspace-view.fxml");
        if (ctrl != null) ctrl.setProject(p);
    }

    private void alert(Alert.AlertType t, String title, String msg) {
        Alert a = new Alert(t); a.setTitle(title); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }
}