/*
 * Copyright 2022 Esri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.esri.arcgisruntime.toolkit.controller;

import com.esri.arcgisruntime.toolkit.model.Example;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;

/**
 * A custom BorderPane used to display an example either within the Example App or as a standalone via the individual
 * component Application. The layout is configured via example_view.fxml.
 *
 * @since 100.15.0
 */
public class ExampleView extends BorderPane {

    private final SimpleObjectProperty<Example> selectedExampleProperty = new SimpleObjectProperty<>();
    @FXML private ScrollPane settingsScrollPane;
    @FXML private TabPane exampleTabPane;
    @FXML private ToggleButton settingsButton;

    /**
     * Constructor for the ExampleView. Loads the FXML file and sets the controller. Configures properties for the view.
     *
     * @since 100.15.0
     */
    public ExampleView() {
        // load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/example_view.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();

            // when a new example is selected, update the UI to display it
            selectedExampleProperty.addListener(((observable, oldValue, newValue) -> {
                // configure the tab pane to contain all tabs associated with the newly selected example
                exampleTabPane.getTabs().clear();
                if (newValue.getTabs() != null) {
                    exampleTabPane.getTabs().addAll(newValue.getTabs());
                }
                // configure the settings pane but hide it initially
                settingsScrollPane.setContent(newValue.getSettings());
                this.setRight(null);
                settingsButton.selectedProperty().set(false);
            }));

            // configure the settings button to show/hide the settings pane
            settingsButton.selectedProperty().addListener(((observable, oldValue, newValue) -> {
                if (settingsButton.isSelected()) {
                    settingsButton.setText("Hide Settings");
                    this.setRight(settingsScrollPane);
                } else {
                    settingsButton.setText("Show Settings");
                    this.setRight(null);
                }
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the selected example to the selected example property.
     *
     * @param selectedExample the example to be set
     * @since 100.15.0
     */
    public void setSelectedExample(Example selectedExample) {
        selectedExampleProperty.set(selectedExample);
    }
}
