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
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.toolkit.Scalebar;
import com.esri.arcgisruntime.toolkit.model.Example;
import com.esri.arcgisruntime.toolkit.utils.ExampleUtils;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * An Example Class for the {@link Scalebar} Toolkit Component. Implements the {@link Example} interface to ensure
 * required methods are implemented. The Scalebar can be used on a {@link MapView} and so a tab is created for this view.
 * The example can be viewed by running the {@link com.esri.arcgisruntime.toolkit.ExamplesApp}, or as a standalone app
 * via the {@link ScalebarExampleLauncher} class.
 *
 * @since 100.15.0
 */
public class ScalebarExample  extends Application implements Example {

  // a MapView and a Scalebar to be used on the MapView
  private final MapView mapView = new MapView();
  private final Scalebar scalebar;
  // the required UI components
  private final List<Tab> tabs = new ArrayList<>();
  private final VBox settings;

  /**
   * A constructor for the ScalebarExample that demonstrates how to implement a Scalebar for a MapView.
   *
   * A Tab is created for each view to display in the application and settings are configured for testing and demo
   * purposes.
   *
   * @since 100.15.0
   */
  public ScalebarExample() {
    // configure MapView tab
    mapView.setMap(new ArcGISMap(BasemapStyle.ARCGIS_IMAGERY));
    var stackPane = new StackPane();
    // instantiate a Scalebar passing in the MapView
    scalebar = new Scalebar(mapView);
    // add the MapView and Scalebar to the StackPane
    stackPane.getChildren().addAll(mapView, scalebar);
    StackPane.setAlignment(scalebar, Pos.BOTTOM_CENTER);
    Tab mapViewTab = ExampleUtils.createTab(stackPane, "Map");

    // add both tabs to the list
    tabs.add(mapViewTab);

    // configure settings options
    settings = configureSettings();
  }

  /**
   * Sets up the required UI elements for toggling properties and other settings relating to the Toolkit Component.
   *
   * For the Scalebar this includes // TODO: enter settings details.
   *
   * @return a VBox containing the settings
   * @since 100.15.0
   */
  private VBox configureSettings() {
    // define specific settings for the Compass
    List<Node> scalebarSettings = new ArrayList<>();

    // return a fully configured VBox of the settings
    return ExampleUtils.createSettings(getName(), scalebarSettings);
  }

  @Override
  public String getName() { return "Scalebar"; }

  @Override
  public String getDescription() { return "Shows a ruler with units proportional to the map's current scale."; }

  @Override
  public List<Tab> getTabs() { return tabs; }

  @Override
  public VBox getSettings() { return settings; }

  @Override
  public List<GeoView> getGeoViews() { return List.of(mapView); }

  /**
   * Opens and runs application.
   *
   * @param args arguments passed to this application
   */
  public static void main(String[] args) {
    // configure the API Key
    // authentication with an API key or named user is required to access basemaps and other location services
    ExampleUtils.configureAPIKeyForRunningStandAloneExample();
    Application.launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    // sets up the individual stage if run via the Launcher class
    ExampleUtils.setupIndividualExampleStage(primaryStage, this);
  }
}
