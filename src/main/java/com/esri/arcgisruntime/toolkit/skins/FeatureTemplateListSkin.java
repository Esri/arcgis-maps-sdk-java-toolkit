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

import com.esri.arcgisruntime.data.ArcGISFeatureTable;
import com.esri.arcgisruntime.data.FeatureTemplate;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.toolkit.FeatureTemplatePickerItem;
import com.esri.arcgisruntime.toolkit.FeatureTemplateList;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

/**
 * Defines a skin for a {@link FeatureTemplateList}.
 *
 * @since 100.6.0
 */
public final class FeatureTemplateListSkin extends SkinBase<FeatureTemplateList>  {

  private final VBox vBox = new VBox();
  private boolean invalid = true;
  private boolean populated = false;
  private boolean loaded = false;

  private final TilePane tilePane = new TilePane();

  private final List<FeatureTemplatePickerItem> templatePickerItems = new ArrayList<>();

  private final SimpleObjectProperty<FeatureTemplate> selectedFeatureTemplateProperty = new SimpleObjectProperty<>();

  private final ToggleGroup toggleGroup;

  /**
   * Creates a new skin instance.
   *
   * @param control the control this skin is for
   * @since 100.6.0
   */
  public FeatureTemplateListSkin(FeatureTemplateList control, ToggleGroup toggleGroup) {
    super(control);

    this.toggleGroup = toggleGroup;

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

    var featureTable = control.featureLayerProperty().get().getFeatureTable();

    control.disableIfCannotAddFeaturesProperty().addListener((observableValue, oldValue, newValue) -> {
      tilePane.setDisable(newValue && !featureTable.canAdd());
    });

    control.selectedTemplateProperty().bindBidirectional(selectedFeatureTemplateProperty);

    selectedFeatureTemplateProperty.addListener(observable -> {
      if (selectedFeatureTemplateProperty.get() == null) {
        clearSelection();
      }
    });

    toggleGroup.selectedToggleProperty().addListener(o -> {
      var t = toggleGroup.getSelectedToggle();
      if (t != null) {
        FeatureTemplatePickerItem f = (FeatureTemplatePickerItem) t;
        control.selectedTemplateProperty().set(f.templateProperty().get().getFeatureTemplate());
      } else {
        control.selectedTemplateProperty().set(null);
      }
    });

    featureTable.addDoneLoadingListener(() -> {
      if (featureTable.getLoadStatus() == LoadStatus.LOADED) {
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

    getChildren().forEach(c ->
      layoutInArea(c, contentX, contentY, contentWidth, contentHeight, -1, HPos.CENTER, VPos.CENTER));
  }

  /**
   * Populates the list with {@link FeatureTemplatePickerItem}s.
   *
   * @since 100.6.0
   */
  private void populate() {
    var control = getSkinnable();
    var featureLayer = control.featureLayerProperty().get();
    var featureTable = (ArcGISFeatureTable) featureLayer.getFeatureTable();

    featureTable.getFeatureTemplates()
      .forEach(featureTemplate -> templatePickerItems.add(new FeatureTemplatePickerItem(featureLayer, featureTemplate)));
    featureTable.getFeatureTypes()
      .forEach(featureType -> featureType.getTemplates()
        .forEach(featureTemplate -> templatePickerItems.add(new FeatureTemplatePickerItem(featureLayer, featureTemplate))));

    templatePickerItems.forEach(t -> {
      t.symbolWidthProperty().bind(control.symbolWidthProperty());
      t.symbolHeightProperty().bind(control.symbolHeightProperty());
    });

    templatePickerItems.forEach(t -> t.setToggleGroup(toggleGroup));

    toggleGroup.selectedToggleProperty().addListener(o -> {
      var t = toggleGroup.getSelectedToggle();
      if (t != null) {
        FeatureTemplatePickerItem f = (FeatureTemplatePickerItem) t;
      }
    });
  }

  /**
   * Updates the skin when the content has become invalid, for example if the option to display the layer name is
   * changed value.
   *
   * @param contentWidth the content width
   * @param contentHeight the content height
   * @since 100.6.0
   */
  private void update(double contentWidth, double contentHeight) {
    var control = getSkinnable();

    vBox.getChildren().clear();
    tilePane.getChildren().clear();

    templatePickerItems.forEach(t -> {
      t.showNameProperty().set(control.showTemplateNameProperty().get());
    });

    if (control.showLayerNameProperty().get()) {
      ArcGISFeatureTable featureTable = (ArcGISFeatureTable) control.featureLayerProperty().get().getFeatureTable();
      vBox.getChildren().add(new Label(featureTable.getTableName()));
    }

    tilePane.getChildren().addAll(templatePickerItems);
    vBox.getChildren().addAll(tilePane);
  }

  /**
   * Sets any selected cell to not be selected.
   *
   * @since 100.6.0
   */
   private void clearSelection() {
     templatePickerItems.forEach(featureTemplateCell ->
       templatePickerItems.stream().filter(FeatureTemplatePickerItem::isSelected).findFirst().ifPresent(
         t -> t.setSelected(false)));
  }

  @Override
  public void dispose() {
    clearSelection();
    templatePickerItems.forEach(t -> t.setToggleGroup(null));
  }
}
