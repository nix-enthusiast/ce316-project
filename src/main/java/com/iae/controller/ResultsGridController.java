package com.iae.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import com.iae.model.StudentResult;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import java.util.List;

public class ResultsGridController {

    @FXML private TableView<StudentResult> resultsTable;
    @FXML private TableColumn<StudentResult, String> colStudentId;
    @FXML private TableColumn<StudentResult, Boolean> colCompiled;
    @FXML private TableColumn<StudentResult, Boolean> colExecuted;
    @FXML private TableColumn<StudentResult, Boolean> colMatch;

    @FXML
    public void initialize() {
        colStudentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colCompiled.setCellValueFactory(new PropertyValueFactory<>("compilationSuccess"));
        colExecuted.setCellValueFactory(new PropertyValueFactory<>("executionSuccess"));
        colMatch.setCellValueFactory(new PropertyValueFactory<>("outputMatch"));
    }

    public void loadResultsFromDatabase(int projectId) {
        try {
            com.iae.service.ProjectService projectService = new com.iae.service.ProjectService();
            List<StudentResult> results = projectService.getResultsForProject(projectId);
            resultsTable.getItems().setAll(results);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onViewDetails(ActionEvent event) {
        StudentResult selected = resultsTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            Alert warning = new Alert(Alert.AlertType.WARNING);
            warning.setTitle("Warning");
            warning.setHeaderText(null);
            warning.setContentText("Please select a student to view details.");
            warning.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Details - " + selected.getStudentId());
        alert.setHeaderText(null);

        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);

        String compLog = selected.getCompilationLog() != null && !selected.getCompilationLog().isBlank() ? selected.getCompilationLog() : "No log available";
        String execLog = selected.getExecutionLog() != null && !selected.getExecutionLog().isBlank() ? selected.getExecutionLog() : "No log available";
        String errLog = selected.getErrorLog() != null && !selected.getErrorLog().isBlank() ? selected.getErrorLog() : "No log available";

        String content = "--- Compilation Log ---\n" + compLog +
                "\n\n--- Execution Log ---\n" + execLog +
                "\n\n--- Error Log ---\n" + errLog;

        textArea.setText(content);

        VBox dialogPaneContent = new VBox();
        dialogPaneContent.getChildren().add(textArea);
        alert.getDialogPane().setContent(dialogPaneContent);
        alert.setResizable(true);
        alert.getDialogPane().setPrefSize(500, 400);

        alert.showAndWait();
    }

    @FXML
    void onBackToWorkspace(ActionEvent event) {
        MainController.getInstance().loadView("/com/iae/controller/project-workspace-view.fxml");
    }
}