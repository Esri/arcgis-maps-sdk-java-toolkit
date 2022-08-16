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

package com.esri.arcgisruntime.toolkit.skins;

import java.util.HashMap;

import com.esri.arcgisruntime.ArcGISRuntimeException;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.toolkit.UtilityNetworkTraceOperationResult;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * A custom Tab for a UtilityNetworkTraceOperationResult displayed in a {@link UtilityNetworkTraceSkin}.
 *
 * <p>
 * Has custom style class applied if required for customization:
 * utility-network-trace-operation-result-view
 *
 * @since 100.15.0
 */
public class UtilityNetworkTraceOperationResultView extends Tab {

  private final UtilityNetworkTraceOperationResult result;

  /**
   * Creates a UtilityNetworkTraceStartingPointView.
   *
   * @param skin the UtilityNetworkTraceSkin skin where the result is displayed
   * @param result the UtilityNetworkTraceOperationResult model for the result
   * @since 100.15.0
   */
  protected UtilityNetworkTraceOperationResultView(
    UtilityNetworkTraceSkin skin, UtilityNetworkTraceOperationResult result) {
    this.result = result;
    // configure the tab
    this.setText(result.getName());
    this.setClosable(false);
    this.getStyleClass().add("utility-network-trace-operation-result-view");

    var scrollPane = new ScrollPane();
    var vBox = new VBox(10);
    scrollPane.setContent(vBox);
    scrollPane.setFitToWidth(true);
    scrollPane.setFitToHeight(true);
    this.setContent(scrollPane);
    vBox.setPadding(new Insets(10, 10, 10, 10));

    // configure UI universal to all results, successful or unsuccessful
    var buttonsHBox = new HBox(10);
    buttonsHBox.setAlignment(Pos.CENTER);
    var discardButton = new Button("Discard");
    discardButton.getStyleClass().add("arcgis-toolkit-java-button-large");
    discardButton.setOnAction(e -> skin.traceResultsProperty.remove(result));
    Region trashIcon = new Region();
    trashIcon.getStyleClass().add("arcgis-toolkit-java-trash-icon");
    discardButton.setGraphic(trashIcon);
    buttonsHBox.getChildren().add(discardButton);
    vBox.getChildren().add(buttonsHBox);

    if (result.getException() != null) {
      // if the result has an exception, display the details
      var errorPanel = new HBox(10);
      errorPanel.getStyleClass().add("arcgis-toolkit-java-error-box");
      Region errorIcon = new Region();
      errorIcon.getStyleClass().add("arcgis-toolkit-java-error-icon");
      var error = new Label();
      if (result.getException().getCause() instanceof ArcGISRuntimeException) {
        var runtimeException = (ArcGISRuntimeException) result.getException().getCause();
        error.setText(runtimeException.getMessage() + " " + runtimeException.getAdditionalMessage());
      } else if (result.getException().getCause() != null) {
        error.setText(result.getException().getCause().getMessage());
      } else {
        error.setText(result.getException().getMessage());
      }
      errorPanel.getChildren().addAll(errorIcon, error);
      vBox.getChildren().add(errorPanel);
    }

    if (!result.hasAnyResults()) {
      // if there are no results, show limited UI and info message
      var noResultsFoundVBox = new VBox(5);
      noResultsFoundVBox.setAlignment(Pos.CENTER);
      Region infoIcon = new Region();
      infoIcon.getStyleClass().add("arcgis-toolkit-java-info-icon");
      var label = new Label("No results found.");
      noResultsFoundVBox.getChildren().addAll(infoIcon, label);
      vBox.getChildren().add(noResultsFoundVBox);
    } else {
      // if there are results, set up the rest of the UI
      if (result.getExtent() != null) {
        var zoomButton = new Button("Zoom to");
        zoomButton.getStyleClass().add("arcgis-toolkit-java-button-large");
        zoomButton.setOnAction(e -> skin.controlMapView.setViewpoint(new Viewpoint(result.getExtent())));
        Region zoomIcon = new Region();
        zoomIcon.getStyleClass().add("arcgis-toolkit-java-zoom-icon");
        zoomButton.setGraphic(zoomIcon);
        buttonsHBox.getChildren().add(0, zoomButton);
      }

      if (!result.getFunctionResults().isEmpty()) {
        // if there are function results, configure the UI
        var functionResultsTitledPane = new TitledPane();
        functionResultsTitledPane.setExpanded(false);
        functionResultsTitledPane.setText("Function results");

        var functionResultsVBox = new VBox(5);
        result.getFunctionResults().forEach(functionResult -> {
          var hBox = new HBox(20);
          var networkAttributeName = new Label();
          networkAttributeName.setText(functionResult.getFunction().getNetworkAttribute().getName());
          var functionType = new Label();
          functionType.setText(functionResult.getFunction().getFunctionType().toString());
          var functionResultValue = new Label();
          functionResultValue.setText(functionResult.getResult().toString());
          hBox.getChildren().addAll(networkAttributeName, functionType, functionResultValue);
          functionResultsVBox.getChildren().add(hBox);
        });

        functionResultsTitledPane.setContent(functionResultsVBox);
        vBox.getChildren().add(functionResultsTitledPane);
      }

      if (!result.getElementResults().isEmpty()) {
        // if there are element results, configure the feature results UI
        var featureResultsTitledPane = new TitledPane();
        var featureResultsVBox = new VBox(5);
        featureResultsTitledPane.setExpanded(false);
        featureResultsTitledPane.setText("Feature results");

        // checkbox enabling selection/deselection of features on the map
        var selectFeaturesCheckbox = new CheckBox();
        selectFeaturesCheckbox.setText("Select features on map");
        selectFeaturesCheckbox.selectedProperty().bindBidirectional(result.isSelectedProperty());

        // creates a list of the number of features per asset group
        var elementResultsVBox = new VBox(5);
        HashMap<String, Integer> numberOfEachGroup = new HashMap<>();
        result.getElementResultsByAssetGroup().keySet().forEach(key -> {
          numberOfEachGroup.put(key.getName(), result.getElementResultsByAssetGroup().get(key).size());
        });
        numberOfEachGroup.keySet().forEach(key -> {
          var hBox = new HBox(20);
          var assetGroup = new Label();
          assetGroup.setText(key);
          var count = new Label();
          count.setText(String.valueOf(numberOfEachGroup.get(key)));
          hBox.getChildren().addAll(assetGroup, count);
          elementResultsVBox.getChildren().add(hBox);
        });
        featureResultsVBox.getChildren().addAll(selectFeaturesCheckbox, elementResultsVBox);
        featureResultsTitledPane.setContent(featureResultsVBox);
        vBox.getChildren().addAll(featureResultsTitledPane);
      }

      if (!result.getGraphics().isEmpty()) {
        // if there are graphics, configure the visualization options pane
        var visualizationOptionsTitledPane = new TitledPane();
        visualizationOptionsTitledPane.setExpanded(false);
        visualizationOptionsTitledPane.setText("Visualization options");
        var visualizationOptionsVHBox = new HBox(15);
        visualizationOptionsTitledPane.setContent(visualizationOptionsVHBox);

        // create colored rectangles that set their respective color to the result when clicked
        // this helps differentiate multiple results from one another on the map
        var size = 20;
        var redSelector = createRectangle(Color.RED, size);
        var orangeSelector = createRectangle(Color.ORANGE, size);
        var yellowSelector = createRectangle(Color.YELLOW, size);
        var greenSelector = createRectangle(Color.DARKGREEN, size);
        var blueSelector = createRectangle(Color.BLUE, size);
        var purpleSelector = createRectangle(Color.PURPLE, size);
        var pinkSelector = createRectangle(Color.PINK, size);
        var blackSelector = createRectangle(Color.BLACK, size);
        visualizationOptionsVHBox.getChildren().addAll(
          redSelector, orangeSelector, yellowSelector, greenSelector, blueSelector, purpleSelector,
          pinkSelector, blackSelector);
        vBox.getChildren().add(visualizationOptionsTitledPane);
      }

      if (!result.getWarnings().isEmpty()) {
        // if there are warnings, configure the UI
        var warningsTitledPane = new TitledPane();
        warningsTitledPane.setExpanded(false);
        warningsTitledPane.setText("Warnings");
        var warningsVBox = new VBox(5);
        result.getWarnings().forEach(warning -> {
          var warningLabel = new Label();
          warningLabel.setText(warning);
          warningsVBox.getChildren().add(warningLabel);
        });
        warningsTitledPane.setContent(warningsVBox);
      }
    }
  }

  /**
   * Returns the UtilityNetworkTraceOperationResult this Tab represents.
   *
   * @return the UtilityNetworkTraceOperationResult this tab represents
   * @since 100.15.0
   */
  public UtilityNetworkTraceOperationResult getResult() {
    return this.result;
  }

  /**
   * Creates and returns a rectangle of the provided color and size. Adds an action that on click will update the
   * color of the result to the color of the rectangle with opacity reduced.
   *
   * @param color the color to set to the rectangle
   * @param size the size to set to the rectangle
   * @return the rectangle
   * @since 100.15.0
   */
  private Rectangle createRectangle(Color color, int size) {
    var rectangle = new Rectangle(size, size);
    rectangle.setFill(color);
    // add opacity for overlaying on the map
    var visualizationColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 0.5);
    rectangle.setOnMouseClicked(e -> {
      if (e.getButton() == MouseButton.PRIMARY && e.isStillSincePress()) {
        result.visualizationColorProperty().set(visualizationColor);
      }
    });
    return rectangle;
  }
}
