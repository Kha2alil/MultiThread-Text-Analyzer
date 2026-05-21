package com.example.multithreadtextanalyzer.controllers;

import com.example.multithreadtextanalyzer.model.AnalysisResult;

import com.example.multithreadtextanalyzer.tasks.AnalysisTask;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.util.Duration;


import java.io.File;

import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;



public class MainController {
    @FXML private HBox perFileResultsContainer;
    @FXML private BorderPane mainRoot;
    @FXML private VBox emptyStatePane, filesListContainer, frequentTermsContainer;
    @FXML private Pane dragAndDropArea;
    @FXML private Label filesNumberLabel, analysisStatusLabel;
    @FXML private ProgressBar analysisProgressBar;
    @FXML private ToggleButton toggleBtn;
    @FXML private ImageView themeIcon;

    @FXML private Label totalWordsLabel, uniqueWordsLabel, sentencesLabel, paragraphsLabel;
    @FXML private Label readingTimeLabel, processingTimeLabel, compressionRatioLabel;
    @FXML private Label fileEncodingLabel, languageLabel;

    @FXML private Button bottomBtnStart, bottomBtnCancel;

    private FileListManager fileListManager;
    private DragAndDropManager dragAndDropManager;
    private ExecutorService executorService;

    private Set<String> detectedLanguagesSet = new HashSet<>();

    private Map<String, Integer> globalFrequentTerms = new HashMap<>();
    private long totalWordsAcc, totalSentencesAcc, totalUniqueWordsAcc, totalParagraphsAcc;
    private double totalReadingTimeAcc, totalProcessingTimeAcc, totalCompressionRatioAcc, totalFilesProcessedAcc;
    private AtomicBoolean isCancelled = new AtomicBoolean(false);
    private Set<String> globalUniqueWordsSet = new HashSet<>();


    @FXML
    public void initialize() {
        fileListManager = new FileListManager(filesListContainer, emptyStatePane, filesNumberLabel);
        dragAndDropManager = new DragAndDropManager(dragAndDropArea, fileListManager);
        dragAndDropManager.setup();

        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        themeController = new ThemeController(mainRoot, toggleBtn, themeIcon);
        alertManager = new AlertManager(toggleBtn, mainRoot);
        exportController = new ExportController();

        bottomBtnStart.setDisable(false);
        bottomBtnCancel.setDisable(true);
        clearAnalysisUI();
    }

    @FXML
    private void onStartAnalysisClicked() {
        List<File> filesToAnalyze = fileListManager.getTrackedFiles();
        if (filesToAnalyze.isEmpty()) return;

        bottomBtnStart.setDisable(true);
        bottomBtnCancel.setDisable(false);

        prepareUIForNewAnalysis();
        isCancelled.set(false);
        int totalFiles = filesToAnalyze.size();
        AtomicInteger filesFinished = new AtomicInteger(0);
        AtomicBoolean hasError = new AtomicBoolean(false);
        List<String> failedFiles = Collections.synchronizedList(new ArrayList<>());

        for (File file : filesToAnalyze) {
            AnalysisTask task = new AnalysisTask(file);
            task.setOnSucceeded(event -> {
                AnalysisResult result = task.getValue();
                totalFilesProcessedAcc++;
                updateAnalysisUI(result);
                checkProgress(filesFinished.incrementAndGet(), totalFiles, hasError, failedFiles);
            });
            task.setOnFailed(event -> {
                hasError.set(true);
                failedFiles.add(file.getName());
                checkProgress(filesFinished.incrementAndGet(), totalFiles, hasError, failedFiles);
                addFailedFileCard(file);
            });
            executorService.submit(task);
        }
    }


