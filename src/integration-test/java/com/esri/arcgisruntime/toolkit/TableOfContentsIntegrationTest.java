package com.esri.arcgisruntime.toolkit;

import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SceneView;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

/**
 * Integration tests for BookmarkView.
 */
public class TableOfContentsIntegrationTest extends ApplicationTest {

  private StackPane stackPane;

  @Override
  public void start(Stage primaryStage) {
    stackPane = new StackPane();
    Scene fxScene = new Scene(stackPane);
    primaryStage.setWidth(500);
    primaryStage.setHeight(500);
    primaryStage.setScene(fxScene);
    primaryStage.show();
    primaryStage.toFront();
  }

  @After
  public void cleanup() throws Exception {
    FxToolkit.cleanupStages();
  }

  /**
   * Tests that every operational layer in the map has its name displayed.
   */
  @Test
  public void itemForEveryOperationalLayerInMap() {
    // given a map view containing a map with bookmarks
    MapView mapView = new MapView();
    Platform.runLater(() -> stackPane.getChildren().add(mapView));

    ArcGISMap map = new ArcGISMap(Basemap.createImagery());
    final String WILDFIRE_RESPONSE_URL = "https://sampleserver6.arcgisonline" +
        ".com/arcgis/rest/services/Wildfire/FeatureServer/0";
    FeatureTable featureTable = new ServiceFeatureTable(WILDFIRE_RESPONSE_URL);
    FeatureLayer featureLayer = new FeatureLayer(featureTable);
    map.getOperationalLayers().add(featureLayer);
    mapView.setMap(map);

    // when the bookmarks view is added with the map view
    TableOfContents tableOfContents = new TableOfContents(mapView);
    tableOfContents.setMaxSize(100, 100);
    StackPane.setAlignment(tableOfContents, Pos.TOP_RIGHT);
    StackPane.setMargin(tableOfContents, new Insets(10));
    Platform.runLater(() -> stackPane.getChildren().add(tableOfContents));

    sleep(6000);

    // every bookmark's name will be displayed in the view
    map.getOperationalLayers().forEach(layer -> clickOn(layer.getName()));
  }

  /**
   * Tests that every operational layer in the map has its name displayed.
   */
  @Test
  public void itemForEveryOperationalLayerInScene() {
    // given a map view containing a map with bookmarks
    SceneView sceneView = new SceneView();
    Platform.runLater(() -> stackPane.getChildren().add(sceneView));

    ArcGISScene scene = new ArcGISScene(Basemap.createImagery());
    final String WILDFIRE_RESPONSE_URL = "https://sampleserver6.arcgisonline" +
        ".com/arcgis/rest/services/Wildfire/FeatureServer/0";
    FeatureTable featureTable = new ServiceFeatureTable(WILDFIRE_RESPONSE_URL);
    FeatureLayer featureLayer = new FeatureLayer(featureTable);
    scene.getOperationalLayers().add(featureLayer);
    sceneView.setArcGISScene(scene);

    // when the bookmarks view is added with the map view
    TableOfContents tableOfContents = new TableOfContents(sceneView);
    tableOfContents.setMaxSize(100, 100);
    StackPane.setAlignment(tableOfContents, Pos.TOP_RIGHT);
    StackPane.setMargin(tableOfContents, new Insets(10));
    Platform.runLater(() -> stackPane.getChildren().add(tableOfContents));

    sleep(6000);

    // every bookmark's name will be displayed in the view
    scene.getOperationalLayers().forEach(layer -> clickOn(layer.getName()));
  }
}