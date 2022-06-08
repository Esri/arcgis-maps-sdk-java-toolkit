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
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExamplesAppController {

    private final List<Example> examples = new ArrayList<>();

    private final SimpleBooleanProperty showLandingPageProperty = new SimpleBooleanProperty();

    @FXML private ComboBox<Example> menu;

    @FXML private ExampleView exampleView;

    @FXML private GridPane examplesGridPane;

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

        var numberOfColumns = 2;
        var numberOfRows = examples.size() / 2;
        var indexOfExample = 0;
        while (indexOfExample < examples.size()) {
            for (int row = 0; row < numberOfRows; row++) {
                for(int col = 0; col < numberOfColumns; col++) {
                    var example = examples.get(indexOfExample);
                    HBox hbox = new HBox(15);
                    hbox.getStyleClass().add("panel");
                    hbox.getStyleClass().add("panel-white");
                    hbox.setId("example-grid-pane-item");
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    ImageView imageView = new ImageView();
                    imageView.setFitWidth(100);
                    imageView.setFitHeight(100);
                    if (ExamplesAppController.class.getResource("/images/" + example.getExampleName() + ".png") != null) {
                        imageView.setImage(new Image("/images/" + example.getExampleName() + ".png"));
                    } else if (ExamplesAppController.class.getResource("/images/default.png") != null){
                        imageView.setImage(new Image("/images/default.png"));
                    }
                    var labelVBox = new VBox(8);
                    labelVBox.getStyleClass().add("panel-no-padding, panel-no-border, panel-white");
                    labelVBox.setAlignment(Pos.CENTER_LEFT);
                    var componentName = new Label(example.getName());
                    componentName.getStyleClass().add("h2");
                    componentName.getStyleClass().add("blue-text");
                    componentName.getStyleClass().add("label-wrap-text");
                    var componentDescription = new Label(example.getDescription());
                    componentDescription.getStyleClass().add("label-wrap-text");
                    hbox.setOnMouseClicked(e -> menu.getSelectionModel().select(example));
                    labelVBox.getChildren().addAll(componentName, componentDescription);
                    hbox.getChildren().addAll(imageView, labelVBox);
                    examplesGridPane.add(hbox, col, row);
                    indexOfExample += 1;
                }
            }
        }

        exampleView.visibleProperty().bind(showLandingPageProperty.not());
        landingPage.visibleProperty().bind(showLandingPageProperty);
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
