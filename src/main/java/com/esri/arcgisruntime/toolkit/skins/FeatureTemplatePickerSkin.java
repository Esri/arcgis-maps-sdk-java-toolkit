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
import com.esri.arcgisruntime.toolkit.FeatureTemplateList;
import com.esri.arcgisruntime.toolkit.FeatureTemplatePicker;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class FeatureTemplatePickerSkin extends SkinBase<FeatureTemplatePicker> {

  private Pane pane = new VBox();
  private ScrollPane scrollPane = new ScrollPane();
  private boolean invalid = true;

  private final LinkedHashMap<FeatureLayer, FeatureTemplateList> featureLayerMap = new LinkedHashMap<>();

  private final SimpleObjectProperty<FeatureTemplatePicker.Template> selectedTemplate = new SimpleObjectProperty<>();

  public FeatureTemplatePickerSkin(FeatureTemplatePicker control) {
    super(control);

    control.featureLayerListProperty().addListener((ListChangeListener<? super FeatureLayer>) change -> {
      while (change.next()) {
        for (FeatureLayer featureLayer : change.getRemoved()) {
          System.out.println("Removed " + featureLayer.getFeatureTable().getTableName() + " from  " + change.getFrom());
          removeTemplateList(featureLayer);
        }
        for (FeatureLayer featureLayer : change.getAddedSubList()) {
          System.out.println("Added " + featureLayer.getFeatureTable().getTableName() + " at " + change.getFrom());
          addTemplateList(featureLayer);
        }
      }
    });

    control.widthProperty().addListener(observable -> invalid = true);
    control.heightProperty().addListener(observable -> invalid = true);
    control.insetsProperty().addListener(observable -> invalid = true);

    control.selectedTemplateProperty().bindBidirectional(selectedTemplate);

    control.orientationProperty().addListener((observableValue, oldValue, newValue) -> {
      switch (newValue) {
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
      invalid = true;
    });

    selectedTemplate.addListener(observable -> {
      if (selectedTemplate.get() == null) {
        featureLayerMap.values().forEach(FeatureTemplateList::clearSelection);
      }
    });

    // build template list for any feature layers already in the feature layers property
    getSkinnable().featureLayerListProperty().stream().filter(entry -> entry.getFeatureTable() instanceof ArcGISFeatureTable)
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

    getChildren().forEach(c -> layoutInArea(c, contentX, contentY, contentWidth, contentHeight, -1, HPos.CENTER, VPos.CENTER));
  }

  private void update(double contentWidth, double contentHeight) {
  }

  private void addTemplateList(FeatureLayer featureLayer) {
    var table = featureLayer.getFeatureTable();

    // only ArcGISFeatureTable is supported so ignore this feature layer if it has a different type of table
    if (!(table instanceof ArcGISFeatureTable)) {
      return;
    }

    ArcGISFeatureTable featureTable = (ArcGISFeatureTable) table;
    FeatureTemplateList featureTemplateList = new FeatureTemplateList(featureLayer);
    featureTemplateList.symbolWidthProperty().bind(getSkinnable().symbolWidthProperty());
    featureTemplateList.symbolHeightProperty().bind(getSkinnable().symbolHeightProperty());
    featureTemplateList.showTemplateNameProperty().bind(getSkinnable().showTemplateNamesProperty());
    featureTemplateList.showLayerNameProperty().bind(getSkinnable().showFeatureLayerNamesProperty());
    featureTemplateList.disableCannotAddFeatureLayersProperty().bind(getSkinnable().disableCannotAddFeatureLayersProperty());

    featureTemplateList.selectedTemplateProperty().addListener(observable -> {
      if (featureTemplateList.selectedTemplateProperty().get() != null) {
        FeatureTemplatePicker.Template template = new FeatureTemplatePicker.Template(featureTemplateList.featureLayerProperty().get(), featureTemplateList.selectedTemplateProperty().get());
        selectedTemplate.set(template);

        featureLayerMap.values().stream().filter(t -> t != featureTemplateList).forEach(FeatureTemplateList::clearSelection);
      }
    });

    featureLayerMap.put(featureLayer, featureTemplateList);

    pane.getChildren().setAll(featureLayerMap.values());
  }

  private void removeTemplateList(FeatureLayer featureLayer) {
    var featureTemplateList = featureLayerMap.remove(featureLayer);
    if (featureTemplateList != null) {
      if (featureTemplateList.selectedTemplateProperty().get() != null) {
        selectedTemplate.set(null);
      }
      pane.getChildren().setAll(featureLayerMap.values());
    }
  }
}
