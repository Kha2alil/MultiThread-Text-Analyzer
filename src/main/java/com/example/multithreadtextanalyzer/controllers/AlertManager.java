package com.example.multithreadtextanalyzer.controllers;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.application.Platform;

import java.util.List;

public class AlertManager {

    private ToggleButton toggleBtn;
    private BorderPane mainRoot;

    public AlertManager(ToggleButton toggleBtn, BorderPane mainRoot) {
        this.toggleBtn = toggleBtn;
        this.mainRoot = mainRoot;
    }

    // Show success alert for final completion of analysis
    public void showFinalSuccessAlert(int totalFiles) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Analysis Complete");
            alert.setHeaderText("Success!");
            alert.setContentText("All " + totalFiles + " files have been analyzed successfully");

            // Add custom CSS
            styleAlert(alert);
            alert.showAndWait();
        });
    }

    // Show error alert for failed files in the analysis
    public void showFinalErrorAlert(List<String> failedFiles) {
        if (failedFiles.isEmpty()) return;

        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Analysis Errors");
            alert.setHeaderText("The following files could not be processed:");

            // Concatenate the failed files into a string
            StringBuilder sb = new StringBuilder();
            for (String name : failedFiles) {
                sb.append("- ").append(name).append("\n");
            }

            alert.setContentText(sb.toString());

            // Add custom CSS
            styleAlert(alert);
            alert.show();
        });
    }

    // Show generic information alert
    public void showInfoAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        styleAlert(alert);
        alert.showAndWait();
    }

    // Show error alert with a custom title and content
    public void showErrorAlert(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        styleAlert(alert);
        alert.showAndWait();
    }

    // Helper method to style the alert based on the theme and CSS
    private void styleAlert(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        try {
            String cssPath = getClass().getResource("/com/example/multithreadtextanalyzer/styles/style.css").toExternalForm();
            dialogPane.getStylesheets().add(cssPath);
            dialogPane.getStyleClass().add("my-alert");
            if (toggleBtn.isSelected()) dialogPane.getStyleClass().add("dark-mode");
        } catch (Exception e) {
            System.err.println("CSS Alert error: " + e.getMessage());
        }

        if (mainRoot.getScene() != null) alert.initOwner(mainRoot.getScene().getWindow());
    }
}
