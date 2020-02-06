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
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Screen;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public final class FeatureTemplatePickerFlowPaneSkin extends SkinBase<FeatureTemplatePicker> {

  private final ScrollPane scrollPane;
  private final FlowPane rootFlowPane;
  private final ToggleGroup toggleGroup;

  public FeatureTemplatePickerFlowPaneSkin(FeatureTemplatePicker control) {
    super(control);

    scrollPane = new ScrollPane();
    getChildren().add(scrollPane);

    rootFlowPane = new FlowPane();
    rootFlowPane.setHgap(10);
    rootFlowPane.setVgap(10);
    scrollPane.setContent(rootFlowPane);
    updateOrientation(control.getOrientation());

    // toggle group so selection is shared between all feature template items
    toggleGroup = new ToggleGroup();
    toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
      control.setSelectedFeatureTemplateItem(newValue == null ? null : (FeatureTemplateItem) newValue.getUserData());
    });
    control.selectedFeatureTemplateItemProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue == null) {
        toggleGroup.selectToggle(null);
      } else {
        Toggle matchingToggle = toggleGroup.getToggles().stream()
            .filter(t -> (t.getUserData()).equals(newValue))
            .findFirst()
            .orElse(null);
        toggleGroup.selectToggle(matchingToggle);
      }
    });

    // add a tile pane for each feature template group
    control.getFeatureTemplateGroups().stream()
        .map(this::createVBoxForFeatureTemplateGroup)
        .forEach(tilePane -> rootFlowPane.getChildren().add(tilePane));

    // add or remove tile panes if the feature template group collection changes
    control.featureTemplateGroupsProperty().addListener((ListChangeListener<FeatureTemplateGroup>) c -> {
      while (c.next()) {
        if (c.wasAdded()) {
          List<VBox> added = c.getAddedSubList().stream()
              .map(this::createVBoxForFeatureTemplateGroup)
              .collect(Collectors.toList());
          rootFlowPane.getChildren().addAll(c.getFrom(), added);
        } else if (c.wasRemoved()) {
          rootFlowPane.getChildren().remove(c.getFrom(), c.getFrom() + c.getRemovedSize());
        }
      }
    });

    control.orientationProperty().addListener((observable, oldValue, newValue) -> updateOrientation(newValue));
  }

  private void updateOrientation(Orientation orientation) {
    if (orientation == Orientation.HORIZONTAL) {
      rootFlowPane.setOrientation(Orientation.VERTICAL);

      scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
      scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
      scrollPane.setFitToWidth(false);
      scrollPane.setFitToHeight(true);
    } else {
      rootFlowPane.setOrientation(Orientation.HORIZONTAL);

      scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
      scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
      scrollPane.setFitToWidth(true);
      scrollPane.setFitToHeight(false);
    }
  }

  private VBox createVBoxForFeatureTemplateGroup(FeatureTemplateGroup featureTemplateGroup) {
    VBox vBox = new VBox(5);
    vBox.setAlignment(Pos.CENTER);

    vBox.prefHeightProperty().bind(Bindings.createDoubleBinding(() -> getSkinnable().getOrientation() == Orientation.HORIZONTAL ?
        getSkinnable().getHeight() : Region.USE_COMPUTED_SIZE, getSkinnable().heightProperty(),
        getSkinnable().orientationProperty()));
    vBox.prefWidthProperty().bind(Bindings.createDoubleBinding(() -> getSkinnable().getOrientation() == Orientation.HORIZONTAL ?
        Region.USE_COMPUTED_SIZE : getSkinnable().getWidth(), getSkinnable().widthProperty(), getSkinnable().orientationProperty()));

    FeatureLayer featureLayer = featureTemplateGroup.getFeatureLayer();
    Label label = new Label();

    featureLayer.addDoneLoadingListener(() -> {
      if (featureLayer.getLoadStatus() == LoadStatus.LOADED) {
        label.setText(featureLayer.getName());
      }
    });

    TilePane tilePane = new TilePane();
    vBox.getChildren().addAll(label, tilePane);

    // add toggle buttons for each feature template item in the group
    featureTemplateGroup.getFeatureTemplateItems().stream()
        .map(this::createVBoxForFeatureTemplateItem)
        .forEach(toggleButton -> tilePane.getChildren().add(toggleButton));

    // add or remove tiles if the feature template item collection changes
    featureTemplateGroup.featureTemplateItemsProperty().addListener((ListChangeListener<FeatureTemplateItem>) c -> {
      while (c.next()) {
        if (c.wasAdded()) {
          List<ToggleButton> added = c.getAddedSubList().stream()
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

  private ToggleButton createVBoxForFeatureTemplateItem(FeatureTemplateItem featureTemplateItem) {
    ToggleButton toggleButton = new ToggleButton(featureTemplateItem.getFeatureTemplate().getName());
    toggleButton.setContentDisplay(ContentDisplay.TOP);
    toggleButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    toggleButton.setTooltip(new Tooltip(featureTemplateItem.getFeatureTemplate().getName()));
    toggleButton.setToggleGroup(toggleGroup);
    toggleButton.setUserData(featureTemplateItem);
    toggleButton.getStyleClass().add("feature-template-item");

    ImageView imageView = new ImageView();
    toggleButton.setGraphic(imageView);

    updateSwatch(featureTemplateItem, imageView);

    getSkinnable().symbolSizeProperty().addListener((observable, oldValue, newValue) ->
        updateSwatch(featureTemplateItem, imageView)
    );

    return toggleButton;
  }

  private void updateSwatch(FeatureTemplateItem featureTemplateItem, ImageView imageView) {
    final Graphic graphic = new Graphic();
    graphic.getAttributes().putAll(featureTemplateItem.getFeatureTemplate().getPrototypeAttributes());

    // use the feature layer's currently set renderer to generate a swatch for the toggle button's graphic
    final FeatureLayer featureLayer = featureTemplateItem.getFeatureLayer();
    featureLayer.addDoneLoadingListener(() -> {
      if (featureLayer.getLoadStatus() == LoadStatus.LOADED) {
        var symbol = featureLayer.getRenderer().getSymbol(graphic);
        ListenableFuture<Image> swatch = symbol.createSwatchAsync(this.getSkinnable().getSymbolSize(),
            this.getSkinnable().getSymbolSize(), (float) Screen.getPrimary().getOutputScaleX(), 0x00);
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
