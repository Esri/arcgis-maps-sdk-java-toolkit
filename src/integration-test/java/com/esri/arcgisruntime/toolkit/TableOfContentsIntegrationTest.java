package com.esri.arcgisruntime.toolkit;

import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.*;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SceneView;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.testfx.api.FxRobotException;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.Arrays;
import java.util.Set;

import static com.esri.arcgisruntime.toolkit.skins.TableOfContentsTreeViewSkin.LayerContentTreeCell;
import static com.esri.arcgisruntime.toolkit.skins.TableOfContentsTreeViewSkin.LayerContentTreeItem;

/**
 * Integration tests for TableOfContents.
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
    // given a map view containing a map with an operational layer
    MapView mapView = new MapView();
    Platform.runLater(() -> stackPane.getChildren().add(mapView));

    ArcGISMap map = new ArcGISMap(Basemap.createImagery());
    final String WILDFIRE_RESPONSE_URL = "https://sampleserver6.arcgisonline" +
        ".com/arcgis/rest/services/Wildfire/FeatureServer/0";
    FeatureTable featureTable = new ServiceFeatureTable(WILDFIRE_RESPONSE_URL);
    FeatureLayer featureLayer = new FeatureLayer(featureTable);
    map.getOperationalLayers().add(featureLayer);
    mapView.setMap(map);

    // when the table of contents is added
    TableOfContents tableOfContents = new TableOfContents(mapView);
    tableOfContents.setMaxSize(150, 100);
    StackPane.setAlignment(tableOfContents, Pos.TOP_RIGHT);
    StackPane.setMargin(tableOfContents, new Insets(10));
    Platform.runLater(() -> stackPane.getChildren().add(tableOfContents));

    sleep(6000);

    // every layer's name will be displayed in the view
    map.getOperationalLayers().forEach(layer -> clickOn(layer.getName()));
  }

  /**
   * Tests that every operational layer in the scene has its name displayed.
   */
  @Test
  public void itemForEveryOperationalLayerInScene() {
    // given a scene view containing a scene with an operational layer
    SceneView sceneView = new SceneView();
    Platform.runLater(() -> stackPane.getChildren().add(sceneView));

    ArcGISScene scene = new ArcGISScene(Basemap.createImagery());
    final String WILDFIRE_RESPONSE_URL = "https://sampleserver6.arcgisonline" +
        ".com/arcgis/rest/services/Wildfire/FeatureServer/0";
    FeatureTable featureTable = new ServiceFeatureTable(WILDFIRE_RESPONSE_URL);
    FeatureLayer featureLayer = new FeatureLayer(featureTable);
    scene.getOperationalLayers().add(featureLayer);
    sceneView.setArcGISScene(scene);

    // when the table of contents is added
    TableOfContents tableOfContents = new TableOfContents(sceneView);
    tableOfContents.setMaxSize(150, 100);
    StackPane.setAlignment(tableOfContents, Pos.TOP_RIGHT);
    StackPane.setMargin(tableOfContents, new Insets(10));
    Platform.runLater(() -> stackPane.getChildren().add(tableOfContents));

    sleep(6000);

    // every layer's name will be displayed in the view
    scene.getOperationalLayers().forEach(layer -> clickOn(layer.getName()));
  }

  /**
   * Tests that every item which can change its visibility can have its visibility toggled via its checkbox.
   */
  @Test
  public void toggleVisibilityWithCheckbox() {
    // given a map view containing a map with an operational layer
    MapView mapView = new MapView();
    Platform.runLater(() -> stackPane.getChildren().add(mapView));

    ArcGISMap map = new ArcGISMap(Basemap.createImagery());
    final String WILDFIRE_RESPONSE_URL = "https://sampleserver6.arcgisonline" +
        ".com/arcgis/rest/services/Wildfire/FeatureServer/0";
    FeatureTable featureTable = new ServiceFeatureTable(WILDFIRE_RESPONSE_URL);
    FeatureLayer featureLayer = new FeatureLayer(featureTable);
    map.getOperationalLayers().add(featureLayer);
    mapView.setMap(map);

    TableOfContents tableOfContents = new TableOfContents(mapView);
    tableOfContents.setMaxSize(150, 100);
    StackPane.setAlignment(tableOfContents, Pos.TOP_RIGHT);
    StackPane.setMargin(tableOfContents, new Insets(10));
    Platform.runLater(() -> stackPane.getChildren().add(tableOfContents));

    sleep(6000);

    // when the item's checkbox is deselected
    Set<CheckBox> visibilityCheckboxes = lookup(n -> n instanceof CheckBox).queryAll();
    CheckBox checkBox = (CheckBox) visibilityCheckboxes.toArray()[1];
    clickOn(checkBox);

    sleep(1000);

    // the layer will not be visible
    Assertions.assertFalse(featureLayer.isVisible());

    // when the item's checkbox is selected
    clickOn(checkBox);

    sleep(1000);

    // the layer will be visible
    Assertions.assertTrue(featureLayer.isVisible());
  }

  /**
   * Tests that the checkbox is disabled when visibility cannot be changed.
   */
  @Test
  public void basemapLayers() {
    // given a map view containing a map with a basemap
    MapView mapView = new MapView();
    Platform.runLater(() -> stackPane.getChildren().add(mapView));

    ArcGISTiledLayer tiledLayer = new ArcGISTiledLayer("http://services.arcgisonline" +
        ".com/ArcGIS/rest/services/World_Street_Map/MapServer");
    ArcGISMap map = new ArcGISMap(new Basemap(tiledLayer));
    mapView.setMap(map);

    TableOfContents tableOfContents = new TableOfContents(mapView);
    tableOfContents.setMaxSize(150, 100);
    StackPane.setAlignment(tableOfContents, Pos.TOP_RIGHT);
    StackPane.setMargin(tableOfContents, new Insets(10));
    Platform.runLater(() -> stackPane.getChildren().add(tableOfContents));

    sleep(6000);

    ArcGISSublayer subLayer = tiledLayer.getSublayers().get(0);
    Assertions.assertFalse(subLayer.canChangeVisibility());

    // double-click parent to expand
    doubleClickOn(tiledLayer.getName());

    // the sublayer's item should not have a checkbox
    Set<CheckBox> visibilityCheckboxes = lookup(n -> n instanceof CheckBox).queryAll();
    Assertions.assertEquals(1, visibilityCheckboxes.size());

    sleep(1000);
  }

  /**
   * Tests group layers in the table of contents.
   */
  @Test
  public void groupLayers() {
    // given a scene view containing a scene with a group layer
    SceneView sceneView = new SceneView();
    Platform.runLater(() -> stackPane.getChildren().add(sceneView));

    ArcGISScene scene = new ArcGISScene(Basemap.createImagery());
    ArcGISSceneLayer devOne = new ArcGISSceneLayer("https://tiles.arcgis.com/tiles/P3ePLMYs2RVChkJx/arcgis/rest/services/DevA_Trees/SceneServer");
    ArcGISSceneLayer devTwo = new ArcGISSceneLayer("https://tiles.arcgis.com/tiles/P3ePLMYs2RVChkJx/arcgis/rest/services/DevA_BuildingShells/SceneServer");
    FeatureTable featureTable = new ServiceFeatureTable("https://services.arcgis" +
        ".com/P3ePLMYs2RVChkJx/arcgis/rest/services/DevA_Pathways/FeatureServer/1");
    FeatureLayer featureLayer = new FeatureLayer(featureTable);

    GroupLayer groupLayer = new GroupLayer();
    groupLayer.setName("Group: Dev A");
    groupLayer.getLayers().addAll(Arrays.asList(devOne, devTwo, featureLayer));
    scene.getOperationalLayers().add(groupLayer);
    sceneView.setArcGISScene(scene);

    featureLayer.addDoneLoadingListener(() -> sceneView.setViewpointCamera(new Camera(featureLayer.getFullExtent().getCenter(), 700, 0, 60, 0)));

    // when the table of contents is added
    TableOfContents tableOfContents = new TableOfContents(sceneView);
    tableOfContents.setMaxSize(150, 300);
    StackPane.setAlignment(tableOfContents, Pos.TOP_RIGHT);
    StackPane.setMargin(tableOfContents, new Insets(10));
    Platform.runLater(() -> stackPane.getChildren().add(tableOfContents));

    sleep(6000);

    // the group layer's name and its children's names should be viewable
    clickOn(groupLayer.getName());
    clickOn(groupLayer.getName()); // double-click to expand
    groupLayer.getLayers().forEach(layer -> clickOn(layer.getName()));

    Set<CheckBox> visibilityCheckboxes = lookup(n -> n instanceof CheckBox).queryAll();
    Assertions.assertEquals(5, visibilityCheckboxes.size());

    // turn off parent
    CheckBox checkBox = (CheckBox) visibilityCheckboxes.toArray()[0];
    clickOn(checkBox);

    sleep(1000);

    Assertions.assertFalse(groupLayer.isVisible());
  }

  /**
   * Tests that one can create their own custom layer tree with most of the behavior of table of contents without a
   * map, scene, or geoview.
   */
  @Test
  public void customLayerTree() {
    // given a normal TreeView with some layer contents
    TreeView<LayerContent> layerTree = new TreeView<>();
    layerTree.setMaxSize(150, 300);
    Platform.runLater(() -> stackPane.getChildren().add(layerTree));
    StackPane.setAlignment(layerTree, Pos.TOP_RIGHT);
    StackPane.setMargin(layerTree, new Insets(10));

    GroupLayer groupLayer = new GroupLayer();
    groupLayer.setName("Group");
    ArcGISSceneLayer devOne = new ArcGISSceneLayer("https://tiles.arcgis.com/tiles/P3ePLMYs2RVChkJx/arcgis/rest/services/DevA_Trees/SceneServer");
    ArcGISSceneLayer devTwo = new ArcGISSceneLayer("https://tiles.arcgis.com/tiles/P3ePLMYs2RVChkJx/arcgis/rest/services/DevA_BuildingShells/SceneServer");
    FeatureTable featureTable = new ServiceFeatureTable("https://services.arcgis" +
        ".com/P3ePLMYs2RVChkJx/arcgis/rest/services/DevA_Pathways/FeatureServer/1");
    FeatureLayer featureLayer = new FeatureLayer(featureTable);
    groupLayer.getLayers().addAll(Arrays.asList(devOne, devTwo, featureLayer));

    TreeItem<LayerContent> root = new TreeItem<>();
    Platform.runLater(() -> layerTree.setRoot(root));
    layerTree.setShowRoot(false);

    // when the cell factory is LayerContentTreeCell and the items are LayerContentTreeItems
    layerTree.setCellFactory(param -> new LayerContentTreeCell());
    root.getChildren().add(new LayerContentTreeItem(groupLayer));

    sleep(5000);

    // then the items should show the layer content's name and a checkbox to change the visibility
    doubleClickOn("Group");
    groupLayer.getLayers().forEach(l -> clickOn(l.getName()));

    // when the cell factory is LayerContentTreeCell and the items are normal TreeItems
    root.getChildren().clear();
    root.getChildren().add(new TreeItem<>(groupLayer));

    WaitForAsyncUtils.waitForFxEvents();

    // then the child layers will not be shown
    doubleClickOn("Group");
    Assertions.assertThrows(FxRobotException.class, () -> clickOn(devOne.getName()));

    sleep(1000);
  }
}