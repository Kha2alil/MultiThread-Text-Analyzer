module com.example.multithreadtextanalyzer {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.poi.ooxml;
    requires org.apache.pdfbox;
    requires com.github.librepdf.openpdf;
    requires java.desktop;


    opens com.example.multithreadtextanalyzer to javafx.fxml;
    opens com.example.multithreadtextanalyzer.controllers to javafx.fxml;

    exports com.example.multithreadtextanalyzer;
    exports com.example.multithreadtextanalyzer.controllers;
}
