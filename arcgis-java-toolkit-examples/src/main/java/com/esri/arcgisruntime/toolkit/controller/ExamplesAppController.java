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
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A Controller for the Examples App. Configures the layout for the main app via app.fxml. This includes a header
 * section with ComboBox menu to select an Example to view, and also includes an initial Landing Page that displays
 * when the app first loads giving an overview of the available Examples in a GridPane layout.
 *
 * @since 100.15.0
 */
public class ExamplesAppController {

    private final List<Example> examples = new ArrayList<>();
    @FXML private ComboBox<Example> menu;
    @FXML private ExampleView exampleView;
    @FXML private GridPane examplesGridPane;
    @FXML private VBox landingPage;

    public void initialize() {
        // authentication with an API key or named user is required to access basemaps and other location services
        ArcGISRuntimeEnvironment.setApiKey(System.getProperty("apiKey"));

        // adds all the examples to the UI
        examples.addAll(getExamples());
        examples.forEach(example -> menu.getItems().add(example));
        // displays the examples by name in the menu
        menu.setConverter(new StringConverter<>() {
            @Override
            public String toString(Example example) {
                if (example == null) {
                    return null;
                } else {
                    return example.getName();
                }
            }
            @Override
            public Example fromString(String string) {
                return null;
            }
        });
        // displays a checkmark in the menu on the selected item via CSS
        menu.setCellFactory(lv -> {
            final ListCell<Example> cell = new ListCell<>() {
                @Override
                public void updateItem(Example item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(item != null ? item.getName() : null);
                }
            };
            Region icon = new Region();
            icon.getStyleClass().add("icon");
            cell.setGraphic(icon);
            cell.setGraphicTextGap(20);
            return cell;
        });

        // sets the selected example to the view
        menu.getSelectionModel().selectedItemProperty().addListener((obvs, old, nv) -> {
            if (landingPage.isVisible()) {
                // hide the landing page once a selection is made
                landingPage.setVisible(false);
            }
            exampleView.setSelectedExample(nv);
        });
        // toggles the visibility of the landing page and example view
        exampleView.visibleProperty().bind(landingPage.visibleProperty().not());
        // configures the grid pane of examples on the landing page
        setupExampleGridPane();
    }

    /**
     * Instantiates Examples and adds them to the list of examples.
     *
     * @return the list of examples
     * @since 100.15.0
     */
    private List<Example> getExamples() {
        Example compassExample = new CompassExample();
        Example floorFilterExample = new FloorFilterExample();
        return Arrays.asList(compassExample, floorFilterExample);
    }

    /**
     * Setups up the GridPane of examples displayed on the landing page. This is a 2 column grid with as many rows
     * as is required for the number of examples. The GridPane is setup in app.fxml.
     *
     * @since 100.15.0
     */
    private void setupExampleGridPane() {
        var numberOfColumns = 2;
        var numberOfRows = examples.size() / 2;
        var indexOfExample = 0;
        // add each of the examples into the grid at the appropriate column and row
        while (indexOfExample < examples.size()) {
            for (int row = 0; row < numberOfRows; row++) {
                for(int col = 0; col < numberOfColumns; col++) {
                    var example = examples.get(indexOfExample);
                    // HBox container for each example
                    HBox hbox = new HBox(15);
                    hbox.getStyleClass().add("panel");
                    hbox.getStyleClass().add("panel-white");
                    hbox.setId("example-grid-pane-item");
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    // on clicking the HBox the example will be selected
                    hbox.setOnMouseClicked(e -> menu.getSelectionModel().select(example));
                    // ImageView displays thumbnail of the component
                    ImageView imageView = new ImageView();
                    imageView.setFitWidth(100);
                    imageView.setFitHeight(100);
                    // check if an image exists for the example and display the default if not
                    if (ExamplesAppController.class.getResource("/images/" + example.getName() + ".png") != null) {
                        imageView.setImage(new Image("/images/" + example.getName() + ".png"));
                    } else if (ExamplesAppController.class.getResource("/images/default.png") != null){
                        imageView.setImage(new Image("/images/default.png"));
                    }
                    // VBox containing the name and description for the example
                    var labelVBox = new VBox(8);
                    labelVBox.getStyleClass().add("panel-no-padding, panel-no-border, panel-white");
                    labelVBox.setAlignment(Pos.CENTER_LEFT);
                    var componentName = new Label(example.getName());
                    componentName.getStyleClass().add("h2");
                    componentName.getStyleClass().add("blue-text");
                    componentName.getStyleClass().add("label-wrap-text");
                    var componentDescription = new Label(example.getDescription());
                    componentDescription.getStyleClass().add("label-wrap-text");
                    labelVBox.getChildren().addAll(componentName, componentDescription);
                    // add child components to the HBox
                    hbox.getChildren().addAll(imageView, labelVBox);
                    // add the example to the GridPane
                    examplesGridPane.add(hbox, col, row);
                    // increment the index to loop through the next example
                    indexOfExample += 1;
                }
            }
        }
    }

    /**
     * Disposes the views for each example on termination.
     *
     * @since 100.15.0
     */
    public void terminate() {
        examples.forEach(example -> example.getGeoViews().forEach(GeoView::dispose));
    }
}
