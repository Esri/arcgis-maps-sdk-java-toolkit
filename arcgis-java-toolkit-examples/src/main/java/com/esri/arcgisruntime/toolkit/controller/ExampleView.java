/*
 COPYRIGHT 1995-2022 ESRI

 TRADE SECRETS: ESRI PROPRIETARY AND CONFIDENTIAL
 Unpublished material - all rights reserved under the
 Copyright Laws of the United States.

 For additional information, contact:
 Environmental Systems Research Institute, Inc.
 Attn: Contracts Dept
 380 New York Street
 Redlands, California, USA 92373

 email: contracts@esri.com
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
