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

import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.security.UserCredential;
import com.esri.arcgisruntime.symbology.ColorUtil;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.toolkit.UtilityNetworkTraceTool;
import com.esri.arcgisruntime.toolkit.model.Example;
import com.esri.arcgisruntime.toolkit.utils.ExampleUtils;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * An Example Class for the {@link UtilityNetworkTraceTool} Toolkit Component. Implements the {@link Example} interface
 * to ensure required methods are implemented. The example can be viewed by running the
 * {@link com.esri.arcgisruntime.toolkit.ExamplesApp}, or as a standalone app via the
 * {@link UtilityNetworkTraceToolExampleLauncher} class.
 *
 * @since 100.15.0
 */
public class UtilityNetworkTraceToolExample extends Application implements Example {

    // used for setting up the Utility Network Trace Tool
    private UtilityNetworkTraceTool utilityNetworkTraceTool;
    private ArcGISMap webMap;
    private final MapView mapView = new MapView();

    // public data used for the example
    private final String webMapURL = "https://www.arcgis.com/home/item.html?id=471eb0bf37074b1fbb972b1da70fb310";
    private final String featureServiceURL = "https://sampleserver7.arcgisonline.com/portal/sharing/rest";
    private final UserCredential userCred = new UserCredential("viewer01", "I68VGU^nMurF");

    // used for creating the example
    Stage primaryStage;
    private final List<Tab> tabs = new ArrayList<>();
    private final VBox settings = new VBox(5);
    private final BorderPane mapViewBorderPane = new BorderPane();

