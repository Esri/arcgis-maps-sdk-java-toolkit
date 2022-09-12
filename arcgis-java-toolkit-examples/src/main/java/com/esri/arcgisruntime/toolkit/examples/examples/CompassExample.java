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

package com.esri.arcgisruntime.toolkit.examples.examples;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.toolkit.Compass;
import com.esri.arcgisruntime.toolkit.examples.ExamplesApp;
import com.esri.arcgisruntime.toolkit.examples.model.Example;
import com.esri.arcgisruntime.toolkit.examples.utils.ExampleUtils;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * An Example Class for the {@link Compass} Toolkit Component. Implements the {@link Example} interface to ensure
 * required methods are implemented. The Compass can be used on a {@link MapView} or {@link SceneView} and so a tab
 * is created for each view. The example can be viewed by running the {@link ExamplesApp},
 * or as a standalone app via {@link CompassExample}.
 *
 * @since 100.15.0
 */
public class CompassExample extends Application implements Example {

    // a MapView and a Compass to be used on the MapView
    private final MapView mapView = new MapView();
    private final Compass mapViewCompass;
    // a SceneView and a Compass to be used on the SceneView
    private final SceneView sceneView = new SceneView();
    private final Compass sceneViewCompass;
    // the required UI components
    private final List<Tab> tabs = new ArrayList<>();
    private final VBox settings;

    /**
     * A constructor for the CompassExample that demonstrates how to implement a Compass for a MapView or SceneView.
     *
     * A Tab is created for each view to display in the application and settings are configured for testing and demo
     * purposes.
     *
     * @since 100.15.0
     */
    public CompassExample() {
        // configure MapView tab
        mapView.setMap(new ArcGISMap(BasemapStyle.ARCGIS_IMAGERY));
        StackPane mapViewStackPane = new StackPane();
        // instantiate a Compass passing in the MapView
        mapViewCompass = new Compass(mapView);
        // set the autohide property to false initially so the Compass is always visible
        mapViewCompass.setAutoHide(false);
        // add the MapView and Compass to the StackPane
        mapViewStackPane.getChildren().addAll(mapView, mapViewCompass);
        StackPane.setAlignment(mapViewCompass, Pos.TOP_LEFT);
        Tab mapViewTab = ExampleUtils.createTab(mapViewStackPane, "Map");

        // configure SceneView tab
        sceneView.setArcGISScene(new ArcGISScene(BasemapStyle.ARCGIS_IMAGERY));
        StackPane sceneViewStackPane = new StackPane();
        // instantiate a Compass passing in the SceneView
        sceneViewCompass = new Compass(sceneView);
        // set the autohide property to false initially so the compass is always visible
        sceneViewCompass.setAutoHide(false);
        // add the SceneView and Compass to the StackPane
        sceneViewStackPane.getChildren().addAll(sceneView, sceneViewCompass);
        StackPane.setAlignment(sceneViewCompass, Pos.TOP_LEFT);
        Tab sceneViewTab = ExampleUtils.createTab(sceneViewStackPane, "Scene");

        // add both tabs to the list
        tabs.addAll(List.of(mapViewTab, sceneViewTab));

        // configure settings options
        settings = configureSettings();
    }

