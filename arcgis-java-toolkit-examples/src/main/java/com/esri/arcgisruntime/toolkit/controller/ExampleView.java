package com.esri.arcgisruntime.toolkit.controller;

import com.esri.arcgisruntime.toolkit.model.Example;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;

public class ExampleView extends BorderPane {

    // model
    private final SimpleObjectProperty<Example> selectedExampleProperty = new SimpleObjectProperty<>();

    @FXML
    ScrollPane settingsScrollPane;

    @FXML
    TabPane exampleTabPane;

    @FXML
    ToggleButton settingsButton;

    public ExampleView() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/example_view.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (Exception e) {
            System.out.println(e);
        }

        selectedExampleProperty.addListener(((observable, oldValue, newValue) -> {
            exampleTabPane.getTabs().clear();
            if (newValue.getTabs() != null) {
                exampleTabPane.getTabs().addAll(newValue.getTabs());
            }
            settingsScrollPane.setContent(newValue.getSettings());
            this.setRight(null);
        }));

        settingsButton.selectedProperty().addListener(((observable, oldValue, newValue) -> {
            if (settingsButton.isSelected()) {
                settingsButton.setText("Hide Settings");
                this.setRight(settingsScrollPane);
            } else {
                settingsButton.setText("Show Settings");
                this.setRight(null);
            }
        }));
    }

    public void setSelectedExample(Example selectedExample) {
        selectedExampleProperty.set(selectedExample);
    }
}
