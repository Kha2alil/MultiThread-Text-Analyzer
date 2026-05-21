package com.example.multithreadtextanalyzer.tasks;

import com.example.multithreadtextanalyzer.model.AnalysisResult;
import com.example.multithreadtextanalyzer.services.TextAnalyzerService;
import javafx.concurrent.Task;

import java.io.File;

/**
 * Task for running the file analysis in a background thread.
 * This prevents the JavaFX UI from freezing.
 */
public class AnalysisTask extends Task<AnalysisResult> {

    private final File fileToAnalyze; // The file to be analyzed
    private final TextAnalyzerService analyzerService; // Service to perform the analysis

    // Constructor to initialize with the file to analyze
    public AnalysisTask(File fileToAnalyze) {
        this.fileToAnalyze = fileToAnalyze;
        this.analyzerService = new TextAnalyzerService(); // Initialize the service

        // Initial setup for the task properties (used for binding to UI later)
        updateTitle("Analyzing: " + fileToAnalyze.getName());
        updateMessage("Waiting to start...");
    }

    /**
     * This method runs on a background thread.
     * @return The completed AnalysisResult object.
     */
    @Override
    protected AnalysisResult call() throws Exception {
        // --- 1. Preparation Phase (Small Progress Update) ---
        // total work is 1.0 (100%). We update progress throughout.
        updateProgress(0.01, 1.0);
        updateMessage("Reading file and preparing analysis...");

        // --- 2. Heavy Computation Phase ---
        // Run the service method we built previously
        AnalysisResult result = analyzerService.analyze(fileToAnalyze);

        // --- 3. Completion Phase ---
        updateProgress(1.0, 1.0); // Set progress to 100%
        updateMessage("Analysis complete!");

        // Return the final result
        return result;
    }

}