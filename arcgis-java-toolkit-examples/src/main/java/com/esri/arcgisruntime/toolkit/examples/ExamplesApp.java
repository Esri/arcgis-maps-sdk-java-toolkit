package com.esri.arcgisruntime.toolkit.examples;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ExamplesApp extends Application {

    @Override
    public void start(Stage primaryStage) {

        ArcGISRuntimeEnvironment.setApiKey(System.getProperty("apiKey"));

        BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(borderPane);
        scene.getStylesheets().add(getClass().getResource("/app.css").toExternalForm());


        // set title, size and scene to stage
        primaryStage.setTitle("ArcGIS Runtime for Java Toolkit");

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setWidth(screenBounds.getWidth() * 0.75);
        primaryStage.setHeight(screenBounds.getHeight() * .75);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() {
    }
}