    private void prepareUIForNewAnalysis() {
        resetAccumulators();
        detectedLanguagesSet.clear();
        frequentTermsContainer.getChildren().clear();
        if (perFileResultsContainer != null) {
            perFileResultsContainer.getChildren().clear();
        }
        analysisProgressBar.setProgress(0);
        analysisStatusLabel.setText("Analyzing...");
    }
    private void addFileResultCard(AnalysisResult result) {
        boolean isDark = toggleBtn.isSelected();
        Platform.runLater(() -> {
            VBox card = new VBox(12);
            card.getStyleClass().add("file-result-card");
            card.setPadding(new Insets(15));
            card.setMinWidth(220);
            card.setMaxWidth(220);

            Label nameLbl = new Label(result.getFile().getName());
            nameLbl.getStyleClass().add("card-file-name");

            Label statusLbl = new Label("Completed");
            statusLbl.getStyleClass().add("status-badge-success");

            GridPane grid = new GridPane();
            grid.setHgap(12); grid.setVgap(10);
            grid.add(createStyledLabel("Words: " + result.getTotalWords()), 0, 0);
            grid.add(createStyledLabel("Uniq: " + result.getUniqueWordsCount()), 1, 0);
            grid.add(createStyledLabel("Sent: " + result.getSentenceCount()), 0, 1);
            grid.add(createStyledLabel("Ratio: " + String.format("%.2f", result.getCompressionRatio())), 1, 1);
            grid.add(createStyledLabel("lang: " + result.getLanguage()), 0, 2);
            grid.add(createStyledLabel("ProcTime: " + result.getProcessingTimeMillis()), 1, 2);


            ProgressBar pb = new ProgressBar(1.0);

            pb.setStyle("-fx-accent: #023828; -fx-control-inner-background: #10b981") ;
            pb.setMaxWidth(Double.MAX_VALUE);
            pb.setPrefHeight(6);

            card.getChildren().addAll(nameLbl, statusLbl, grid, pb);
            perFileResultsContainer.getChildren().add(card);
        });
    }

    private void addFailedFileCard(File file) {
        boolean isDark = toggleBtn.isSelected();
        Platform.runLater(() -> {
            VBox card = new VBox(12);
            card.getStyleClass().addAll("file-result-card", "card-failed");
            card.setPadding(new Insets(15));
            card.setMinWidth(220);

            Label nameLbl = new Label(file.getName());
            nameLbl.getStyleClass().add("card-file-name");

            Label statusLbl = new Label("File Corrupted");
            statusLbl.getStyleClass().add("status-badge-failed");

            Label errorMsg = new Label("Failed to read content.");
            errorMsg.getStyleClass().add("card-sub-text");

            ProgressBar pb = new ProgressBar(1.0);

            pb.setStyle("-fx-accent: #ef4444; -fx-control-inner-background: " + (isDark ? "#2d1a1a" : "#fee2e2") + ";");
            pb.setMaxWidth(Double.MAX_VALUE);
            pb.setPrefHeight(6);

            card.getChildren().addAll(nameLbl, statusLbl, errorMsg, pb);
            perFileResultsContainer.getChildren().add(card);
        });
    }    private Label createStyledLabel(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("card-sub-text");
        return l;
    }


    private void checkProgress(int finished, int total, AtomicBoolean hasError, List<String> failedFiles) {
        if (isCancelled.get()) {
            return;
        }
        animateProgressBar((double) finished / total);
        analysisStatusLabel.setText("Processing: " + finished + " / " + total);

        if (finished == total) {
            if (!hasError.get()) {
                finalizeUIState("Completed Successfully!");
                showFinalSuccessAlert(total);
            } else {
                finalizeUIState("Analysis Finished with errors");
                showFinalErrorAlert(failedFiles);
            }
        }
    }


    @FXML
    private void onCancelAnalysisClicked() {
        if (executorService != null) {
            isCancelled.set(true);
            executorService.shutdownNow();


            executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

            Platform.runLater(() -> {
                finalizeUIState("Analysis Cancelled");


                analysisStatusLabel.setText("Analysis Stopped by User");
            });

        }
    }

