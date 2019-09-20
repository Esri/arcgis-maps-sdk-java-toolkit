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
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.toolkit.FeatureTemplateList;
import com.esri.arcgisruntime.toolkit.FeatureTemplatePicker;
import com.esri.arcgisruntime.toolkit.TemplatePicker;
import javafx.beans.InvalidationListener;
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
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class FeatureTemplatePickerSkin extends SkinBase<FeatureTemplatePicker> {

  private Pane pane = new VBox();
  //private VBox vBox = new VBox();
  private ScrollPane scrollPane = new ScrollPane();
  private boolean invalid = true;

  private final List<FeatureTemplateList> templateLists = new ArrayList<>();

  private final SimpleObjectProperty<TemplatePicker.Template> selectedTemplate = new SimpleObjectProperty<>();

  public FeatureTemplatePickerSkin(FeatureTemplatePicker control) {
    super(control);

    control.featureLayerListProperty().addListener((InvalidationListener) observable -> {
      populate();
      invalid = true;
    });

    control.featureLayerListProperty().addListener((ListChangeListener<? super FeatureLayer>) change -> {
      while (change.next()) {
        if (change.wasPermutated()) {
          for (int i = change.getFrom(); i < change.getTo(); ++i) {
            //permutate
          }
        } else if (change.wasUpdated()) {
          //update item
        } else {
          for (FeatureLayer featureLayer : change.getRemoved()) {
            System.out.println("Removed " + featureLayer.getFeatureTable().getTableName() + " from  " + change.getFrom());
          }
          for (FeatureLayer featureLayer : change.getAddedSubList()) {
            System.out.println("Added " + featureLayer.getFeatureTable().getTableName() + " at " + change.getFrom());
          }
        }
      }
    });

    control.widthProperty().addListener(observable -> invalid = true);
    control.heightProperty().addListener(observable -> invalid = true);
    control.insetsProperty().addListener(observable -> invalid = true);

    control.selectedTemplateProperty().bindBidirectional(selectedTemplate);

    control.orientationProperty().addListener((observableValue, oldValue, newValue) -> {
      System.out.println(oldValue + " " + newValue);
      switch (newValue) {
        case HORIZONTAL:
          pane = new HBox();
          break;
        case VERTICAL:
          pane = new VBox();
          break;
      }
      pane.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
      pane.getChildren().addAll(templateLists);
      scrollPane.setContent(pane);
      invalid = true;
    });

    selectedTemplate.addListener(observable -> {
      if (selectedTemplate.get() == null) {
        templateLists.forEach(t -> t.clearSelection());
      }
    });

    populate();

    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

//    vBox.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    pane.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

    getChildren().addAll(scrollPane);

//    Rectangle rectangle = new Rectangle();
//    rectangle.setFill(Color.rgb(0xFF, 0x00, 0x00, 0.5));
//    rectangle.widthProperty().bind(vBox.widthProperty());
//    rectangle.heightProperty().bind(vBox.heightProperty());

    //getChildren().add(rectangle);
//    scrollPane.setContent(vBox);
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

  private void populate() {
    pane.getChildren().clear();
//    vBox.getChildren().clear();
    templateLists.clear();

    getSkinnable().featureLayerListProperty().stream().filter(entry -> entry.getFeatureTable() instanceof ArcGISFeatureTable)
      .forEach(featureLayer -> {
        ArcGISFeatureTable featureTable = (ArcGISFeatureTable) featureLayer.getFeatureTable();
        FeatureTemplateList featureTemplateList = new FeatureTemplateList(featureTable);
        featureTemplateList.symbolWidthProperty().bind(getSkinnable().symbolWidthProperty());
        featureTemplateList.symbolHeightProperty().bind(getSkinnable().symbolHeightProperty());
        featureTemplateList.showTemplateNameProperty().bind(getSkinnable().showTemplateNamesProperty());
        featureTemplateList.showLayerNameProperty().bind(getSkinnable().showFeatureLayerNamesProperty());
        featureTemplateList.disableCannotAddFeatureLayersProperty().bind(getSkinnable().disableCannotAddFeatureLayersProperty());

        featureTemplateList.selectedTemplateProperty().addListener(observable -> {
          if (featureTemplateList.selectedTemplateProperty().get() != null) {
            TemplatePicker.Template template = new TemplatePicker.Template(featureTemplateList.featureTableProperty().get().getFeatureLayer(), featureTemplateList.selectedTemplateProperty().get());
            selectedTemplate.set(template);

            templateLists.stream().filter(t -> t != featureTemplateList).forEach(FeatureTemplateList::clearSelection);
          }
        });

        templateLists.add(featureTemplateList);
      });

    pane.getChildren().addAll(templateLists);
//    vBox.getChildren().addAll(templateLists);
    getSkinnable().requestLayout();
  }

  private void update(double contentWidth, double contentHeight) {
  }
}
