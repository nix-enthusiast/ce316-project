package com.iae.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import java.io.IOException;

public class MainController {

    private static MainController instance;

    @FXML
    private BorderPane mainContainer;

    public MainController() {
        instance = this;
    }

    public static MainController getInstance() {
        return instance;
    }

    @FXML
    public void initialize() {
        loadView("/com/iae/controller/project-list-view.fxml");
    }

    @FXML
    void showDashboard(ActionEvent event) {
        loadView("/com/iae/controller/project-list-view.fxml");
    }

    @FXML
    void showConfigurationManager(ActionEvent event) {
        loadView("/com/iae/controller/configuration-view.fxml");
    }

    @FXML
    void showHelpManual(ActionEvent event) {
        loadView("/com/iae/controller/help-view.fxml");
    }

    @FXML
    void exitApplication(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    public void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            mainContainer.setCenter(view);
        } catch (IOException e) {
            System.err.println("Error loading view: " + fxmlPath);
            e.printStackTrace();
        }
    }
    public Object loadViewAndGetController(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            mainContainer.setCenter(view);
            return loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}