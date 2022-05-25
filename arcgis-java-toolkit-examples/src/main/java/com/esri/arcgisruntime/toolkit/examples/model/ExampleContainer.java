package com.esri.arcgisruntime.toolkit.examples.model;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class ExampleContainer extends BorderPane {

    TabPane tabPane = new TabPane();
    ScrollPane settings = new ScrollPane();

    public ExampleContainer(Scene scene) {
        ToggleButton button = new ToggleButton(">>");
        button.setSelected(true);
        button.selectedProperty().addListener((i) -> {
            if (button.isSelected()) {
                button.setText(">>");
                this.setRight(settings);
            } else {
                button.setText("<<");
                this.setRight(null);
            }
        });
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(tabPane, button);
        StackPane.setAlignment(tabPane, Pos.TOP_LEFT);
        StackPane.setAlignment(button, Pos.TOP_RIGHT);
        this.setCenter(stackPane);

        settings.setFitToWidth(true);
        settings.setFitToHeight(true);
        settings.prefWidthProperty().bind(scene.widthProperty().multiply(0.25));
        this.setRight(settings);
    }

    public void setExample(Example example) {
        tabPane.getTabs().clear();
        tabPane.getTabs().addAll(example.getTabs());
        settings.setContent(example.getSettings());
    }

    public BorderPane getBorderPane() {
        return this;
    }
}
