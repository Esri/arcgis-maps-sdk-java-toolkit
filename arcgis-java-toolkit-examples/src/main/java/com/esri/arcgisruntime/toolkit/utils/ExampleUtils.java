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

package com.esri.arcgisruntime.toolkit.utils;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.toolkit.controller.ExampleView;
import com.esri.arcgisruntime.toolkit.model.Example;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Defines util methods to support the configuration of Examples that are displayed within an
 * {@link com.esri.arcgisruntime.toolkit.controller.ExampleView}.
 *
 * @since 100.15.0
 */
public class ExampleUtils {

    /**
     * Creates a Tab from the provided Node and label and sets the required configuration.
     *
     * @param node the node to display in the Tab
     * @param tabLabel the String to display on the tab
     * @return the configured Tab
     * @since 100.15.0
     */
    public static Tab createTab(Node node, String tabLabel) {
        Tab tab = new Tab();
        // set the provided node as the tabs content
        tab.setContent(node);
        // set the tab text as the provided label
        tab.setText(tabLabel);
        // prevent the tab from being closed
        tab.setClosable(false);
        return tab;
    }

    /**
     * Creates a VBox containing the provided settings and sets all the required configuration for consistency.
     *
     * @param exampleName the name of the example
     * @param settings a list of the Nodes used to create the settings for the specific example
     * @return the configured VBox
     * @since 100.15.0
     */
    public static VBox createSettings(String exampleName, ArrayList<Node> settings) {
        // configure the VBox
        VBox vBox = new VBox(10);
        vBox.getStyleClass().add("settings");
        vBox.setAlignment(Pos.TOP_LEFT);
        // set a heading using the example name
        Label label = new Label(exampleName + " Settings");
        label.getStyleClass().add("h3");
        vBox.getChildren().add(label);
        // for each provided setting add a styleclass and add to the VBox
        settings.forEach(node -> {
            node.getStyleClass().add("individual-setting");
            vBox.getChildren().add(node);
        });
        return vBox;
    }

    /**
     * Used to configure the JavaFX Stage when the Example is run directly via the Launcher Class.
     *
     * @param primaryStage the stage
     * @param example the example
     * @since 100.15.0
     */
    public static void setupIndividualExampleStage(Stage primaryStage, Example example) {
        // add the ExampleView to a StackPane
        StackPane stackPane = new StackPane();
        ExampleView exampleView = new ExampleView();
        exampleView.setSelectedExample(example);
        stackPane.getChildren().add(exampleView);

        // set the stage title
        primaryStage.setTitle("ArcGIS Runtime for Java Toolkit Examples - " + example.getName());
        // initially launch the stage at 75% of the screen size
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setWidth(screenBounds.getWidth() * 0.75);
        primaryStage.setHeight(screenBounds.getHeight() * .75);
        // configure the Scene and add to the Stage
        var scene = new Scene(stackPane);
        scene.getStylesheets().add("/styles/example.css");
        scene.getStylesheets().add("/styles/style.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Used to configure the API Key when the Example is run directly via the Launcher Class. This must be called before
     * the app is launched.
     *
     * Note: it is not best practice to store API keys in source code.
     * An API key is required to enable access to services, web maps, and web scenes hosted in ArcGIS Online.
     * If you haven't already, go to your developer dashboard to get your API key.
     * Please refer to
     * <a href="https://developers.arcgis.com/java/get-started/">https://developers.arcgis.com/java/get-started/</a>
     * for more information.
     *
     * @since 100.15.0
     */
    public static void configureAPIKeyForRunningStandAloneExample() {
        // configure warning message to provide details about API Keys if required
        var logger = Logger.getLogger(ExampleUtils.class.getName());
        var apiKeyWarning = "An API key is required to enable access to services, web maps, and web scenes hosted in " +
                "ArcGIS Online.\n If you haven't already, go to your developer dashboard to get your API key.\n Please " +
                "refer to https://developers.arcgis.com/java/get-started/ for more information.\nYou can set your API " +
                "Key in ExampleUtils or add an apiKey property to " +
                System.getProperty("user.home") + "\\.gradle\\gradle.properties.\n Note: it is not best practice to " +
                "store API keys in source code.";
        try {
            // loads the gradle.properties file
            Properties prop = new Properties();
            prop.load(new FileInputStream(System.getProperty("user.home") + "/.gradle/gradle.properties"));
            // set the API Key
            // Note: it is not best practice to store API keys in source code
            ArcGISRuntimeEnvironment.setApiKey(prop.getProperty("apiKey"));

            if (prop.getProperty("apiKey") == null) {
                // if the API Key is not configured correctly in the gradle.properties file, display the warning message
                logger.warning(apiKeyWarning);
            }
        } catch (Exception e) {
            logger.warning("Exception details: " + e.getMessage() + apiKeyWarning);
        }
    }
}
