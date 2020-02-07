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

import java.util.LinkedHashMap;

import com.esri.arcgisruntime.data.ArcGISFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.toolkit.FeatureTemplateCell;
import com.esri.arcgisruntime.toolkit.FeatureTemplateList;
import com.esri.arcgisruntime.toolkit.FeatureTemplatePicker;
import javafx.beans.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SkinBase;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Defines a skin for a {@link FeatureTemplatePicker}.
 *
 * @since 100.6.0
 */
public final class FeatureTemplatePickerSkin extends SkinBase<FeatureTemplatePicker> {

  private Pane pane = new VBox();
  private final ScrollPane scrollPane = new ScrollPane();
  private boolean invalid = true;

  private final LinkedHashMap<FeatureLayer, FeatureTemplateList> featureLayerMap = new LinkedHashMap<>();

  private final SimpleObjectProperty<FeatureTemplatePicker.Template> selectedTemplate = new SimpleObjectProperty<>();

  private Orientation orientation;

  private final ToggleGroup toggleGroup = new ToggleGroup();

  /**
   * Creates a new skin instance.
   *
   * @param control the control this skin is for
   * @since 100.6.0
   */
  public FeatureTemplatePickerSkin(FeatureTemplatePicker control) {
    super(control);

    control.featureLayersProperty().addListener((ListChangeListener<? super FeatureLayer>) change -> {
      while (change.next()) {
        for (FeatureLayer featureLayer : change.getRemoved()) {
          removeTemplateList(featureLayer);
        }
        for (FeatureLayer featureLayer : change.getAddedSubList()) {
          addTemplateList(featureLayer);
        }
      }
    });

    control.widthProperty().addListener(this::invalidated);
    control.heightProperty().addListener(this::invalidated);
    control.insetsProperty().addListener(this::invalidated);
    control.orientationProperty().addListener(this::invalidated);

    orientation = control.getOrientation();

    control.selectedTemplateProperty().bindBidirectional(selectedTemplate);

    selectedTemplate.addListener(observable -> {
      if (selectedTemplate.get() == null) {
        featureLayerMap.values().forEach(FeatureTemplateList::clearSelection);
      }
    });

    // build template list for any feature layers already in the feature layers property
    control.featureLayersProperty().stream().filter(entry -> entry.getFeatureTable() instanceof ArcGISFeatureTable)
      .forEach(this::addTemplateList);

    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

    pane.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

    getChildren().addAll(scrollPane);

    scrollPane.setContent(pane);
  }

  @Override
  protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
    if (invalid) {
      update(contentWidth, contentHeight);
      invalid = false;
    }

    getChildren().forEach(c ->
      layoutInArea(c, contentX, contentY, contentWidth, contentHeight, -1, HPos.CENTER, VPos.CENTER));
  }

  /**
   * Updates the skin when the content has become invalid, for example if the value of the
   * {@link FeatureTemplatePicker#orientationProperty()} is changed.
   *
   * @param contentWidth the content width
   * @param contentHeight the content height
   * @since 100.6.0
   */
  private void update(double contentWidth, double contentHeight) {
    Orientation controlOrientation = getSkinnable().getOrientation();
    if (orientation != controlOrientation) {
      switch (controlOrientation) {
        case HORIZONTAL:
          pane = new HBox();
          break;
        case VERTICAL:
          pane = new VBox();
          break;
      }
      pane.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
      pane.getChildren().setAll(featureLayerMap.values());
      scrollPane.setContent(pane);

      orientation = controlOrientation;
    }
  }

  /**
   * Adds a {@link FeatureTemplateList} for a {@link FeatureLayer} to the skin.
   *
   * @param featureLayer the feature layer
   * @since 100.6.0
   */
  private void addTemplateList(FeatureLayer featureLayer) {
    var table = featureLayer.getFeatureTable();

    // only ArcGISFeatureTable is supported so ignore this feature layer if it has a different type of table
    if (!(table instanceof ArcGISFeatureTable)) {
      return;
    }

    var control = getSkinnable();

    FeatureTemplateList featureTemplateList = new FeatureTemplateList(featureLayer, toggleGroup);
    featureTemplateList.symbolWidthProperty().bind(control.symbolWidthProperty());
    featureTemplateList.symbolHeightProperty().bind(control.symbolHeightProperty());
    featureTemplateList.showTemplateNameProperty().bind(control.showTemplateNamesProperty());
    featureTemplateList.showLayerNameProperty().bind(control.showFeatureLayerNamesProperty());
    featureTemplateList.disableIfCannotAddFeaturesProperty().bind(control.disableIfCannotAddFeatureLayersProperty());

    toggleGroup.selectedToggleProperty().addListener(o -> {
      var t = toggleGroup.getSelectedToggle();
      if (t != null) {
        FeatureTemplateCell f = (FeatureTemplateCell) t;
        control.selectedTemplateProperty().set(f.templateProperty().get());
        //control.selectedTemplateProperty.set((FeatureTemplateCell) t).templateProperty());
      } else {
        control.selectedTemplateProperty().set(null);
      }
    });

//    featureTemplateList.selectedTemplateProperty().addListener(observable -> {
//      if (featureTemplateList.selectedTemplateProperty().get() != null) {
//        FeatureTemplatePicker.Template template =
//          new FeatureTemplatePicker.Template(featureTemplateList.featureLayerProperty().get(),
//            featureTemplateList.selectedTemplateProperty().get());
//        selectedTemplate.set(template);
//
//        featureLayerMap.values().stream().filter(
//          t -> t != featureTemplateList).forEach(FeatureTemplateList::clearSelection);
//      }
//    });

    featureLayerMap.put(featureLayer, featureTemplateList);

    pane.getChildren().setAll(featureLayerMap.values());
  }

  /**
   * Removes a {@link FeatureLayer} from the skin.
   *
   * @param featureLayer the feature layer to remove
   */
  private void removeTemplateList(FeatureLayer featureLayer) {
    var featureTemplateList = featureLayerMap.remove(featureLayer);
    if (featureTemplateList != null) {
      if (featureTemplateList.selectedTemplateProperty().get() != null) {
        selectedTemplate.set(null);
      }
      pane.getChildren().setAll(featureLayerMap.values());
    }
  }

  /**
   * Sets invalid to true whenever the skin becomes invalidated, for example if the control's size changes.
   *
   * @param observable the observable
   * @since 100.6.0
   */
  private void invalidated(Observable observable) {
    invalid = true;
  }
}