    /**
     * A constructor for the UtilityNetworkTraceExample that demonstrates how to implement a UtilityNetworkTrace
     * for a MapView.
     *
     * A Tab is created for the view to display in the application and settings are configured for testing and demo
     * purposes.
     *
     * @since 100.15.0
     */
    public UtilityNetworkTraceToolExample() {
        StackPane stackPane = new StackPane();
        // display progress indicator while map loads
        var progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(true);

        // setup the portal and set the webmap to the mapview
        try {
            var portal = new Portal(featureServiceURL);
            portal.setCredential(userCred);
            portal.addDoneLoadingListener(() -> {
                if (portal.getLoadStatus() == LoadStatus.LOADED) {
                    webMap = new ArcGISMap(webMapURL);
                    mapView.setMap(webMap);

                    // create a utility network trace tool passing in the mapview
                    utilityNetworkTraceTool = new UtilityNetworkTraceTool(mapView);

                    // display the utility network trace tool in the UI
                    // here the tool is displayed in a BorderPane alongside the MapView
                    mapViewBorderPane.setRight(utilityNetworkTraceTool);

                    webMap.addDoneLoadingListener(() -> {
                        if (webMap.getLoadStatus() == LoadStatus.LOADED) {
                            // when the map loads hide the progress indicator
                            progressIndicator.setVisible(false);
                            // configure settings options once map has loaded so data can be accessed
                            var configuredSettings = configureSettings();
                            settings.getChildren().add(configuredSettings);
                        } else if (webMap.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
                            progressIndicator.setVisible(false);
                            throw webMap.getLoadError();
                        }
                    });
                } else if (portal.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
                    progressIndicator.setVisible(false);
                    throw portal.getLoadError();
                }
            });
            // load the portal
            portal.loadAsync();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        stackPane.getChildren().addAll(mapView, progressIndicator);
        mapViewBorderPane.setCenter(stackPane);
        Tab mapViewTab = ExampleUtils.createTab(mapViewBorderPane, "Map");

        // add the tab to the list
        tabs.add(mapViewTab);
    }

    /**
     * Sets up the required UI elements for toggling properties and other settings relating to the Toolkit Component.
     *
     * For the UtilityNetwork this includes public properties, custom styles, size and position.
     *
     * @return a VBox containing the settings
     * @since 100.15.0
     */
    private VBox configureSettings() {
        ArrayList<Node> requiredSettings = new ArrayList<>();

        // property settings
        TitledPane propertyTitledPane = new TitledPane();
        propertyTitledPane.setExpanded(false);
        propertyTitledPane.setText("Properties");
        VBox propertyVBox = new VBox(5);
        propertyTitledPane.setContent(propertyVBox);
        requiredSettings.add(propertyTitledPane);
        // adding starting points
        Label isAddingStartingPoints = new Label("Toggle isAddingStartingPoints:");
        RadioButton addStartingPointT = new RadioButton("True");
        addStartingPointT.setUserData(true);
        RadioButton addStartingPointF = new RadioButton("False");
        addStartingPointF.setUserData(false);
        var addStartingPointToggleGroup = new ToggleGroup();
        addStartingPointT.setToggleGroup(addStartingPointToggleGroup);
        addStartingPointF.setToggleGroup(addStartingPointToggleGroup);
        addStartingPointToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.getUserData() instanceof Boolean) {
                utilityNetworkTraceTool.isAddingStartingPointsProperty().set((Boolean) newValue.getUserData());
            }
        });
        utilityNetworkTraceTool.isAddingStartingPointsProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue) {
                    addStartingPointToggleGroup.selectToggle(addStartingPointT);
                } else {
                    addStartingPointToggleGroup.selectToggle(addStartingPointF);
                }
            }
        }));
        if (utilityNetworkTraceTool.isAddingStartingPointsProperty().get()) {
            addStartingPointToggleGroup.selectToggle(addStartingPointT);
        } else {
            addStartingPointToggleGroup.selectToggle(addStartingPointF);
        }
        propertyVBox.getChildren().addAll(isAddingStartingPoints, addStartingPointT, addStartingPointF);

        // auto zoom to result
        Label isAutoZoomToResults = new Label("Toggle isAutoZoomToResults");
        RadioButton isAutoZoomT = new RadioButton("True");
        isAutoZoomT.setUserData(true);
        RadioButton isAutoZoomF = new RadioButton("False");
        isAutoZoomF.setUserData(false);
        var isAutoZoomToggleGroup = new ToggleGroup();
        isAutoZoomT.setToggleGroup(isAutoZoomToggleGroup);
        isAutoZoomF.setToggleGroup(isAutoZoomToggleGroup);
        isAutoZoomToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.getUserData() instanceof Boolean) {
                utilityNetworkTraceTool.autoZoomToResultsProperty().set((Boolean) newValue.getUserData());
            }
        });
        utilityNetworkTraceTool.autoZoomToResultsProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue) {
                    isAutoZoomToggleGroup.selectToggle(isAutoZoomT);
                } else {
                    isAutoZoomToggleGroup.selectToggle(isAutoZoomF);
                }
            }
        }));
        if (utilityNetworkTraceTool.autoZoomToResultsProperty().get()) {
            isAutoZoomToggleGroup.selectToggle(isAutoZoomT);
        } else {
            isAutoZoomToggleGroup.selectToggle(isAutoZoomF);
        }
        propertyVBox.getChildren().addAll(isAutoZoomToResults, isAutoZoomT, isAutoZoomF);


        // update starting point symbol color
        Label updateStartingPointSymbolColor = new Label("Update starting point color");
        ColorPicker colorPicker =
                new ColorPicker(ColorUtil.argbToColor(
                        ((SimpleMarkerSymbol) utilityNetworkTraceTool.getStartingPointSymbol()).getColor()));
        colorPicker.setOnAction(event -> {
            ((SimpleMarkerSymbol) utilityNetworkTraceTool.getStartingPointSymbol()).setColor(
                    ColorUtil.colorToArgb(colorPicker.getValue()));
        });
        propertyVBox.getChildren().addAll(updateStartingPointSymbolColor, colorPicker);

        // map settings
        Label setNewMap = new Label("Set new ARCGISMap on MapView.");
        TitledPane mapTitledPane = new TitledPane();
        mapTitledPane.setExpanded(false);
        mapTitledPane.setText("Map settings");
        VBox mapVBox = new VBox(5);
        mapTitledPane.setContent(mapVBox);
        Label mapDescription = new Label("Also calls refresh() to reload data.");
        ComboBox<ArcGISMap> mapComboBox = new ComboBox<>();
        var map = new ArcGISMap(BasemapStyle.ARCGIS_IMAGERY);
        mapComboBox.getItems().addAll(webMap, map);
        mapComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(ArcGISMap arcGISMap) {
                if (arcGISMap == webMap) {
                    return "Webmap with utility networks";
                } else {
                    return "Map with no utility networks";
                }
            }
            @Override
            public ArcGISMap fromString(String string) {
                return null;
            }
        });
        mapComboBox.getSelectionModel().select(webMap);
        mapComboBox.getSelectionModel().selectedItemProperty().addListener((obvs, ov, nv) -> {
            if(nv.equals(webMap)) {
                mapView.setMap(webMap);
                utilityNetworkTraceTool.refresh();
            } else {
                mapView.setMap(map);
                utilityNetworkTraceTool.refresh();
            }
        });
        mapVBox.getChildren().addAll(setNewMap, mapDescription, mapComboBox);
        requiredSettings.add(mapTitledPane);

        // style settings
        Label styleSettingsLabel = new Label("Show/Hide default styles.");
        TitledPane stylesTitledPane = new TitledPane();
        stylesTitledPane.setExpanded(false);
        stylesTitledPane.setText("Syle settings");
        VBox stylesVBox = new VBox(5);
        stylesTitledPane.setContent(stylesVBox);
        RadioButton showCustomStyles = new RadioButton("Show custom styles");
        showCustomStyles.setUserData(true);
        RadioButton hideCustomStyles = new RadioButton("Hide custom styles");
        hideCustomStyles.setUserData(false);
        var stylesToggleGroup = new ToggleGroup();
        showCustomStyles.setToggleGroup(stylesToggleGroup);
        hideCustomStyles.setToggleGroup(stylesToggleGroup);
        showCustomStyles.setSelected(true);
        stylesToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.getUserData() instanceof Boolean) {
                if ((Boolean) newValue.getUserData()) {
                    primaryStage.getScene().getStylesheets().add("/styles/style.css");
                } else {
                    primaryStage.getScene().getStylesheets().remove("/styles/style.css");
                }
            }
        });
        stylesVBox.getChildren().addAll(styleSettingsLabel, showCustomStyles, hideCustomStyles);
        requiredSettings.add(stylesTitledPane);

        // Layout settings
        TitledPane layoutTitledPane = new TitledPane();
        layoutTitledPane.setExpanded(false);
        layoutTitledPane.setText("Layout settings");
        VBox layoutVBox = new VBox(5);
        layoutTitledPane.setContent(layoutVBox);
        Label sizeLabel = new Label("Resize:");
        Slider sizeSlider = new Slider(120, 500, 220);
        sizeSlider.setShowTickLabels(true);
        sizeSlider.setMajorTickUnit(500);
        sizeSlider.valueProperty().addListener((obvs, ov, nv) -> {
            utilityNetworkTraceTool.setPrefSize(nv.doubleValue(), nv.doubleValue());
        });
        layoutVBox.getChildren().addAll(sizeLabel, sizeSlider);
        // position
        Label positionLabel = new Label("Re-position:");
        ComboBox<String> positionComboBox = new ComboBox<>();
        positionComboBox.getItems().addAll("Left", "Right");
        positionComboBox.getSelectionModel().selectedItemProperty().addListener((obvs, ov, nv) -> {
            if(nv.equals("Left")) {
                mapViewBorderPane.setRight(null);
                mapViewBorderPane.setLeft(utilityNetworkTraceTool);
            } else {
                mapViewBorderPane.setLeft(null);
                mapViewBorderPane.setRight(utilityNetworkTraceTool);
            }
        });
        positionComboBox.getSelectionModel().select("Right");
        layoutVBox.getChildren().addAll(positionLabel, positionComboBox);
        requiredSettings.add(layoutTitledPane);

        return ExampleUtils.createSettings(getName(), requiredSettings);
    }

    @Override
    public VBox getSettings() {
        return settings;
    }

    @Override
    public String getName() {
        return "Utility Network Trace";
    }

    @Override
    public List<Tab> getTabs() {
        return tabs;
    }

    @Override
    public String getDescription() {
        return "Use named trace configurations defined in a web map to perform connected trace operations and " +
                "compare results.";
    }

    @Override
    public List<GeoView> getGeoViews() { return List.of(mapView); }

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        ExampleUtils.setupIndividualExampleStage(primaryStage, this);
    }

    @Override
    public void stop() {
        mapView.dispose();
    }
}