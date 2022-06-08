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

/**
 * An Example Class for the {@link Compass} Toolkit Component. Implements the {@link Example} interface to ensure
 * required methods are implemented. The Compass can be used on a {@link MapView} or {@link SceneView} and so a tab
 * is created for each view. The example can be viewed by running the {@link com.esri.arcgisruntime.toolkit.ExamplesApp},
 * or as a standalone app via the {@link CompassExampleLauncher} class.
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
        tabs.addAll(Arrays.asList(mapViewTab, sceneViewTab));

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
        ArrayList<Node> compassSettings = new ArrayList<>();

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
        sizeSlider.valueProperty().addListener((obvs, ov, nv) -> {
            // update both compasses
            mapViewCompass.setPrefSize(nv.doubleValue(), nv.doubleValue());
            sceneViewCompass.setPrefSize(nv.doubleValue(), nv.doubleValue());
        });
        layoutVBox.getChildren().addAll(sizeLabel, sizeSlider);
        // position
        Label positionLabel = new Label("Re-position:");
        ComboBox<Pos> positionComboBox = new ComboBox<>();
        positionComboBox.getItems().addAll(Pos.TOP_LEFT, Pos.TOP_CENTER, Pos.TOP_RIGHT, Pos.CENTER, Pos.BOTTOM_LEFT,
                Pos.BOTTOM_CENTER, Pos.BOTTOM_RIGHT);
        positionComboBox.getSelectionModel().selectedItemProperty().addListener((obvs, ov, nv) -> {
            // update both compasses
            StackPane.setAlignment(mapViewCompass, nv);
            StackPane.setAlignment(sceneViewCompass, nv);
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
        return Arrays.asList(mapView, sceneView);
    }

    @Override
    public void start(Stage primaryStage) {
        // sets up the individual stage if run via the Launcher class
        ExampleUtils.setupIndividualExampleStage(primaryStage, new CompassExample());
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void stop() {
        mapView.dispose();
        sceneView.dispose();
    }
}
