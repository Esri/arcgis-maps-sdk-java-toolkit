package com.esri.arcgisruntime.toolkit.examples.utils;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class ExampleUtils {

    public static Tab createTab(Node node, String text) {
        Tab tab = new Tab();
        tab.setContent(node);
        tab.setText(text);
        tab.setClosable(false);
        return tab;
    }

    public static VBox createSettings(String name, ArrayList<Node> settings) {
        VBox vBox = new VBox(10);
        vBox.setMinWidth(200);
        vBox.getStyleClass().add("settings");
        vBox.setAlignment(Pos.TOP_LEFT);
        Label label = new Label(name + " Settings");
        label.getStyleClass().add("h3");
        vBox.getChildren().add(label);
        settings.forEach(node -> {
            node.getStyleClass().add("individual-setting");
            vBox.getChildren().add(node);
        });
        return vBox;
    }
}
