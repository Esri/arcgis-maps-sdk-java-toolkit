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

public class ExampleUtils {

    public static Tab createTab(Node node, String text) {
        Tab tab = new Tab();
        tab.setContent(node);
        tab.setText(text);
        tab.setClosable(false);
        return tab;
    }

    public static VBox createSettings(String name, ArrayList<Node> settings) {
        VBox vBox = new VBox(10);
        vBox.setMinWidth(200);
        vBox.getStyleClass().add("settings");
        vBox.setAlignment(Pos.TOP_LEFT);
        Label label = new Label(name + " Settings");
        label.getStyleClass().add("h3");
        vBox.getChildren().add(label);
        settings.forEach(node -> {
            node.getStyleClass().add("individual-setting");
            vBox.getChildren().add(node);
        });
        return vBox;
    }

    public static void setupIndividualExampleStage(Stage primaryStage, Example example) {
        StackPane stackPane = new StackPane();
        ExampleView exampleView = new ExampleView();
        stackPane.getChildren().add(exampleView);

        primaryStage.setTitle("ArcGIS Runtime for Java Toolkit Examples - " + example.getName());
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setWidth(screenBounds.getWidth() * 0.75);
        primaryStage.setHeight(screenBounds.getHeight() * .75);
        var scene = new Scene(stackPane);
        scene.getStylesheets().add("/styles/example.css");
        scene.getStylesheets().add("/styles/style.css");
        primaryStage.setScene(scene);
        primaryStage.show();

        exampleView.setSelectedExample(example);
    }

    public static void configureAPIKeyForRunningStandAloneExample() {
        var logger = Logger.getLogger(ExampleUtils.class.getName());
        var apiKeyWarning = "An API key is required to enable access to services, web maps, and web scenes hosted in " +
                "ArcGIS Online.\n If you haven't already, go to your developer dashboard to get your API key.\n Please " +
                "refer to https://developers.arcgis.com/java/get-started/ for more information.\nYou can set your API " +
                "Key in ExampleUtils or add an apiKey property to " +
                System.getProperty("user.home") + "\\.gradle\\gradle.properties.\n Note: it is not best practice to " +
                "store API keys in source code.";
        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream(System.getProperty("user.home") + "/.gradle/gradle.properties"));
            // Note: it is not best practice to store API keys in source code.
            // An API key is required to enable access to services, web maps, and web scenes hosted in ArcGIS Online.
            // If you haven't already, go to your developer dashboard to get your API key.
            // Please refer to https://developers.arcgis.com/java/get-started/ for more information
            ArcGISRuntimeEnvironment.setApiKey(prop.getProperty("apiKey"));
            if (prop.get("apiKey") == null) {
                logger.warning(apiKeyWarning);
            }
        } catch (Exception e) {
            logger.warning("Exception details: " + e.getMessage() + apiKeyWarning);
        }
    }
}