    private void updateAnalysisUI(AnalysisResult result) {
        if (isCancelled.get()) return;
        totalWordsAcc += result.getTotalWords();
        totalSentencesAcc += result.getSentenceCount();
        totalParagraphsAcc += result.getParagraphCount();
        totalReadingTimeAcc += result.getReadingTimeMinutes();
        totalProcessingTimeAcc += result.getProcessingTimeMillis();


        detectedLanguagesSet.add(result.getLanguage());

        addFileResultCard(result);

        Platform.runLater(() -> {

            if (detectedLanguagesSet.size() > 1) {
                languageLabel.setText("Multiple Languages");
            } else if (detectedLanguagesSet.size() == 1) {
                languageLabel.setText(detectedLanguagesSet.iterator().next());
            }

            if (result.getUniqueWordsSet() != null) {
                globalUniqueWordsSet.addAll(result.getUniqueWordsSet());
            }

            uniqueWordsLabel.setText(String.valueOf(globalUniqueWordsSet.size()));
            totalWordsLabel.setText(String.valueOf(totalWordsAcc));
            sentencesLabel.setText(String.valueOf(totalSentencesAcc));
            paragraphsLabel.setText(String.valueOf(totalParagraphsAcc));
            readingTimeLabel.setText(String.format("%.1f min", totalReadingTimeAcc));
            processingTimeLabel.setText(String.format("%.2f s", totalProcessingTimeAcc / 1000.0));

            totalCompressionRatioAcc += result.getCompressionRatio();
            double avgRatio = totalCompressionRatioAcc / totalFilesProcessedAcc;
            compressionRatioLabel.setText(String.format("%.2f", avgRatio));

            fileEncodingLabel.setText(result.getFileEncoding());
        });


        if (result.getFrequentTerms() != null) {
            result.getFrequentTerms().forEach((word, count) ->
                    globalFrequentTerms.merge(word, count, Integer::sum));
        }
        refreshFrequentTermsUI();
    }

    private void resetAccumulators() {
        totalWordsAcc = totalSentencesAcc = totalUniqueWordsAcc = totalParagraphsAcc = 0;
        totalReadingTimeAcc = totalProcessingTimeAcc = totalCompressionRatioAcc = totalFilesProcessedAcc = 0;
        globalFrequentTerms.clear();
        globalUniqueWordsSet.clear();
    }

    private void finalizeUIState(String statusText) {
        bottomBtnStart.setDisable(false);
        bottomBtnCancel.setDisable(true);
        analysisStatusLabel.setText(statusText);
    }

    private void animateProgressBar(double targetValue) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(600), new KeyValue(analysisProgressBar.progressProperty(), targetValue)));
        timeline.play();
    }

    @FXML
    private void clearAnalysisUI() {
        resetAccumulators();
        detectedLanguagesSet.clear();

        totalWordsLabel.setText("0");
        sentencesLabel.setText("0");
        uniqueWordsLabel.setText("0");
        paragraphsLabel.setText("0");
        readingTimeLabel.setText("0 min");
        processingTimeLabel.setText("0 s");
        compressionRatioLabel.setText("0.00");
        fileEncodingLabel.setText("-");
        languageLabel.setText("-");

        frequentTermsContainer.getChildren().clear();
        if (perFileResultsContainer != null) {
            perFileResultsContainer.getChildren().clear();
        }


        if (fileListManager != null) {
            fileListManager.getTrackedFiles().clear();
            if (filesListContainer != null) {
                filesListContainer.getChildren().clear();
            }
            filesNumberLabel.setText("Files: 0");
            emptyStatePane.setVisible(true);
            emptyStatePane.setManaged(true);
        }

        analysisProgressBar.setProgress(0);
        analysisStatusLabel.setText("Ready");
        bottomBtnStart.setDisable(false);
        bottomBtnCancel.setDisable(true);
    }

    private ThemeController themeController;
    @FXML
    private void onToggleTheme() {
        themeController.toggleTheme();
    }

    private void refreshFrequentTermsUI() {
        frequentTermsContainer.getChildren().clear();
        globalFrequentTerms.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue())).limit(10)
                .forEach(entry -> {
                    Button btn = new Button(entry.getKey() + " (" + entry.getValue() + ")");
                    btn.getStyleClass().add("frequent-terms");
                    btn.setMaxWidth(Double.MAX_VALUE);
                    frequentTermsContainer.getChildren().add(btn);
                });
    }

    @FXML
    private void onSelectFilesClicked() {
        FileChooser fc = new FileChooser();
        List<File> files = fc.showOpenMultipleDialog(null);
        if (files != null) fileListManager.addFiles(files);
    }












    //    export *********************************************************************
    private ExportController exportController;

    @FXML
    private void handleExport(ActionEvent event) {
        // Prepare the export content
        String reportContent = generateReportContent();

        // Show file chooser for text file export
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Analysis Report");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showSaveDialog(mainRoot.getScene().getWindow());

        if (file != null) {
            // Export to text file
            exportController.exportToTextFile(file, reportContent);
        }
    }

    @FXML
    private void exportToPDF() {
        // Prepare the report content and table data
        String reportContent = generateReportContent();
        String[][] tableData = prepareTableData();

        // Show file chooser for PDF export
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF Report");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(mainRoot.getScene().getWindow());

        if (file != null) {
            // Export to PDF
            exportController.exportToPDF(file, "Full Text Analysis Report", tableData, reportContent);
        }
    }

    @FXML
    private void exportToWord() {
        // Prepare the report content and table data
        String reportContent = generateReportContent();
        String[][] tableData = prepareTableData();

        // Show file chooser for Word export
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Word Report");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Word Files", "*.docx"));
        File file = fileChooser.showSaveDialog(mainRoot.getScene().getWindow());

        if (file != null) {
            // Export to Word
            exportController.exportToWord(file, "Complete Analysis Report", tableData, reportContent);
        }
    }

    // Helper method to generate the report content
