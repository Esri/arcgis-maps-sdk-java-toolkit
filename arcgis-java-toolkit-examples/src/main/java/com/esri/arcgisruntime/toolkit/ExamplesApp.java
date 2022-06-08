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

package com.esri.arcgisruntime.toolkit;

import com.esri.arcgisruntime.toolkit.controller.ExamplesAppController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * The main application class for the ExamplesApp. The Controller Class is {@link ExamplesAppController}.
 *
 * @since 100.15.0
 */
public class ExamplesApp extends Application {

    private ExamplesAppController controller;

    @Override
    public void start(Stage primaryStage) {
        // loads the FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/app.fxml"));
        try {
            Parent root = loader.load();
            controller = loader.getController();
            // sets the stage title
            primaryStage.setTitle("ArcGIS Runtime for Java Toolkit - Examples");
            // on initial launch the stage is set to 75% of the screen size
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            primaryStage.setWidth(screenBounds.getWidth() * 0.75);
            primaryStage.setHeight(screenBounds.getHeight() * .75);
//            primaryStage.setWidth(200);
//            primaryStage.setHeight(300);

            // configures the scene and sets it to the stage
            var scene = new Scene(root);
            scene.getStylesheets().add("/styles/style.css");
            scene.getStylesheets().add("/styles/app.css");
            scene.getStylesheets().add("/styles/example.css");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() {
        controller.terminate();
    }
}
