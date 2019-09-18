/*
 COPYRIGHT 1995-2019 ESRI

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

package com.esri.arcgisruntime.toolkit.skins;

import java.util.ArrayList;
import java.util.List;

import com.esri.arcgisruntime.data.ArcGISFeatureTable;
import com.esri.arcgisruntime.toolkit.FeatureTemplateList;
import com.esri.arcgisruntime.toolkit.FeatureTemplatePicker;
import com.esri.arcgisruntime.toolkit.TemplatePicker;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class FeatureTemplatePickerSkin extends SkinBase<FeatureTemplatePicker> {

  private VBox vBox = new VBox();
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

    control.widthProperty().addListener(observable -> invalid = true);
    control.heightProperty().addListener(observable -> invalid = true);
    control.insetsProperty().addListener(observable -> invalid = true);

    control.selectedTemplateProperty().bindBidirectional(selectedTemplate);

    selectedTemplate.addListener(observable -> {
      if (selectedTemplate.get() == null) {
        templateLists.forEach(t -> t.clearSelection());
      }
    });

    populate();

//    scrollPane.setFitToWidth(true);
//    scrollPane.setFitToHeight(true);

    //scrollPane.setFitToWidth(true);
    //scrollPane.setFitToHeight(true);
    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

    vBox.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

//    getChildren().addAll(/*new ScrollPane(vBox)*/vBox/*scrollPane*//*vBox*/);
    getChildren().addAll(scrollPane);

    Rectangle rectangle = new Rectangle();
    rectangle.setFill(Color.rgb(0xFF, 0x00, 0x00, 0.5));
    rectangle.widthProperty().bind(vBox.widthProperty());
    rectangle.heightProperty().bind(vBox.heightProperty());

    //getChildren().add(rectangle);
    scrollPane.setContent(vBox);
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
    vBox.getChildren().clear();
    templateLists.clear();

    getSkinnable().featureLayerListProperty().stream().filter(entry -> entry.getFeatureTable() instanceof ArcGISFeatureTable)
      .forEach(featureLayer -> {
        ArcGISFeatureTable featureTable = (ArcGISFeatureTable) featureLayer.getFeatureTable();
        FeatureTemplateList featureTemplateList = new FeatureTemplateList(featureTable);
        featureTemplateList.selectedTemplateProperty().addListener(observable -> {
          if (featureTemplateList.selectedTemplateProperty().get() != null) {
            TemplatePicker.Template template = new TemplatePicker.Template(featureTemplateList.featureTableProperty().get().getFeatureLayer(), featureTemplateList.selectedTemplateProperty().get());
            selectedTemplate.set(template);

            templateLists.stream().filter(t -> t != featureTemplateList).forEach(FeatureTemplateList::clearSelection);
          }
        });

        templateLists.add(featureTemplateList);
      });

    vBox.getChildren().addAll(templateLists);
    getSkinnable().requestLayout();
  }

  private void update(double contentWidth, double contentHeight) {
  }
}