// Helper method to generate the report content
    private String generateReportContent() {
        StringBuilder content = new StringBuilder();

        // Adding header

                content.append("Generated on: ").append(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())).append("\n")
                .append("--------------------------------------------------\n");


        content.append("--------------------------------------------------\n");

        // Add file information
        addStatistic(content, "File Encoding", fileEncodingLabel.getText());
        addStatistic(content, "Detected Language", languageLabel.getText());

        content.append("--------------------------------------------------\n");

        // Add frequent terms section
        content.append("\nTOP 10 MOST FREQUENT TERMS:\n");
        content.append("RANK       WORD                FREQUENCY\n");
        content.append("----               ----                ---------\n");

        if (globalFrequentTerms != null && !globalFrequentTerms.isEmpty()) {
            java.util.concurrent.atomic.AtomicInteger rank = new java.util.concurrent.atomic.AtomicInteger(1);
            globalFrequentTerms.entrySet().stream()
                    .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                    .limit(10)
                    .forEach(e -> {
                        content.append(String.format("#%-5d %-18s : %d times%n", rank.getAndIncrement(), e.getKey().toUpperCase(), e.getValue()));
                    });
            content.append("\n");
            content.append("\n");
        } else {
            content.append("No terms analyzed yet.\n");
            content.append("\n");
            content.append("\n");
        }

        return content.toString();
    }

    // Helper method to add formatted statistics to the report
    private void addStatistic(StringBuilder content, String label, String value) {
        content.append(String.format("%-25s : %s%n", label, value != null ? value : "N/A"));
    }

    // Helper method to prepare table data for PDF and Word exports
    private String[][] prepareTableData() {
        return new String[][] {
                {"Total Words", totalWordsLabel.getText()},
                {"Unique Words", uniqueWordsLabel.getText()},
                {"Sentences", sentencesLabel.getText()},
                {"Paragraphs", paragraphsLabel.getText()},
                {"Reading Time", readingTimeLabel.getText()},
                {"Processing Time", processingTimeLabel.getText()},
                {"File Encoding", fileEncodingLabel.getText()},
                {"Detected Language", languageLabel.getText()}
        };
    }








    //    alert *****************************************************************

    private AlertManager alertManager;

    private void showFinalSuccessAlert(int totalFiles) {
        alertManager.showFinalSuccessAlert(totalFiles);
    }

    private void showFinalErrorAlert(List<String> failedFiles) {
        alertManager.showFinalErrorAlert(failedFiles);
    }

    @FXML
    private void showInfoAlert(String title, String content) {
        alertManager.showInfoAlert(title, content);
    }

    private void showErrorAlert(String title, String content) {
        alertManager.showErrorAlert(title, content);
    }


    public void shutdown() {
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }


}