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
import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.toolkit.FloorFilter;
import com.esri.arcgisruntime.toolkit.model.Example;
import com.esri.arcgisruntime.toolkit.utils.ExampleUtils;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class FloorFilterExample extends Application implements Example {

    private final MapView mapView = new MapView();
    private final List<Tab> tabs = new ArrayList<>();
    private final VBox settings;
    private final FloorFilter floorFilter;
    private final BorderPane borderPane;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ExampleUtils.setupIndividualExampleStage(primaryStage, new FloorFilterExample());
    }

    public FloorFilterExample() {
        // configure mapview tab
        ArcGISMap map = new ArcGISMap("https://www.arcgis.com/home/item.html?id=f133a698536f44c8884ad81f80b6cfc7");
        mapView.setMap(map);
        borderPane = new BorderPane();
        floorFilter = new FloorFilter(mapView);
        floorFilter.minHeightProperty().bind(borderPane.heightProperty());
        borderPane.setLeft(floorFilter);
        borderPane.setCenter(mapView);
        Tab mapViewTab = ExampleUtils.createTab(borderPane, "Map");

        tabs.add(mapViewTab);

        // configure settings options
        settings = configureSettings();
    }

    private VBox configureSettings() {
        ArrayList<Node> requiredSettings = new ArrayList<>();

        // automatic selection mode
        VBox autoSelectVBox = new VBox(5);
        Label autoSelectLabel = new Label("Automatic selection mode:");
        ComboBox<FloorFilter.AutomaticSelectionMode> autoSelectComboBox = new ComboBox<>();
        autoSelectComboBox.getItems().addAll(FloorFilter.AutomaticSelectionMode.ALWAYS, FloorFilter.AutomaticSelectionMode.ALWAYS_NON_CLEARING, FloorFilter.AutomaticSelectionMode.NEVER);
        autoSelectComboBox.getSelectionModel().selectedItemProperty().addListener((obvs, ov, nv) -> {
            floorFilter.setAutomaticSelectionMode(nv);
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
        sizeSlider.valueProperty().addListener((obvs, ov, nv) -> {
            floorFilter.setPrefSize(nv.doubleValue(), nv.doubleValue());
        });
        layoutVBox.getChildren().addAll(sizeLabel, sizeSlider);
        // position
        Label positionLabel = new Label("Re-position:");
        ComboBox<String> positionComboBox = new ComboBox<>();
        positionComboBox.getItems().addAll("Left", "Right");
        positionComboBox.getSelectionModel().selectedItemProperty().addListener((obvs, ov, nv) -> {
            if(nv.equals("Left")) {
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

        return ExampleUtils.createSettings(getName(), requiredSettings);
    }

    @Override
    public String getName() {return "Floor Filter";}

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
        return "Shows sites and facilities, and enables toggling the visibility of levels on floor aware maps and scenes.";
    }

    @Override
    public List<GeoView> getGeoViews() {
        return List.of(mapView);
    }

    @Override
    public void stop() {
        mapView.dispose();
    }
}
