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

package com.esri.arcgisruntime.toolkit.examples.utils;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.toolkit.examples.controller.ExampleView;
import com.esri.arcgisruntime.toolkit.examples.model.Example;
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
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Defines util methods to support the configuration of Examples that are displayed within an
 * {@link ExampleView}.
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
    public static VBox createSettings(String exampleName, List<Node> settings) {
        // configure the VBox
        VBox vBox = new VBox(10);
        vBox.getStyleClass().add("settings");
        vBox.setAlignment(Pos.TOP_LEFT);
        // set a heading using the example name
        Label label = new Label(exampleName + " Settings");
        label.getStyleClass().add("arcgis-toolkit-java-h3");
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
        primaryStage.setTitle("ArcGIS Maps SDK for Java Toolkit Examples - " + example.getName());
        // initially launch the stage at 75% of the screen size
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setWidth(screenBounds.getWidth() * 0.75);
        primaryStage.setHeight(screenBounds.getHeight() * .75);
        // configure the Scene and add to the Stage
        var scene = new Scene(stackPane);
        // individual stylesheets can be commented out for testing purposes
        scene.getStylesheets().add("/com/esri/arcgisruntime/toolkit/examples/styles/example.css");
        scene.getStylesheets().add("/com/esri/arcgisruntime/toolkit/examples/styles/style.css");
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
