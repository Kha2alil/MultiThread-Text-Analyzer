package com.example.multithreadtextanalyzer.controllers;

import javafx.stage.FileChooser;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Region;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class ExportController {

    // Method to export the report content to a text file
    public void exportToTextFile(File file, String reportContent) {
        try (PrintWriter pw = new PrintWriter(file)) {
            pw.println(reportContent);
            showAlert("Export Success", "Report saved successfully as a text file.");
        } catch (Exception e) {
            showAlert("Export Error", "An error occurred while saving the file: " + e.getMessage());
        }
    }

    // Method to export the report content to a PDF file
    public void exportToPDF(File file, String title, String[][] tableData, String reportContent) {
        Document myDoc = new Document();
        try {
            FileOutputStream output = new FileOutputStream(file);
            PdfWriter.getInstance(myDoc, output);
            myDoc.open();

            // Title and report content
            Paragraph pdfTitle = new Paragraph(title);
            pdfTitle.setAlignment(Paragraph.ALIGN_CENTER);
            myDoc.add(pdfTitle);
            myDoc.add(new Paragraph(" "));
            myDoc.add(new Paragraph(reportContent));

            // Add table with 2 columns (Metric Name, Result)
            com.lowagie.text.pdf.PdfPTable myTable = new com.lowagie.text.pdf.PdfPTable(2);
            myTable.setWidthPercentage(100);

            // Add table headers
            myTable.addCell(createTableCell("Metric Name"));
            myTable.addCell(createTableCell("Result"));

            // Add data to the table
            for (String[] row : tableData) {
                myTable.addCell(row[0]);
                myTable.addCell(row[1]);
            }

            myDoc.add(myTable);

            myDoc.close();
            showAlert("Export Success", "PDF exported successfully.");
        } catch (Exception e) {
            showAlert("Export Error", "An error occurred while exporting to PDF: " + e.getMessage());
        }
    }

    // Method to export the report content to a Word file
    public void exportToWord(File file, String title, String[][] tableData, String reportContent) {
        try {
            XWPFDocument myDoc = new XWPFDocument();
            XWPFTable myTable = myDoc.createTable(1, 2); // 1 row, 2 columns for headers
            myTable.setWidth("100%");

            // Add title and report content
            XWPFRun titleRun = myDoc.createParagraph().createRun();
            titleRun.setText(title);
            titleRun.setBold(true);
            titleRun.setFontSize(20);

            // Create table headers
            myTable.getRow(0).getCell(0).setText("Metric Name");
            myTable.getRow(0).getCell(1).setText("Result");

            // Populate table with data
            for (String[] row : tableData) {
                int rowIndex = myTable.getRows().size();
                myTable.createRow();
                myTable.getRow(rowIndex).getCell(0).setText(row[0]);
                myTable.getRow(rowIndex).getCell(1).setText(row[1]);
            }

            // Add report content below the table
            myDoc.createParagraph().createRun().setText(reportContent);

            // Save the Word document
            try (FileOutputStream output = new FileOutputStream(file)) {
                myDoc.write(output);
            }

            showAlert("Export Success", "Word report saved successfully.");
        } catch (Exception e) {
            showAlert("Export Error", "An error occurred while exporting to Word: " + e.getMessage());
        }
    }

    // Helper method to create a table cell in the PDF and Word exports
    private com.lowagie.text.pdf.PdfPCell createTableCell(String content) {
        com.lowagie.text.pdf.PdfPCell cell = new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Phrase(content));
        cell.setBackgroundColor(java.awt.Color.decode("#2B579A"));
        return cell;
    }

    // Helper method to show success/error alerts
    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }
}
