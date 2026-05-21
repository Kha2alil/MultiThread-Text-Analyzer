package com.example.multithreadtextanalyzer.controllers;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileListManager {

    private final VBox filesListContainer;
    private final VBox emptyStatePane;
    private final Label filesNumberLabel;

    // CRITICAL: This list tracks the actual File objects for the analysis logic
    private final List<File> trackedFiles = new ArrayList<>();

    public FileListManager(VBox filesListContainer, VBox emptyStatePane, Label filesNumberLabel) {
        this.filesListContainer = filesListContainer;
        this.emptyStatePane = emptyStatePane;
        this.filesNumberLabel = filesNumberLabel;
    }

    /**
     * Adds a list of files to the UI and the internal tracking list.
     */
    public void addFiles(List<File> files) {
        if (files == null || files.isEmpty()) return;

        // Make the file list visible
        emptyStatePane.setVisible(false);
        emptyStatePane.setManaged(false);
        filesListContainer.setVisible(true);
        filesListContainer.setManaged(true);

        for (File file : files) {
            // Check for duplicates before adding to UI or tracking list
            if (!trackedFiles.contains(file)) {
                trackedFiles.add(file);
                filesListContainer.getChildren().add(createFileItem(file));
            }
        }
        updateFilesCount();
    }

    /**
     * Creates a single UI item for a file with a remove button.
     */
    private HBox createFileItem(File file) {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(8, 15, 8, 15));
        box.getStyleClass().add("file-item-box");


        // File name label
        Label nameLabel = new Label(file.getName());
        nameLabel.getStyleClass().add("file-item-name");

        Label sizeLabel = new Label(FileSize.formatFileSize(file.length()));
        sizeLabel.getStyleClass().add("file-item-size");

        // Region to push elements apart
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);


        // Remove button
        Button removeBtn = new Button("X");
        removeBtn.getStyleClass().add("file-item-remove-btn");

        // Set action for the remove button
        removeBtn.setOnAction(e -> {
            filesListContainer.getChildren().remove(box);
            // CRITICAL: Remove the file from the tracking list
            trackedFiles.remove(file);
            updateFilesCount();
        });

        box.getChildren().addAll(nameLabel, sizeLabel, spacer, removeBtn);
        return box;
    }

    /**
     * Updates the label showing the current number of files.
     */
    public void updateFilesCount() {
        int count = trackedFiles.size();
        filesNumberLabel.setText(count + (count == 1 ? " file" : " files"));

        // If the list is empty, show the empty state pane
        if (count == 0) {
            filesListContainer.setVisible(false);
            filesListContainer.setManaged(false);
            emptyStatePane.setVisible(true);
            emptyStatePane.setManaged(true);
        }
    }

    /**
     * Public getter to allow MainController to access the files for analysis.
     */
    public List<File> getTrackedFiles() {
        return trackedFiles;
    }
}