package com.example.multithreadtextanalyzer.controllers;

import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;

import java.io.File;
import java.util.List;

public class DragAndDropManager {

    private final Pane dragAndDropArea;
    private final FileListManager fileListManager;

    public DragAndDropManager(Pane dragAndDropArea, FileListManager fileListManager) {
        this.dragAndDropArea = dragAndDropArea;
        this.fileListManager = fileListManager;
    }

    // Call this to activate the listeners
    public void setup() {
        // Check if the thing you dragged into the area has files than copy it
        dragAndDropArea.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        dragAndDropArea.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();

            if (db.hasFiles()) {
                List<File> droppedFiles = db.getFiles();

                // Use the FileListManager to update the UI
                if (!droppedFiles.isEmpty()) {
                    fileListManager.addFiles(droppedFiles);
                }
            }

            event.setDropCompleted(true);
            event.consume();
        });

        // To Add a border when You drag something to yor Drag area
        dragAndDropArea.setOnDragEntered(event -> {
            dragAndDropArea.setStyle("-fx-border-color: #6C5CE7; -fx-border-width: 3; -fx-border-style: dashed;");
        });

        // No nned to the border if you exit the Area
        dragAndDropArea.setOnDragExited(event -> {
            dragAndDropArea.setStyle("");
        });
    }
}