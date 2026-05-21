package com.example.multithreadtextanalyzer.controllers;
import java.io.File;

public class FileSize {

    // to format the size of the file
    public static String formatFileSize(long size) {
        if (size <= 0) return "0 Bytes";

        final String[] units = new String[] { "Bytes", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

        return String.format("%.1f %s", size / Math.pow(1024, digitGroups), units[digitGroups]);
    }
}
