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
            primaryStage.setTitle("ArcGIS Maps SDK for Java Toolkit - Examples");
            // on initial launch the stage is set to 75% of the screen size
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            primaryStage.setWidth(screenBounds.getWidth() * 0.75);
            primaryStage.setHeight(screenBounds.getHeight() * .75);

            // configures the scene and sets it to the stage
            var scene = new Scene(root);
            // individual stylesheets can be commented out for testing purposes
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
