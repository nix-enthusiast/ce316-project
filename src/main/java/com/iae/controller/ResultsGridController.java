package com.iae.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ResultsGridController {

    @FXML private TableView<?> resultsTable;
    @FXML private TableColumn<?, ?> colStudentId;
    @FXML private TableColumn<?, ?> colCompiled;
    @FXML private TableColumn<?, ?> colExecuted;
    @FXML private TableColumn<?, ?> colMatch;

    @FXML
    void onViewDetails(ActionEvent event) {
        System.out.println("View Details triggered - Murat will implement.");
    }

    @FXML
    void onBackToWorkspace(ActionEvent event) {
        MainController.getInstance().loadView("/com/iae/controller/project-workspace-view.fxml");
    }
}