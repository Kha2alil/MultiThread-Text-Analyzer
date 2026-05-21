package com.example.multithreadtextanalyzer.controllers;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.ToggleButton;

public class ThemeController {

    private BorderPane mainRoot;
    private ToggleButton toggleBtn;
    private ImageView themeIcon;

    public ThemeController(BorderPane mainRoot, ToggleButton toggleBtn, ImageView themeIcon) {
        this.mainRoot = mainRoot;
        this.toggleBtn = toggleBtn;
        this.themeIcon = themeIcon;
    }

    public void toggleTheme() {
        if (toggleBtn.isSelected()) {
            mainRoot.getStyleClass().add("dark-mode");
            toggleBtn.setText("Light Mode");
            themeIcon.setImage(new Image(getClass().getResourceAsStream("/com/example/multithreadtextanalyzer/assets/top/light.png")));
        } else {
            mainRoot.getStyleClass().remove("dark-mode");
            toggleBtn.setText("Night Mode");
            themeIcon.setImage(new Image(getClass().getResourceAsStream("/com/example/multithreadtextanalyzer/assets/top/dark.png")));
        }
    }
}
