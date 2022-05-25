package com.esri.arcgisruntime.toolkit;

import com.esri.arcgisruntime.toolkit.controller.ExamplesAppController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ExamplesApp extends Application {

    private ExamplesAppController controller;

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/app.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        primaryStage.setTitle("ArcGIS Runtime for Java Toolkit - Examples");
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setWidth(screenBounds.getWidth() * 0.75);
        primaryStage.setHeight(screenBounds.getHeight() * .75);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() {
        controller.terminate();
    }
}
