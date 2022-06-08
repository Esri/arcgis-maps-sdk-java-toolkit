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

package com.esri.arcgisruntime.toolkit.examples;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.toolkit.Compass;
import com.esri.arcgisruntime.toolkit.model.Example;
import com.esri.arcgisruntime.toolkit.utils.ExampleUtils;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompassExample extends Application implements Example {

    private final MapView mapView = new MapView();
    private final SceneView sceneView = new SceneView();
    private final List<Tab> tabs = new ArrayList<>();
    private final VBox settings;
    private final Compass mapViewCompass;
    private final Compass sceneViewCompass;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ExampleUtils.setupIndividualExampleStage(primaryStage, new CompassExample());
    }

    public CompassExample() {

        // configure mapview tab
        mapView.setMap(new ArcGISMap(BasemapStyle.ARCGIS_IMAGERY));
        StackPane mapViewStackPane = new StackPane();
        mapViewCompass = new Compass(mapView);
        mapViewCompass.setAutoHide(false);
        mapViewStackPane.getChildren().addAll(mapView, mapViewCompass);
        StackPane.setAlignment(mapViewCompass, Pos.TOP_LEFT);
        Tab mapViewTab = ExampleUtils.createTab(mapViewStackPane, "Map");

        // configure sceneview tab
        sceneView.setArcGISScene(new ArcGISScene(BasemapStyle.ARCGIS_IMAGERY));
        StackPane sceneViewStackPane = new StackPane();
        sceneViewCompass = new Compass(sceneView);
        sceneViewCompass.setAutoHide(false);
        StackPane.setAlignment(sceneViewCompass, Pos.TOP_LEFT);
        sceneViewStackPane.getChildren().addAll(sceneView, sceneViewCompass);
        Tab sceneViewTab = ExampleUtils.createTab(sceneViewStackPane, "Scene");

        tabs.addAll(Arrays.asList(mapViewTab, sceneViewTab));

        // configure settings options
        settings = configureSettings();
    }

    private VBox configureSettings() {
        ArrayList<Node> requiredSettings = new ArrayList<>();

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
        requiredSettings.add(autoHideSettings);

        // Adjust Heading
        VBox headingSettings = new VBox();
        Label headingLabel = new Label("Heading:");
        Label headingText = new Label("Use 'A' and 'D' on your keyboard to rotate the MapView / SceneView. The Compass will follow. Click on the Compass to reset heading to 0.0 (North).");
        headingSettings.getChildren().addAll(headingLabel, headingText);
        requiredSettings.add(headingSettings);

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
        sizeSlider.valueProperty().addListener((obvs, ov, nv) -> {
            mapViewCompass.setPrefSize(nv.doubleValue(), nv.doubleValue());
            sceneViewCompass.setPrefSize(nv.doubleValue(), nv.doubleValue());
        });
        layoutVBox.getChildren().addAll(sizeLabel, sizeSlider);
        // position
        Label positionLabel = new Label("Re-position:");
        ComboBox<Pos> positionComboBox = new ComboBox<>();
        positionComboBox.getItems().addAll(Pos.TOP_LEFT, Pos.TOP_CENTER, Pos.TOP_RIGHT, Pos.CENTER, Pos.BOTTOM_LEFT, Pos.BOTTOM_CENTER, Pos.BOTTOM_RIGHT);
        positionComboBox.getSelectionModel().selectedItemProperty().addListener((obvs, ov, nv) -> {
            StackPane.setAlignment(mapViewCompass, nv);
            StackPane.setAlignment(sceneViewCompass, nv);
        });
        positionComboBox.getSelectionModel().select(Pos.TOP_LEFT);
        layoutVBox.getChildren().addAll(positionLabel, positionComboBox);
        requiredSettings.add(layoutTitledPane);

        return ExampleUtils.createSettings(getName(), requiredSettings);
    }

    @Override
    public String getName() {return "Compass";}

    @Override
    public VBox getSettings() {
        return settings;
    }

    @Override
    public List<Tab> getTabs() {
        return tabs;
    }

    @Override
    public String getDescription() {
        return "Shows the current viewpoint heading. Can be clicked to reorient the view to north.";
    }

    @Override
    public List<GeoView> getGeoViews() {
        return Arrays.asList(mapView, sceneView);
    }



    @Override
    public void stop() {
        mapView.dispose();
        sceneView.dispose();
    }
}
