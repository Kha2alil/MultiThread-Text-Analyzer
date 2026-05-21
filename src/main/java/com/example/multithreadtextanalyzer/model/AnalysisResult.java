package com.example.multithreadtextanalyzer.model;

import java.io.File;
import java.util.Map;
import java.util.Set;

/**
 * Data model to hold all calculated statistics and information
 * for a single file analysis run.
 */
public class AnalysisResult {


    private final File sourceFile;
    private final long totalWords;
    private final Set<String> uniqueWordsSet;
    private final int sentenceCount;
    private final int paragraphCount;
    private final Map<String, Integer> frequentTerms; // Stores word:count pairs

    private final long processingTimeMillis; // Used for "Processing Time"
    private final double readingTimeMinutes;   // Used for "Reading Time"
    private final String fileEncoding;
    private final String language;
    private final double compressionRatio; // The calculated ratio

    public AnalysisResult(
            File sourceFile,
            long totalWords,
            Set<String> uniqueWordsSet,
            int sentenceCount,
            int paragraphCount,
            Map<String, Integer> frequentTerms,
            long processingTimeMillis,
            double readingTimeMinutes,
            String fileEncoding,
            String language,
            double compressionRatio) {

        this.sourceFile = sourceFile;
        this.totalWords = totalWords;
        this.uniqueWordsSet = uniqueWordsSet;
        this.sentenceCount = sentenceCount;
        this.paragraphCount = paragraphCount;
        this.frequentTerms = frequentTerms;
        this.processingTimeMillis = processingTimeMillis;
        this.readingTimeMinutes = readingTimeMinutes;
        this.fileEncoding = fileEncoding;
        this.language = language;
        this.compressionRatio = compressionRatio;
    }

    public File getFile() {
        return sourceFile;
    }

    public File getSourceFile() {
        return sourceFile;
    }

    public long getTotalWords() {
        return totalWords;
    }
    public Set<String> getUniqueWordsSet() {
        return uniqueWordsSet;
    }

    public long getUniqueWordsCount() {
        return uniqueWordsSet.size();
    }

    public int getSentenceCount() {
        return sentenceCount;
    }

    public int getParagraphCount() {
        return paragraphCount;
    }

    public Map<String, Integer> getFrequentTerms() {
        return frequentTerms;
    }

    public long getProcessingTimeMillis() {
        return processingTimeMillis;
    }

    public double getReadingTimeMinutes() {
        return readingTimeMinutes;
    }

    public String getFileEncoding() {
        return fileEncoding;
    }

    public String getLanguage() {
        return language;
    }

    public double getCompressionRatio() {
        return compressionRatio;
    }
}