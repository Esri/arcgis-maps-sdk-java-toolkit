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
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.symbology.ColorUtil;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.Symbol;
import com.esri.arcgisruntime.toolkit.OverviewMap;
import com.esri.arcgisruntime.toolkit.model.Example;
import com.esri.arcgisruntime.toolkit.utils.ExampleUtils;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * An Example Class for the {@link com.esri.arcgisruntime.toolkit.OverviewMap} Toolkit Component. Implements the {@link Example} interface to ensure
 * required methods are implemented. The OverviewMap can be used on a {@link MapView} or {@link SceneView} and so a tab
 * is created for each view. The example can be viewed by running the {@link com.esri.arcgisruntime.toolkit.ExamplesApp},
 * or as a standalone app via the {@link OverviewMapExampleLauncher} class.
 *
 * @since 100.15.0
 */
public class OverviewMapExample extends Application implements Example {

  // a MapView and an OverviewMap to be used on the MapView
  private final MapView mapView = new MapView();
  private final OverviewMap mapViewOverviewMap;
  // a SceneView and an OverviewMap to be used on the SceneView
  private final SceneView sceneView = new SceneView();
  private final OverviewMap sceneViewOverviewMap;
  // other UI components
  private final List<Tab> tabs = new ArrayList<>();
  private final Tab mapViewTab;
  private final Tab sceneViewTab;
  private final VBox settings;
  private final Symbol symbolMVRedBorder =  new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, ColorUtil.colorToArgb(Color.TRANSPARENT),
    new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, ColorUtil.colorToArgb(Color.RED), 1.0f));
  private final Symbol symbolMVGreyFill = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0x50000000,
    new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, ColorUtil.colorToArgb(Color.TRANSPARENT), 2.0f));
  private final Symbol symbolMVBlueBorder = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, ColorUtil.colorToArgb(Color.TRANSPARENT),
    new SimpleLineSymbol(SimpleLineSymbol.Style.DOT, ColorUtil.colorToArgb(Color.BLUE), 4.0f));
  private final Symbol symbolSVRedCross = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CROSS, ColorUtil.colorToArgb(Color.RED), 20);
  private final Symbol symbolSVGreenCircle = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, ColorUtil.colorToArgb(Color.GREEN), 15);
  private final Symbol symbolSVYellowTriangle = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.TRIANGLE, ColorUtil.colorToArgb(Color.YELLOW), 15);

  /**
   * A constructor for the OverviewMapExample that demonstrates how to implement an OverviewMap for a MapView or SceneView.
   *
   * A Tab is created for each view to display in the application and settings are configured for testing and demo
   * purposes.
   *
   * @since 100.15.0
   */
  public OverviewMapExample() {
    // initial viewpoint to use for both mapview and sceneview
    Viewpoint initialViewpoint = new Viewpoint(55.949, -3.194, 1000000);

    // configure MapView tab
    mapView.setMap(new ArcGISMap(BasemapStyle.ARCGIS_IMAGERY));
    // set initial viewpoint
    mapView.setViewpoint(initialViewpoint);
    // instantiate an OverviewMap passing in the MapView
    mapViewOverviewMap = new OverviewMap(mapView);
    var mapViewStackPane = new StackPane();
    // add the MapView and OverviewMap to a StackPane
    mapViewStackPane.getChildren().addAll(mapView, mapViewOverviewMap);
    // set the desired position and padding
    StackPane.setAlignment(mapViewOverviewMap, Pos.BOTTOM_LEFT);
    StackPane.setMargin(mapViewOverviewMap, new Insets(0, 0, 20, 0));
    mapViewTab = ExampleUtils.createTab(mapViewStackPane, "Map");

    // configure SceneView tab
    sceneView.setArcGISScene(new ArcGISScene(BasemapStyle.ARCGIS_IMAGERY));
    // set initial viewpoint
    sceneView.setViewpoint(initialViewpoint);
    // instantiate an OverviewMap passing in the SceneView
    sceneViewOverviewMap = new OverviewMap(sceneView);
    // add the SceneView and OverviewMap to a StackPane
    var sceneViewStackPane = new StackPane();
    sceneViewStackPane.getChildren().addAll(sceneView, sceneViewOverviewMap);
    // set the desired position and padding
    StackPane.setAlignment(sceneViewOverviewMap, Pos.BOTTOM_LEFT);
    StackPane.setMargin(sceneViewOverviewMap, new Insets(0, 0, 20, 0));
    sceneViewTab = ExampleUtils.createTab(sceneViewStackPane, "Scene");

    // add both tabs to the list
    tabs.addAll(List.of(mapViewTab, sceneViewTab));
    // configure settings options
    settings = configureSettings();
  }

  /**
   * Sets up the required UI elements for toggling properties and other settings relating to the Toolkit Component.
   *
   * For the OverviewMap this includes basemap style, symbol style, scale factor and layout settings.
   *
   * @return a VBox containing the settings
   * @since 100.15.0
   */
  private VBox configureSettings() {
    // define specific settings for the OverviewMap
    List<Node> overviewMapSettings = new ArrayList<>();

    // Property settings
    var propertyTitledPane = new TitledPane();
    propertyTitledPane.setExpanded(false);
    propertyTitledPane.setText("Property settings");
    var propertyVBox = new VBox(5);
    propertyTitledPane.setContent(propertyVBox);
    overviewMapSettings.add(propertyTitledPane);
    // Basemap
    var basemapVBox = new VBox(5);
    var basemapLabel = new Label("Change Basemap:");
    ComboBox<BasemapStyle> basemapComboBox = new ComboBox<>();
    basemapComboBox.getItems().addAll(BasemapStyle.ARCGIS_IMAGERY, BasemapStyle.ARCGIS_TOPOGRAPHIC,
      BasemapStyle.ARCGIS_COMMUNITY, BasemapStyle.ARCGIS_CHARTED_TERRITORY, BasemapStyle.ARCGIS_COLORED_PENCIL,
      BasemapStyle.ARCGIS_DARK_GRAY, BasemapStyle.ARCGIS_HILLSHADE_DARK, BasemapStyle.ARCGIS_HILLSHADE_LIGHT,
      BasemapStyle.ARCGIS_LIGHT_GRAY, BasemapStyle.ARCGIS_MIDCENTURY, BasemapStyle.ARCGIS_MODERN_ANTIQUE,
      BasemapStyle.ARCGIS_NAVIGATION, BasemapStyle.ARCGIS_NAVIGATION_NIGHT, BasemapStyle.ARCGIS_NEWSPAPER,
      BasemapStyle.ARCGIS_NOVA, BasemapStyle.ARCGIS_OCEANS, BasemapStyle.ARCGIS_STREETS, BasemapStyle.ARCGIS_TERRAIN,
      BasemapStyle.OSM_STANDARD, BasemapStyle.OSM_DARK_GRAY, BasemapStyle.OSM_LIGHT_GRAY, BasemapStyle.OSM_STREETS);
    basemapComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      mapViewOverviewMap.setBasemap(new Basemap(newValue));
      sceneViewOverviewMap.setBasemap(new Basemap(newValue));
    });
    basemapComboBox.getSelectionModel().select(BasemapStyle.ARCGIS_TOPOGRAPHIC);
    basemapVBox.getChildren().addAll(basemapLabel, basemapComboBox);
    propertyVBox.getChildren().add(basemapVBox);

    // Symbol property
    var symbolVBox = new VBox(5);
    // mapview
    var symbolMapViewLabel = new Label("Change MapView Symbol:");
    ComboBox<Symbol> symbolMapViewComboBox = new ComboBox<>();
    symbolMapViewComboBox.setConverter(new StringConverter<>() {
      @Override
      public String toString(Symbol symbol) {
        if (symbol == symbolMVRedBorder) {
          return "Red outline";
        } else if (symbol == symbolMVGreyFill) {
          return "Grey box";
        } else if (symbol == symbolMVBlueBorder) {
          return "Blue dotted outline";
        } else {
          return "Unidentified symbol";
        }
      }
      @Override
      public Symbol fromString(String string) {
        return null;
      }
    });
    symbolMapViewComboBox.getItems().addAll(symbolMVRedBorder, symbolMVGreyFill, symbolMVBlueBorder);
    symbolMapViewComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
      mapViewOverviewMap.setSymbol(newValue));
    symbolMapViewComboBox.getSelectionModel().select(0);
    symbolMapViewComboBox.disableProperty().bind(mapViewTab.selectedProperty().not());
    symbolVBox.getChildren().addAll(symbolMapViewLabel, symbolMapViewComboBox);
    // sceneview
    var symbolSceneViewLabel = new Label("Change SceneView Symbol:");
    ComboBox<Symbol> symbolSceneViewComboBox = new ComboBox<>();
    symbolSceneViewComboBox.setConverter(new StringConverter<>() {
      @Override
      public String toString(Symbol symbol) {
        if (symbol == symbolSVRedCross) {
          return "Red cross";
        } else if (symbol == symbolSVGreenCircle) {
          return "Green circle";
        } else if (symbol == symbolSVYellowTriangle) {
          return "Yellow triangle";
        } else {
          return "Unidentified symbol";
        }
      }
      @Override
      public Symbol fromString(String string) {
        return null;
      }
    });
    symbolSceneViewComboBox.getItems().addAll(symbolSVRedCross, symbolSVGreenCircle, symbolSVYellowTriangle);
    symbolSceneViewComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
      sceneViewOverviewMap.setSymbol(newValue));
    symbolSceneViewComboBox.getSelectionModel().select(0);
    symbolSceneViewComboBox.disableProperty().bind(sceneViewTab.selectedProperty().not());
    symbolVBox.getChildren().addAll(symbolSceneViewLabel, symbolSceneViewComboBox);

    propertyVBox.getChildren().add(symbolVBox);

    // scale factor
    var scaleFactorVBox = new VBox(5);
    var scaleFactorLabel = new Label("Adjust scale factor:");
    // default is 25.0
    // scale factor must not be less than or equal to 0.0
    var scaleFactorSlider = new Slider(10.0, 100.0, 25.0);
    scaleFactorSlider.setShowTickLabels(true);
    scaleFactorSlider.setMajorTickUnit(5);
    scaleFactorSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
      // update both OverviewMaps
      mapViewOverviewMap.setScaleFactor(newValue.doubleValue());
      sceneViewOverviewMap.setScaleFactor(newValue.doubleValue());
    });
    scaleFactorVBox.getChildren().addAll(scaleFactorLabel, scaleFactorSlider);
    propertyVBox.getChildren().add(scaleFactorVBox);

    // Layout settings
    var layoutTitledPane = new TitledPane();
    layoutTitledPane.setExpanded(false);
    layoutTitledPane.setText("Layout settings");
    var layoutVBox = new VBox(5);
    layoutTitledPane.setContent(layoutVBox);
    // resize - default height is two thirds of width
    var sizeLabel = new Label("Resize:");
    var sizeSlider = new Slider(100, 500, 200);
    sizeSlider.setShowTickLabels(true);
    sizeSlider.setMajorTickUnit(500);
    sizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
      // update both OverviewMaps
      mapViewOverviewMap.setPrefSize(newValue.doubleValue(), (newValue.doubleValue() * 0.66));
      sceneViewOverviewMap.setPrefSize(newValue.doubleValue(), (newValue.doubleValue() * 0.66));
    });
    layoutVBox.getChildren().addAll(sizeLabel, sizeSlider);
    // position
    var positionLabel = new Label("Re-position:");
    ComboBox<Pos> positionComboBox = new ComboBox<>();
    positionComboBox.getItems().addAll(Pos.TOP_LEFT, Pos.TOP_CENTER, Pos.TOP_RIGHT, Pos.CENTER, Pos.BOTTOM_LEFT,
      Pos.BOTTOM_CENTER, Pos.BOTTOM_RIGHT);
    positionComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      // update both OverviewMaps
      StackPane.setAlignment(mapViewOverviewMap, newValue);
      StackPane.setAlignment(sceneViewOverviewMap, newValue);
      if (newValue == Pos.BOTTOM_LEFT || newValue == Pos.BOTTOM_CENTER || newValue == Pos.BOTTOM_RIGHT) {
        StackPane.setMargin(mapViewOverviewMap, new Insets(0, 0, 20, 0));
        StackPane.setMargin(sceneViewOverviewMap, new Insets(0, 0, 20, 0));
      } else {
        StackPane.setMargin(mapViewOverviewMap, new Insets(0, 0, 0, 0));
        StackPane.setMargin(sceneViewOverviewMap, new Insets(0, 0, 0, 0));
      }
    });
    positionComboBox.getSelectionModel().select(Pos.BOTTOM_LEFT);
    layoutVBox.getChildren().addAll(positionLabel, positionComboBox);
    overviewMapSettings.add(layoutTitledPane);

    // return a fully configured VBox of the settings
    return ExampleUtils.createSettings(getName(), overviewMapSettings);
  }

  @Override
  public String getName() {return "Overview Map";}

  @Override
  public String getDescription() {
    return "Indicates the viewpoint of the main map/scene view.";
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

  @Override
  public void stop() {
    mapView.dispose();
    sceneView.dispose();
  }
}
