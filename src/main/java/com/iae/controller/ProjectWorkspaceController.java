package com.iae.controller;

import com.iae.ExecutionPipeline.EvaluationEngine;
import com.iae.ExecutionPipeline.ExecutionManager;
import com.iae.ExecutionPipeline.ZipExtractor;
import com.iae.model.Configuration;
import com.iae.model.Project;
import com.iae.model.StudentResult;
import com.iae.service.ConfigurationManager;
import com.iae.service.ProjectService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.scene.control.Button;

import java.io.File;
import java.util.List;

public class ProjectWorkspaceController {

    @FXML private TextField zipDirectoryField;
    @FXML private TextField binaryArgsField;
    @FXML private TextField expectedOutputField;
    @FXML private ProgressBar progressBar;
    @FXML private Label statusLabel;
    @FXML private Button runPipelineBtn;
    @FXML private Label projectInfoLabel;

    private Project currentProject;
    private final ProjectService projectService = new ProjectService();

    public void setProject(Project project) {
        this.currentProject = project;

        if (project != null) {
            String name = project.getName() == null ? "(unnamed)" : project.getName();
            String cfg  = project.getConfigurationName() == null ? "(no config)" : project.getConfigurationName();
            projectInfoLabel.setText("Project: " + name + "   |   Configuration: " + cfg);
        } else {
            projectInfoLabel.setText("No project loaded");
        }

        if (project.getZipDirectory() != null) zipDirectoryField.setText(project.getZipDirectory());
        if (project.getBinaryArgs() != null) binaryArgsField.setText(project.getBinaryArgs());
        if (project.getExpectedOutputPath() != null) expectedOutputField.setText(project.getExpectedOutputPath());
    }

    @FXML
    void onBrowseZip(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select ZIP Directory");
        File selectedDirectory = directoryChooser.showDialog(zipDirectoryField.getScene().getWindow());
        if (selectedDirectory != null) {
            zipDirectoryField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    @FXML
    void onBrowseExpectedOutput(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Expected Output File");
        File selectedFile = fileChooser.showOpenDialog(expectedOutputField.getScene().getWindow());
        if (selectedFile != null) {
            expectedOutputField.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    void onRunPipeline(ActionEvent event) {
        String zipDir = zipDirectoryField.getText();
        String expOut = expectedOutputField.getText();
        String binArgs = binaryArgsField.getText();

        if (currentProject == null) {
            new Alert(Alert.AlertType.ERROR,
                    "Project not loaded. First, open or create a project from the dashboard.").showAndWait();
            return;
        }

        if (zipDir == null || zipDir.isBlank() || expOut == null || expOut.isBlank()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "ZIP directory and Expected Output paths cannot be empty.");
            alert.showAndWait();
            return;
        }

        currentProject.setZipDirectory(zipDir);
        currentProject.setExpectedOutputPath(expOut);
        currentProject.setBinaryArgs(binArgs);

        try {
            projectService.saveProject(currentProject);
            projectService.clearResults(currentProject.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        javafx.concurrent.Task<Void> gradingTask = new javafx.concurrent.Task<>() {
            @Override
            protected Void call() throws Exception {
                ZipExtractor extractor = new ZipExtractor();
                File zipDirectory = new File(zipDir);
                List<ZipExtractor.ExtractionResult> extractedFiles = extractor.extractAll(zipDirectory);

                Configuration config = ConfigurationManager.getInstance().listAll().stream()
                        .filter(c -> c.getName().equals(currentProject.getConfigurationName()))
                        .findFirst().orElse(null);

                if (config == null) {
                    updateMessage("Error: Configuration not found!");
                    return null;
                }

                ExecutionManager execManager = new ExecutionManager();
                EvaluationEngine evalEngine = new EvaluationEngine();
                File expectedFile = new File(expOut);

                int total = extractedFiles.size();
                int current = 0;

                for (ZipExtractor.ExtractionResult extResult : extractedFiles) {
                    updateMessage("Processing student " + extResult.getStudentId() + "...");

                    if (!extResult.isSuccess()) {
                        StudentResult failResult = new StudentResult(
                                currentProject.getId(), extResult.getStudentId(), false, false, false,
                                "", "", extResult.getErrorMessage()
                        );
                        projectService.saveStudentResult(failResult);
                    } else {
                        File workingDir = extResult.getExtractedDirectory();
                        ExecutionManager.CompileResult compResult = execManager.compile(config, workingDir);

                        if (!compResult.success()) {
                            StudentResult failResult = new StudentResult(
                                    currentProject.getId(), extResult.getStudentId(), false, false, false,
                                    compResult.log(), "", ""
                            );
                            projectService.saveStudentResult(failResult);
                        } else {
                            ExecutionManager.RunResult runResult = execManager.run(config, workingDir, currentProject.getBinaryArgs());
                            boolean match = runResult.success() && evalEngine.compareOutput(runResult.output(), expectedFile);

                            StudentResult finalResult = new StudentResult(
                                    currentProject.getId(), extResult.getStudentId(), true, runResult.success(), match,
                                    compResult.log(), runResult.output(), runResult.errorLog()
                            );
                            projectService.saveStudentResult(finalResult);
                        }
                    }

                    current++;
                    updateProgress(current, total);
                }

                updateMessage("Pipeline execution completed!");
                return null;
            }
        };

        progressBar.progressProperty().bind(gradingTask.progressProperty());
        statusLabel.textProperty().bind(gradingTask.messageProperty());

        gradingTask.setOnSucceeded(e -> {
            runPipelineBtn.setDisable(false);
            ResultsGridController controller = (ResultsGridController) MainController.getInstance()
                    .loadViewAndGetController("/com/iae/controller/results-grid-view.fxml");
            if (controller != null) {
                controller.setProject(currentProject);
            }
        });

        runPipelineBtn.setDisable(true);
        gradingTask.setOnFailed(e -> runPipelineBtn.setDisable(false));
        gradingTask.setOnCancelled(e -> runPipelineBtn.setDisable(false));

        new Thread(gradingTask).start();
    }
}