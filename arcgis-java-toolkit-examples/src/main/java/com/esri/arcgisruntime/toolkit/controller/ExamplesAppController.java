package com.esri.arcgisruntime.toolkit.controller;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.toolkit.examples.CompassExample;
import com.esri.arcgisruntime.toolkit.examples.FloorFilterExample;
import com.esri.arcgisruntime.toolkit.model.Example;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExamplesAppController {

    private final List<Example> examples = new ArrayList<>();

    private SimpleBooleanProperty showLandingPageProperty = new SimpleBooleanProperty();

    @FXML
    private BorderPane borderPane;

    @FXML private ComboBox<Example> menu;

    @FXML private ExampleView exampleView;

    @FXML private VBox indexOfExamples;

    @FXML private VBox landingPage;

    public void initialize() {

        ArcGISRuntimeEnvironment.setApiKey(System.getProperty("apiKey"));

        showLandingPageProperty.set(true);

        examples.addAll(getExamples());
        examples.forEach(example -> menu.getItems().add(example));

        menu.setConverter(new StringConverter<>() {
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

        menu.setCellFactory(lv -> {
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

        menu.getSelectionModel().selectedItemProperty().addListener((obvs, old, nv) -> {
            if (showLandingPageProperty.get()) {
                showLandingPageProperty.set(false);
            }
            exampleView.setSelectedExample(nv);
        });

        examples.forEach(example -> {
            HBox hbox = new HBox(15);
            hbox.setAlignment(Pos.CENTER_LEFT);
            ImageView imageView = new ImageView();
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            if (ExamplesAppController.class.getResource("/images/" + example.getExampleName() + ".png") != null) {
                imageView.setImage(new Image("/images/" + example.getExampleName() + ".png"));
            } else if (ExamplesAppController.class.getResource("/images/default.png") != null){
                imageView.setImage(new Image("/images/default.png"));
            }
            Label component = new Label(example.getExampleName() + ": " + example.getDescription());
            imageView.setOnMouseClicked(e -> menu.getSelectionModel().select(example));
            hbox.getChildren().addAll(imageView, component);
            indexOfExamples.getChildren().add(hbox);
        });

        exampleView.visibleProperty().bind(showLandingPageProperty.not());
        landingPage.visibleProperty().bind(showLandingPageProperty);

//        menu.getSelectionModel().select(0);
    }

    private List<Example> getExamples() {
        Example compassExample = new CompassExample();
        Example floorFilterExample = new FloorFilterExample();
        return Arrays.asList(compassExample, floorFilterExample);
    }

    public void terminate() {
        examples.forEach(example -> example.getGeoViews().forEach(GeoView::dispose));
    }
}
