package com.esri.arcgisruntime.toolkit.examples;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.toolkit.examples.model.Example;
import com.esri.arcgisruntime.toolkit.examples.model.ExampleContainer;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExamplesApp extends Application {

    private final SimpleObjectProperty<Example> selectedExample = new SimpleObjectProperty<>();
    private final List<Example> examples = new ArrayList<>();
    private final VBox homepage = new VBox(20);
    private ComboBox<Example> menuComboBox;

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

        ExampleContainer exampleContainer = new ExampleContainer(scene);
        setupExamples();
        setupHomePage();

        HBox hbox = new HBox(50);

        selectedExample.addListener((observableValue, oldValue, newValue) -> {
            borderPane.setCenter(exampleContainer.getBorderPane());
            exampleContainer.setExample(newValue);
        });

        menuComboBox = new ComboBox<>();
        menuComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Example example) {
                if (example == null) {
                    return null;
                } else {
                    return example.getExampleName();
                }
            }

            @Override
            public Example fromString(String string) {
                return null;
            }
        });

        menuComboBox.setCellFactory(lv -> {
            final ListCell<Example> cell = new ListCell<>() {
                @Override
                public void updateItem(Example item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(item != null ? item.getExampleName() : null);
                }
            };
            Region icon = new Region();
            icon.getStyleClass().add("icon");
            cell.setGraphic(icon);
            cell.setGraphicTextGap(20);
            return cell;
        });

        menuComboBox.getSelectionModel().selectedItemProperty().addListener((obvs, old, nv) -> {
            selectedExample.set(nv);
        });
        menuComboBox.setPromptText("Select Tool");

        examples.forEach(example -> menuComboBox.getItems().add(example));
        hbox.getChildren().addAll(menuComboBox);
        hbox.getStyleClass().add("header-bar");
        hbox.setAlignment(Pos.CENTER);

        borderPane.setCenter(homepage);

        borderPane.setTop(hbox);
//        menuComboBox.getSelectionModel().select(0);
    }

    private void setupExamples() {
        // TODO: automate population of examples
        Example compassExample = new CompassExample();
        Example floorFilterExample = new FloorFilterExample();
        examples.addAll(Arrays.asList(compassExample, floorFilterExample));
    }

    private void setupHomePage() {
        homepage.getStyleClass().add("landing-page");
        homepage.setAlignment(Pos.TOP_CENTER);
        Label home = new Label("ArcGIS Runtime for Java Toolkit");
        home.getStyleClass().add("h1");
        Label description = new Label("This app provides a simple way to view all of the components available within the Toolkit, simply select one from the dropdown menu above. Some components are compatible with both MapViews and SceneViews, others have a more custom layout. This will be demonstrated via the available tabs. In addition, each component has a series of settings, and possible test cases, to demonstrate implementation.");
        description.setAlignment(Pos.CENTER);
        homepage.getChildren().addAll(home, description);
        VBox listOfComponents = new VBox(5);
        listOfComponents.setStyle("-fx-background-color: #EAEAEA;");
        examples.forEach(example -> {
            HBox hbox = new HBox(15);
            hbox.setAlignment(Pos.CENTER_LEFT);
            Image image = new Image("/images/" + example.getExampleName() + ".png");
            ImageView imageView = new ImageView();
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            imageView.setImage(image);
            Label component = new Label(example.getExampleName() + ": " + example.getDescription());
            imageView.setOnMouseClicked(e -> menuComboBox.getSelectionModel().select(example));
            hbox.getChildren().addAll(imageView, component);
            listOfComponents.getChildren().add(hbox);
        });
        homepage.getChildren().add(listOfComponents);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() {
        examples.forEach(example -> example.getGeoViews().forEach(GeoView::dispose));
    }
}