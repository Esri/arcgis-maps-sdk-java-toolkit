/*
 * Copyright 2019 Esri
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

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.toolkit.FeatureTemplateGroup;
import com.esri.arcgisruntime.toolkit.FeatureTemplateItem;
import com.esri.arcgisruntime.toolkit.FeatureTemplatePicker;
import javafx.collections.ListChangeListener;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public final class FeatureTemplatePickerFlowPaneSkin extends SkinBase<FeatureTemplatePicker> {

  private final ToggleGroup toggleGroup;

  public FeatureTemplatePickerFlowPaneSkin(FeatureTemplatePicker control) {
    super(control);

    FlowPane pane = new FlowPane();
    pane.setHgap(10);
    pane.setVgap(10);
    getChildren().add(pane);

    // toggle group so selection is shared between all feature template items
    toggleGroup = new ToggleGroup();

    // add a tile pane for each feature template group
    control.getFeatureTemplateGroups().stream()
        .map(this::createVBoxForFeatureTemplateGroup)
        .forEach(tilePane -> pane.getChildren().add(tilePane));

    // add or remove tile panes if the feature template group collection changes
    control.featureTemplateGroupsProperty().addListener((ListChangeListener<FeatureTemplateGroup>) c -> {
      while (c.next()) {
        if (c.wasAdded()) {
          List<VBox> added = c.getAddedSubList().stream()
              .map(this::createVBoxForFeatureTemplateGroup)
              .collect(Collectors.toList());
          pane.getChildren().addAll(c.getFrom(), added);
        } else if (c.wasRemoved()) {
          pane.getChildren().remove(c.getFrom(), c.getFrom() + c.getRemovedSize());
        }
      }
    });

    pane.orientationProperty().bind(control.orientationProperty());
  }

  private VBox createVBoxForFeatureTemplateGroup(FeatureTemplateGroup featureTemplateGroup) {
    VBox vBox = new VBox(5);

    FeatureLayer featureLayer = featureTemplateGroup.getFeatureLayer();
    Label label = new Label();

    featureLayer.addDoneLoadingListener(() -> {
      if (featureLayer.getLoadStatus() == LoadStatus.LOADED) {
        label.setText(featureLayer.getName());
      }
    });

    FlowPane tilePane = new FlowPane();
    vBox.getChildren().addAll(label, tilePane);

    // add toggle buttons for each feature template item in the group
    featureTemplateGroup.getFeatureTemplateItems().stream()
        .map(this::createVBoxForFeatureTemplateItem)
        .forEach(toggleButton -> tilePane.getChildren().add(toggleButton));

    // add or remove tiles if the feature template item collection changes
    featureTemplateGroup.featureTemplateItemsProperty().addListener((ListChangeListener<FeatureTemplateItem>) c -> {
      while (c.next()) {
        if (c.wasAdded()) {
          List<VBox> added = c.getAddedSubList().stream()
              .map(this::createVBoxForFeatureTemplateItem)
              .collect(Collectors.toList());
          tilePane.getChildren().addAll(c.getFrom(), added);
        } else if (c.wasRemoved()) {
          tilePane.getChildren().remove(c.getFrom(), c.getFrom() + c.getRemovedSize());
        }
      }
    });

    return vBox;
  }

  private VBox createVBoxForFeatureTemplateItem(FeatureTemplateItem featureTemplateItem) {
    VBox vBox = new VBox();

    ToggleButton toggleButton = new ToggleButton(featureTemplateItem.getFeatureTemplate().getName());
    toggleButton.prefWidthProperty().bind(vBox.widthProperty());
    toggleButton.prefHeightProperty().bind(vBox.heightProperty());
    toggleButton.setContentDisplay(ContentDisplay.TOP);
    toggleButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    toggleButton.setTooltip(new Tooltip(featureTemplateItem.getFeatureTemplate().getName()));
    // skip the group and make every item in the picker belong to the same toggle group
    toggleButton.setToggleGroup(toggleGroup);
    toggleButton.getStyleClass().add("feature-template-item");
    vBox.getChildren().add(toggleButton);

    ImageView imageView = new ImageView();
    toggleButton.setGraphic(imageView);

    updateSwatch(featureTemplateItem, imageView);

    getSkinnable().symbolWidthProperty().addListener((observable, oldValue, newValue) ->
        updateSwatch(featureTemplateItem, imageView)
    );
    getSkinnable().symbolHeightProperty().addListener((observable, oldValue, newValue) ->
        updateSwatch(featureTemplateItem, imageView)
    );

    return vBox;
  }

  private void updateSwatch(FeatureTemplateItem featureTemplateItem, ImageView imageView) {
    final Graphic graphic = new Graphic();
    graphic.getAttributes().putAll(featureTemplateItem.getFeatureTemplate().getPrototypeAttributes());

    // use the feature layer's currently set renderer to generate a swatch for the toggle button's graphic
    final FeatureLayer featureLayer = featureTemplateItem.getFeatureLayer();
    featureLayer.addDoneLoadingListener(() -> {
      if (featureLayer.getLoadStatus() == LoadStatus.LOADED) {
        var symbol = featureLayer.getRenderer().getSymbol(graphic);
        ListenableFuture<Image> swatch = symbol.createSwatchAsync(this.getSkinnable().getSymbolWidth(),
            this.getSkinnable().getSymbolHeight(), (float) Screen.getPrimary().getOutputScaleX(), 0x00);
        swatch.addDoneListener(() -> {
          try {
            imageView.setImage(swatch.get());
          } catch (InterruptedException | ExecutionException | IllegalArgumentException e) {
            // do nothing
          }
        });
      }
    });
  }
}
