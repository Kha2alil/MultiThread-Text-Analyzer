# 📊 MultiThreaded Text Analyzer
 
[![Java Version](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21.0.6-blue.svg)](https://openjfx.io/)
[![Maven](https://img.shields.io/badge/build-Maven-C71A36.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)
 
> A highly responsive, multithreaded desktop application built with JavaFX for analyzing text, documents, and code files. Extract metrics, visualize data, and export your reports seamlessly.
 
### 🔗 Live Demo
**[Insert link to your Live Demo, Video Walkthrough, or GIF here]**
 
---
 
## 📝 Description
 
**MultiThreaded Text Analyzer** is a powerful desktop utility designed to process multiple files simultaneously without freezing the user interface. It reads standard text files as well as PDF and Word documents, calculating deep insights such as word counts, sentence/paragraph counts, reading time, language detection, and frequent keyword extraction.
 
Built on a clean **Model-View-Controller (MVC)** architecture, the application delegates heavy I/O operations and computation to background threads using Java's ExecutorService and JavaFX `Task` APIs.
 
### ✨ Key Features
* **Multithreaded Processing:** Analyze dozens of files simultaneously without UI lag.
* **Drag-and-Drop Interface:** Easily import files by dropping them into the app.
* **Broad Format Support:** Reads pure text (`.txt`, `.csv`, `.log`), Word documents (`.docx`), and PDFs (`.pdf`).
* **Deep Metrics:** Calculates total words, unique words, sentences, paragraphs, reading time, file encoding, language, and a compression ratio.
* **Term Extraction:** Automatically extracts and displays the most frequent terms used in the documents.
* **Exportable Reports:** Export your analysis results to **PDF, Word (.docx), or plain text**.
* **Modern UI/UX:** Clean design with custom CSS, interactive progress bars, and a built-in **Dark/Light Theme** toggle.
---
 
## 🛠️ Tech Stack & Dependencies
 
* **Language:** Java 21
* **UI Framework:** JavaFX 21.0.6
* **Build Tool:** Maven (Wrapper included)
* **Key Libraries:**
  * [Apache POI](https://poi.apache.org/) (v5.2.5) — For reading and exporting MS Word (`.docx`) files.
  * [Apache PDFBox](https://pdfbox.apache.org/) (v3.0.1) — For parsing and extracting text from PDFs.
  * [OpenPDF](https://github.com/LibrePDF/OpenPDF) (v1.3.30) — For generating PDF analysis reports.
---
 
## 📂 Project Structure
 
The project follows a strict MVC architectural pattern to separate concerns cleanly:
 
```text
src/main/java/com/example/multithreadtextanalyzer/
├── Launcher.java                 # Main application entry point
├── controllers/                  # JavaFX Controllers
│   ├── MainController.java       # Central UI and thread management
│   ├── ExportController.java     # Handles PDF/Word/TXT exports
│   ├── AlertManager.java         # Custom alert dialogues
│   ├── DragAndDropManager.java   # Drag-and-drop event handling
│   ├── FileListManager.java      # Manages the UI file queue
│   ├── ThemeController.java      # Dark/Light mode toggle
│   └── FileSize.java             # File size formatting utility
├── model/                        # Data Models
│   └── AnalysisResult.java       # Encapsulates file metrics
├── services/                     # Business Logic
│   └── TextAnalyzerService.java  # File parsing and text metrics math
└── tasks/                        # Concurrency
    └── AnalysisTask.java         # Background JavaFX Task for UI updates
```
 
---
 
## ⚙️ Prerequisites and Installation
 
To build and run this project locally, you will need:
 
* **Java Development Kit (JDK) 21** or higher.
* **Git** to clone the repository.
> **Note:** Maven is included via the Maven Wrapper (`mvnw`), so a local Maven installation is not strictly required.
 
### Installation Steps
 
**1. Clone the repository:**
```bash
git clone https://github.com/yourusername/MultiThreadTextAnalyzer.git
cd MultiThreadTextAnalyzer
```
 
**2. Build the project:**
 
Using the included Maven wrapper, download dependencies and compile the project.
 
*Windows:*
```dos
mvnw clean install
```
 
*Mac/Linux:*
```bash
./mvnw clean install
```
 
---
 
## 🚀 Build and Run Commands
 
The project uses the `javafx-maven-plugin` for seamless execution. To launch the application, run the following command from the root directory:
 
*Windows:*
```dos
mvnw javafx:run
```
 
*Mac/Linux:*
```bash
./mvnw javafx:run
```
 
To package the application into a standalone executable/JAR:
```bash
./mvnw clean package
```
 
---
 
## 💡 Usage Examples
 
1. **Importing Files:** Launch the app and either click the **"Select Files"** button or drag and drop `.txt`, `.pdf`, or `.docx` files directly into the left-hand panel.
2. **Analyzing:** Click the **"Start Analysis"** button. The progress bar will update smoothly as the `AnalysisTask` processes each file in the background thread pool.
3. **Viewing Results:** Click on any analyzed file in the list to view its specific metrics (Reading time, unique words, etc.) and its most frequent terms in the right-hand panel.
4. **Exporting:** Use the export buttons to save the displayed analysis table as a styled PDF, Word document, or Text file for your records.
5. **Theming:** Click the moon/sun icon at the top to toggle between **Light** and **Dark** mode.
---
 
## 🛠️ Configuration Details
 
Since this is a desktop client application, there are no external database connections or environment variables (`.env`) required.
 
* **UI Styling:** All visual configurations are managed via `src/main/resources/.../styles/style.css`.
* **Thread Pool:** The application dynamically handles threads. If you wish to configure the maximum concurrent threads, you can modify the `Executors.newFixedThreadPool()` initialization inside `MainController.java`.
---
 
## 🤝 Contributing Guidelines
 
Contributions, issues, and feature requests are welcome! Feel free to check the [issues page](../../issues).
 
1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request
---
 
## 📄 License
 
This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.
 
---
 
## 👥 Authors
 
**Khalil Khalfi** • **Zin Eddine Boudersa** • Group: G01
 
---
 
*Developed with ❤️ using JavaFX.*