    /**
     * Sets up the required UI elements for toggling properties and other settings relating to the Toolkit Component.
     *
     * For the Compass this includes auto-hide, size and position.
     *
     * @return a VBox containing the settings
     * @since 100.15.0
     */
    private VBox configureSettings() {
        // define specific settings for the Compass
        List<Node> compassSettings = new ArrayList<>();

        // Auto Hide
        HBox autoHideSettings = new HBox(10);
        autoHideSettings.setAlignment(Pos.CENTER_LEFT);
        Label autohideLabel = new Label("Toggle auto-hide:");
        ToggleGroup autoHideToggleGroup = new ToggleGroup();
        RadioButton trueAutoHide = new RadioButton();
        trueAutoHide.setText("True");
        RadioButton falseAutoHide = new RadioButton();
        falseAutoHide.setText("False");
        trueAutoHide.setToggleGroup(autoHideToggleGroup);
        falseAutoHide.setToggleGroup(autoHideToggleGroup);
        trueAutoHide.selectedProperty().addListener((i) -> {
            mapViewCompass.setAutoHide(true);
            sceneViewCompass.setAutoHide(true);
        });
        falseAutoHide.selectedProperty().addListener((i) -> {
            mapViewCompass.setAutoHide(false);
            sceneViewCompass.setAutoHide(false);
        });
        falseAutoHide.setSelected(true);
        autoHideSettings.getChildren().addAll(autohideLabel, trueAutoHide, falseAutoHide);
        compassSettings.add(autoHideSettings);

        // Adjust Heading
        VBox headingSettings = new VBox();
        Label headingLabel = new Label("Heading:");
        Label headingText = new Label("Use 'A' and 'D' on your keyboard to rotate the MapView / SceneView. The " +
                "Compass will follow. Click on the Compass to reset heading to 0.0 (North).");
        headingSettings.getChildren().addAll(headingLabel, headingText);
        compassSettings.add(headingSettings);

        // Layout settings
        TitledPane layoutTitledPane = new TitledPane();
        layoutTitledPane.setExpanded(false);
        layoutTitledPane.setText("Layout settings");
        VBox layoutVBox = new VBox(5);
        layoutTitledPane.setContent(layoutVBox);
        // resize
        Label sizeLabel = new Label("Resize:");
        Slider sizeSlider = new Slider(0, 500, 100);
        sizeSlider.setShowTickLabels(true);
        sizeSlider.setMajorTickUnit(500);
        sizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            // update both compasses
            mapViewCompass.setPrefSize(newValue.doubleValue(), newValue.doubleValue());
            sceneViewCompass.setPrefSize(newValue.doubleValue(), newValue.doubleValue());
        });
        layoutVBox.getChildren().addAll(sizeLabel, sizeSlider);
        // position
        Label positionLabel = new Label("Re-position:");
        ComboBox<Pos> positionComboBox = new ComboBox<>();
        positionComboBox.getItems().addAll(Pos.TOP_LEFT, Pos.TOP_CENTER, Pos.TOP_RIGHT, Pos.CENTER, Pos.BOTTOM_LEFT,
                Pos.BOTTOM_CENTER, Pos.BOTTOM_RIGHT);
        positionComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // update both compasses
            StackPane.setAlignment(mapViewCompass, newValue);
            StackPane.setAlignment(sceneViewCompass, newValue);
        });
        positionComboBox.getSelectionModel().select(Pos.TOP_LEFT);
        layoutVBox.getChildren().addAll(positionLabel, positionComboBox);
        compassSettings.add(layoutTitledPane);

        // return a fully configured VBox of the settings
        return ExampleUtils.createSettings(getName(), compassSettings);
    }

    @Override
    public String getName() {return "Compass";}

    @Override
    public String getDescription() {
        return "Shows the current viewpoint heading. Can be clicked to reorient the view to north.";
    }

    @Override
    public VBox getSettings() {
        return settings;
    }

    @Override
    public List<Tab> getTabs() {
        return tabs;
    }


    @Override
    public List<GeoView> getGeoViews() {
        return List.of(mapView, sceneView);
    }

    @Override
    public void start(Stage primaryStage) {
        // sets up the individual stage if run via the Launcher class
        ExampleUtils.setupIndividualExampleStage(primaryStage, this);
    }

    public static void main(String[] args) {
      // configure the API Key
      // authentication with an API key or named user is required to access basemaps and other location services
      ExampleUtils.configureAPIKeyForRunningStandAloneExample();
      Application.launch(args);
    }

    @Override
    public void stop() {
        mapView.dispose();
        sceneView.dispose();
    }
}
