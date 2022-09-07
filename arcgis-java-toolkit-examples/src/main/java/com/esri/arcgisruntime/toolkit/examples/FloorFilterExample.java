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

package com.esri.arcgisruntime.toolkit.examples;

import java.util.ArrayList;
import java.util.List;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.toolkit.FloorFilter;
import com.esri.arcgisruntime.toolkit.model.Example;
import com.esri.arcgisruntime.toolkit.utils.ExampleUtils;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * An Example Class for the {@link FloorFilter} Toolkit Component. Implements the {@link Example} interface to ensure
 * required methods are implemented. The example can be viewed by running the
 * {@link com.esri.arcgisruntime.toolkit.ExamplesApp}, or as a standalone app via the {@link FloorFilterExampleLauncher}
 * class.
 *
 * @since 100.15.0
 */
public class FloorFilterExample extends Application implements Example {

  // a MapView and a FloorFilter to be used on the MapView
  private final MapView mapView = new MapView();
  private final FloorFilter floorFilter;

  // the required UI components
  private final List<Tab> tabs = new ArrayList<>();
  private final VBox settings;
  private final BorderPane borderPane;

  /**
   * A constructor for the FloorFilterExample that demonstrates how to implement a FloorFilter for a MapView.
   *
   * A Tab is created for the view to display in the application and settings are configured for testing and demo
   * purposes.
   *
   * @since 100.15.0
   */
  public FloorFilterExample() {
    // configure mapview tab
    ArcGISMap map = new ArcGISMap("https://www.arcgis.com/home/item.html?id=f133a698536f44c8884ad81f80b6cfc7");
    mapView.setMap(map);
    borderPane = new BorderPane();
    // instantiate a FloorFilter passing in the MapView
    floorFilter = new FloorFilter(mapView);
    // configure the UI for displaying the FloorFilter and MapView
    // here the FloorFilter is displayed in a BorderPane alongside the MapView
    floorFilter.minHeightProperty().bind(borderPane.heightProperty());
    borderPane.setLeft(floorFilter);
    borderPane.setCenter(mapView);
    Tab mapViewTab = ExampleUtils.createTab(borderPane, "Map");

    // add the tab to the list
    tabs.add(mapViewTab);

    // configure settings options
    settings = configureSettings();
  }

  /**
   * Sets up the required UI elements for toggling properties and other settings relating to the Toolkit Component.
   *
   * For the FloorFilter this includes AutomaticSelectionMode, size and position.
   *
   * @return a VBox containing the settings
   * @since 100.15.0
   */
  private VBox configureSettings() {
    // define specific settings for the FloorFilter
    List<Node> requiredSettings = new ArrayList<>();

    // automatic selection mode
    VBox autoSelectVBox = new VBox(5);
    Label autoSelectLabel = new Label("Automatic selection mode:");
    ComboBox<FloorFilter.AutomaticSelectionMode> autoSelectComboBox = new ComboBox<>();
    autoSelectComboBox.getItems().addAll(FloorFilter.AutomaticSelectionMode.ALWAYS,
      FloorFilter.AutomaticSelectionMode.ALWAYS_NON_CLEARING, FloorFilter.AutomaticSelectionMode.NEVER);
    autoSelectComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      floorFilter.setAutomaticSelectionMode(newValue);
    });
    autoSelectComboBox.getSelectionModel().select(FloorFilter.AutomaticSelectionMode.ALWAYS);
    autoSelectVBox.getChildren().addAll(autoSelectLabel, autoSelectComboBox);
    requiredSettings.add(autoSelectVBox);

    // Layout settings
    TitledPane layoutTitledPane = new TitledPane();
    layoutTitledPane.setExpanded(false);
    layoutTitledPane.setText("Layout settings");
    VBox layoutVBox = new VBox(5);
    layoutTitledPane.setContent(layoutVBox);
    // resize
    Label sizeLabel = new Label("Resize:");
    Slider sizeSlider = new Slider(120, 500, 220);
    sizeSlider.setShowTickLabels(true);
    sizeSlider.setMajorTickUnit(500);
    sizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
      floorFilter.setPrefSize(newValue.doubleValue(), newValue.doubleValue());
    });
    layoutVBox.getChildren().addAll(sizeLabel, sizeSlider);
    // position
    Label positionLabel = new Label("Re-position:");
    ComboBox<String> positionComboBox = new ComboBox<>();
    positionComboBox.getItems().addAll("Left", "Right");
    positionComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      if(newValue.equals("Left")) {
        borderPane.setRight(null);
        borderPane.setLeft(floorFilter);
      } else {
        borderPane.setLeft(null);
        borderPane.setRight(floorFilter);
      }
    });
    positionComboBox.getSelectionModel().select("Left");
    layoutVBox.getChildren().addAll(positionLabel, positionComboBox);
    requiredSettings.add(layoutTitledPane);

    // return a fully configured VBox of the settings
    return ExampleUtils.createSettings(getName(), requiredSettings);
  }

  @Override
  public String getName() {return "Floor Filter";}

  @Override
  public String getDescription() {
    return "Shows sites and facilities, and enables toggling the visibility of levels on floor aware maps and scenes.";
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
    return List.of(mapView);
  }

  @Override
  public void start(Stage primaryStage) {
    // sets up the individual stage if run via the Launcher class
    ExampleUtils.setupIndividualExampleStage(primaryStage, this);
  }

  public static void main(String[] args) { Application.launch(args); }

  @Override
  public void stop() {
    mapView.dispose();
  }
}
