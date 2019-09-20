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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.esri.arcgisruntime.data.FeatureTemplate;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.toolkit.FeatureTemplateCell;
import com.esri.arcgisruntime.toolkit.FeatureTemplateList;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

public final class FeatureTemplateListSkin extends SkinBase<FeatureTemplateList>  {

  private final VBox vBox = new VBox();
  private boolean invalid = true;
  private boolean populated = false;
  private boolean loaded = false;

  private final TilePane tilePane = new TilePane();

  private final List<FeatureTemplateCell> templateCells = new ArrayList<>();

  private final SimpleObjectProperty<FeatureTemplate> selectedFeatureTemplateProperty = new SimpleObjectProperty<>();

  public FeatureTemplateListSkin(FeatureTemplateList control) {
    super(control);

    control.widthProperty().addListener(observable -> invalid = true);
    control.heightProperty().addListener(observable -> invalid = true);
    control.insetsProperty().addListener(observable -> invalid = true);

    control.showLayerNameProperty().addListener(observable -> {
      invalid = true;
      control.requestLayout();
    });
    control.showTemplateNameProperty().addListener(observable -> {
      invalid = true;
      control.requestLayout();
    });
    control.symbolWidthProperty().addListener(observable -> invalid = true);
    control.symbolHeightProperty().addListener(observable -> invalid = true);

    var featureTable = control.featureTableProperty().get();

    control.disableCannotAddFeatureLayersProperty().addListener((observableValue, oldValue, newValue) -> {
      tilePane.setDisable(newValue && !featureTable.canAdd());
    });

    control.selectedTemplateProperty().bindBidirectional(selectedFeatureTemplateProperty);

    selectedFeatureTemplateProperty.addListener(observable -> {
      if (selectedFeatureTemplateProperty.get() == null) {
        clearSelection();
      }
    });

    featureTable.addDoneLoadingListener(() -> {
      if (featureTable.getLoadStatus() == LoadStatus.LOADED) {
        System.out.println(featureTable.getLoadStatus() + " " + featureTable.getTableName());
        loaded = true;
        invalid = true;
        control.requestLayout();
      }
    });

    getChildren().addAll(vBox);
  }

  @Override
  protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
    if (loaded && !populated) {
      populate();
      populated = true;
    }
    if (invalid) {
      update(contentWidth, contentHeight);
      invalid = false;
    }

    getChildren().forEach(c -> layoutInArea(c, contentX, contentY, contentWidth, contentHeight, -1, HPos.CENTER, VPos.CENTER));
  }

  private void populate() {
    var featureTable = Objects.requireNonNull(getSkinnable().featureTableProperty().get());
    var featureLayer = featureTable.getFeatureLayer();

    featureTable.getFeatureTemplates()
      .forEach(featureTemplate -> templateCells.add(new FeatureTemplateCell(featureLayer, featureTemplate)));
    featureTable.getFeatureTypes()
      .forEach(featureType -> featureType.getTemplates()
        .forEach(featureTemplate -> templateCells.add(new FeatureTemplateCell(featureLayer, featureTemplate))));

    templateCells.forEach(t -> {
      t.imageWidthProperty().bind(getSkinnable().symbolWidthProperty());
      t.imageHeightProperty().bind(getSkinnable().symbolHeightProperty());
    });

    templateCells.forEach(t -> t.setOnMouseClicked(a -> {
      if (a.getButton() != MouseButton.PRIMARY) {
        return;
      }
      boolean selected = t.isSelected();
      templateCells.forEach(cell -> cell.setSelected(false));
      t.setSelected(!selected);

      selectedFeatureTemplateProperty.set(t.templateProperty().get().getFeatureTemplate());
    }));
  }

  private void update(double contentWidth, double contentHeight) {
    var control = getSkinnable();

    vBox.getChildren().clear();
    tilePane.getChildren().clear();

    templateCells.forEach(t -> {
      t.showNameProperty().set(control.showTemplateNameProperty().get());
    });

    if (control.showLayerNameProperty().get()) {
      vBox.getChildren().add(new Label(control.featureTableProperty().get().getTableName()));
    }

    tilePane.getChildren().addAll(templateCells);
    vBox.getChildren().addAll(tilePane);
  }

  private void clearSelection() {
    templateCells.forEach(featureTemplateCell ->
      templateCells.stream().filter(FeatureTemplateCell::isSelected).findFirst().ifPresent(t -> t.setSelected(false)));
  }
}
