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

import java.util.concurrent.TimeUnit;

import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.symbology.ColorUtil;
import com.esri.arcgisruntime.toolkit.UtilityNetworkTraceStartingPoint;
import com.esri.arcgisruntime.utilitynetworks.UtilityTerminal;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

/**
 * A custom BorderPane for a starting point displayed in a {@link UtilityNetworkTraceSkin}.
 *
 * <p>
 * Has custom style class applied if required for customization:
 * utility-network-trace-starting-point-view
 *
 * @since 100.15.0
 */
public class UtilityNetworkTraceStartingPointView extends BorderPane {

  /**
   * Creates a UtilityNetworkTraceStartingPointView.
   *
   * @param skin the UtilityNetworkTraceSkin skin where the starting point is displayed
   * @param startingPoint the UtilityNetworkTraceStartingPoint model for the starting point
   * @since 100.15.0
   */
  protected UtilityNetworkTraceStartingPointView(
    UtilityNetworkTraceSkin skin, UtilityNetworkTraceStartingPoint startingPoint) {
    double height = skin.getStartingPointListCellHeight() - 10;
    setPrefHeight(height);
    setMinHeight(height);
    setMaxHeight(height + 15);
    setPadding(new Insets(5, 2, 5, 2));
    getStyleClass().add("utility-network-trace-starting-point-view");

    // Left of the borderpane is a thumbnail image of the feature symbol.
    // Setup and configure the feature symbol image
    var symbolVBox = new VBox();
    symbolVBox.setAlignment(Pos.CENTER);
    symbolVBox.setMinWidth(40);
    var featureSymbolImageView = new ImageView();
    symbolVBox.getChildren().add(featureSymbolImageView);
    try {
      featureSymbolImageView.setImage(
        startingPoint.getFeatureSymbol().createSwatchAsync(
          ColorUtil.colorToArgb(Color.TRANSPARENT), 1f).get(5, TimeUnit.SECONDS));
    } catch (Exception ex) {
      // if the async swatch method fails, set the image to null
      featureSymbolImageView.setImage(null);
    }
    setLeft(symbolVBox);

    // Center of the borderpane are labels denoting the network source and asset group.
    // Configure the labels
    var networkSourceLabel = new Label(startingPoint.getUtilityElement().getNetworkSource().getName());
    var assetGroupLabel = new Label(startingPoint.getUtilityElement().getAssetGroup().getName());
    var labelsVBox = new VBox(5);
    labelsVBox.setAlignment(Pos.CENTER_LEFT);
    labelsVBox.getChildren().addAll(networkSourceLabel, assetGroupLabel);
    setCenter(labelsVBox);

    // Right of the borderpane are buttons enabling zoom and deletion of the starting point.
    // Configure the buttons
    var buttonsHBox = new HBox(5);
    buttonsHBox.setAlignment(Pos.CENTER);
    var zoomButton = new Button();
    Region zoomIcon = new Region();
    zoomIcon.getStyleClass().add("arcgis-toolkit-java-zoom-icon");
    zoomButton.setGraphic(zoomIcon);
    if (startingPoint.getExtent() != null) {
      zoomButton.setOnAction(e -> skin.controlMapView.setViewpoint(new Viewpoint(startingPoint.getExtent())));
    } else {
      zoomButton.setVisible(false);
    }
    var deleteButton = new Button();
    Region trashIcon = new Region();
    trashIcon.getStyleClass().add("arcgis-toolkit-java-trash-icon");
    deleteButton.setGraphic(trashIcon);
    deleteButton.setAlignment(Pos.CENTER_RIGHT);
    deleteButton.setOnAction(e -> skin.startingPointsProperty.remove(startingPoint));
    buttonsHBox.getChildren().addAll(zoomButton, deleteButton);
    setRight(buttonsHBox);

    // Bottom of the borderpane is optional depending on whether the starting point has a fraction along edge value
    // and/or multiple terminals.
    // Display a fraction slider if there is a fraction along edge property on the starting point
    var fractionSliderVisible = startingPoint.getHasFractionAlongEdge();
    var terminalPickerVisible = startingPoint.getHasMultipleTerminals();

    if (fractionSliderVisible || terminalPickerVisible) {
      var fractionTerminalsVBox = new VBox(5);
      setBottom(fractionTerminalsVBox);

      if (fractionSliderVisible) {
        var fractionHBox = new HBox(5);
        fractionHBox.setMaxWidth(Double.MAX_VALUE);
        // configure a slider that adjusts the fraction along edge property
        var fractionSlider = new Slider();
        fractionSlider.setMax(1);
        fractionSlider.setShowTickMarks(true);
        fractionSlider.setMajorTickUnit(0.1);
        fractionSlider.setMinorTickCount(0);
        HBox.setHgrow(fractionSlider, Priority.ALWAYS);
        fractionSlider.setMaxWidth(Double.MAX_VALUE);
        fractionSlider.valueProperty().bindBidirectional(startingPoint.fractionAlongEdgeProperty());
        // configure a label that displays the fraction along edge value
        var fractionLabel = new Label();
        fractionLabel.textProperty().bind(Bindings.format("%.2f", fractionSlider.valueProperty()));
        // add to the UI
        fractionHBox.getChildren().addAll(fractionSlider, fractionLabel);
        fractionTerminalsVBox.getChildren().add(fractionHBox);
      }

      if (terminalPickerVisible) {
        // configure a combobox that displays the available UtilityTerminal options
        ComboBox<UtilityTerminal> terminalsComboBox = new ComboBox<>();
        terminalsComboBox.setMaxWidth(Double.MAX_VALUE);
        terminalsComboBox.setConverter(new StringConverter<>() {
          @Override
          public String toString(UtilityTerminal terminal) {
            return terminal != null ? terminal.getName() : "";
          }
          @Override
          public UtilityTerminal fromString(String fileName) {
            return null;
          }
        });
        terminalsComboBox.getItems().addAll(
          startingPoint.getUtilityElement().getAssetType().getTerminalConfiguration().getTerminals());
        terminalsComboBox.getSelectionModel().select(
          startingPoint.getUtilityElement().getAssetType().getTerminalConfiguration().getTerminals().get(0));
        terminalsComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
          if (newValue != null) {
            startingPoint.getUtilityElement().setTerminal(newValue);
          }
        });
        fractionTerminalsVBox.getChildren().add(terminalsComboBox);
      }
    }
  }
}

