package com.example.multithreadtextanalyzer.services;

import com.example.multithreadtextanalyzer.model.AnalysisResult;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TextAnalyzerService {


    private static final double WORDS_PER_MINUTE = 200.0;

    private static final String DEFAULT_ENCODING = StandardCharsets.UTF_8.name();
    public static final List<String> TEXT_EXTENSIONS =
            Arrays.asList("txt", "log", "csv", "rtf");





    public AnalysisResult analyze(File file) throws IOException {
        long startTime = System.currentTimeMillis();

        String pureText = readTextFromFile(file);

        Map<String, Integer> wordFrequencies = new HashMap<>();

        List<String> cleanedWords = countWords(pureText, wordFrequencies);

        long totalWords = cleanedWords.size();


        Set<String> uniqueWordsSet = new HashSet<>(wordFrequencies.keySet());


        int sentenceCount = countSentences(pureText);
        int paragraphCount = countParagraphs(pureText);

        String language = detectLanguageSimple(pureText);
        double readingTimeMinutes = totalWords / WORDS_PER_MINUTE;
        long processingTime = System.currentTimeMillis() - startTime;
        double compressionRatio = calculateCompressionRatio(file, pureText);

        Map<String, Integer> topFrequentTerms = getTopFrequentTerms(wordFrequencies, 10);


        return new AnalysisResult(
                file,
                totalWords,
                uniqueWordsSet,
                sentenceCount,
                paragraphCount,
                topFrequentTerms,
                processingTime,
                readingTimeMinutes,
                DEFAULT_ENCODING,
                language,
                compressionRatio
        );
    }



    private static String getFileExtension(File file) {
        String name = file.getName();
        int lastpos = name.lastIndexOf(".");
        if (lastpos == -1) { //no extension.
            return "";
        }
        return name.substring(lastpos + 1).toLowerCase();
    }


     public String readTextFromFile(File file) throws IOException {
        String extension = getFileExtension(file);

        if (TEXT_EXTENSIONS.contains(extension)) {

            return java.nio.file.Files.readString(file.toPath(), StandardCharsets.UTF_8);
        }
        else if (extension.equals("docx")) {
            // Apache POI for DOCX
            try (FileInputStream f1 = new FileInputStream(file);
                 XWPFDocument document = new XWPFDocument(f1);
                 XWPFWordExtractor textExtractor = new XWPFWordExtractor(document)) {
                return textExtractor.getText();
            }
        }

        else if (extension.equals("pdf")) {
            try (PDDocument document = PDDocument.load(file)) {
                PDFTextStripper stripper = new PDFTextStripper();
                return stripper.getText(document);
            }
        }

        else {
            throw new UnsupportedOperationException("File type ." + extension + " is not yet supported for analysis.");
            
        }
    }

    /**
     * Tokenizes text, removes punctuation, converts to lowercase, and populates the frequency map.
     */
    private List<String> countWords(String text, Map<String, Integer> wordFrequencies) {
        String[] wordsArray = text.toLowerCase().split("[^\\p{L}\\p{N}]+");

        List<String> cleanedWords = new ArrayList<>();

        for (String word : wordsArray) {

            if (word.length() > 1 || (word.length() == 1 && Character.isLetterOrDigit(word.charAt(0)))) {
                cleanedWords.add(word);

                if (wordFrequencies.containsKey(word)) {

                    wordFrequencies.put(word, wordFrequencies.get(word) + 1);
                } else {

                    wordFrequencies.put(word, 1);
                }
            }
        }
        return cleanedWords;
    }

    /**
     * Counts sentences using basic punctuation rules.
     */
    private int countSentences(String text) {
        if (text == null || text.isEmpty()) return 0;

        // Splits the text at ., !, ? followed by space or end
        return text.split("[.!?؟؛]+[\\s|$]").length;
    }

    /**
     * Counts paragraphs by detecting double line breaks.
     */
    private int countParagraphs(String text) {
        if (text == null || text.trim().isEmpty()) return 0;

        // Replaces all matched line breaks with a single \n
        String cleanText = text.replaceAll("\\r\\n|\\r|\\n", "\n");
        String[] paragraphs = cleanText.split("\n\n+");

        // Ensure at least 1 is returned if the text is not empty
        return Math.max(1, paragraphs.length);
    }

    /**
     * Extracts the top N most frequent terms from the frequency map.
     */
    //      key: word , value:count
    private Map<String, Integer> getTopFrequentTerms(Map<String, Integer> wordFrequencies, int limit) {

        List<Map.Entry<String, Integer>> list = new ArrayList<>(wordFrequencies.entrySet()); // create new list

        list.sort(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {

                int compareValue = entry2.getValue().compareTo(entry1.getValue());
                if (compareValue == 0) {
                    return entry1.getKey().compareTo(entry2.getKey());
                }
                return compareValue;
            }
        });


        Map<String, Integer> topTerms = new LinkedHashMap<>();
        int count = 0;

        for (Map.Entry<String, Integer> entry : list) {

            if (count >= limit) {
                break;
            }

            topTerms.put(entry.getKey(), entry.getValue());

            count++;
        }

        return topTerms;
    }

    /**
     * Calculates the compression ratio (Original File Size / Extracted Text Size).
     * This is a simplified proxy for compression ratio.
     */
    private double calculateCompressionRatio(File file, String rawText) {
        try {
            long originalSize = file.length(); // returns the size of the file in bytes

            // Size of the raw text content (in bytes) either
            long textByteSize = rawText.getBytes(StandardCharsets.UTF_8).length;

            if (originalSize == 0 || textByteSize == 0) return 1.0;

            // Ratio of the text content size to the original file size
            return (double) textByteSize / (double) originalSize;

        } catch (Exception e) {
            return 1.0;
        }
    }
    private String detectLanguageSimple(String text) {
        if (text == null || text.isBlank()) return "Unknown";


        boolean hasArabic = text.matches("(?s).*[\\u0600-\\u06FF].*");

        boolean hasEnglish = text.matches("(?s).*[a-zA-Z].*");

        if (hasArabic && hasEnglish) {
            return "Multiple (Ar/En)"; //
        } else if (hasArabic) {
            return "Arabic";
        } else if (hasEnglish) {
            return "English";
        }

        return "Unknown";
    }
}