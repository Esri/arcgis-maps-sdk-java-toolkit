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
import java.util.Objects;

import com.esri.arcgisruntime.data.FeatureTemplate;
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

public class FeatureTemplateListSkin extends SkinBase<FeatureTemplateList>  {

  private VBox vBox = new VBox();
  private boolean invalid = true;
  private boolean populated = false;

  private TilePane tilePane = new TilePane();

  private List<FeatureTemplateCell> templateCells = new ArrayList<>();

  private SimpleObjectProperty<FeatureTemplate> selectedFeatureTemplateProperty = new SimpleObjectProperty<>();

  public FeatureTemplateListSkin(FeatureTemplateList control) {
    super(control);

    control.widthProperty().addListener(observable -> invalid = true);
    control.heightProperty().addListener(observable -> invalid = true);
    control.insetsProperty().addListener(observable -> invalid = true);

    control.showLayerNameProperty().addListener(observable -> invalid = true);
    control.showTemplateNameProperty().addListener(observable -> invalid = true);
    control.symbolWidthProperty().addListener(observable -> invalid = true);
    control.symbolHeightProperty().addListener(observable -> invalid = true);

    control.selectedTemplateProperty().bindBidirectional(selectedFeatureTemplateProperty);

    getChildren().addAll(vBox);
  }

  public void clearSelection() {
    templateCells.forEach(featureTemplateCell ->
      templateCells.stream().filter(FeatureTemplateCell::isSelected).findFirst().ifPresent(t -> t.setSelected(false)));
  }

  @Override
  protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
    if (!populated) {
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
      t.imageWidthProperty().set(control.symbolWidthProperty().get());
      t.imageHeightProperty().set(control.symbolHeightProperty().get());

      t.showNameProperty().set(control.showTemplateNameProperty().get());
    });

    if (control.showLayerNameProperty().get()) {
      vBox.getChildren().add(new Label(control.featureTableProperty().get().getTableName()));
    }
    //vBox.getChildren().addAll(templateCells);
    tilePane.getChildren().addAll(templateCells);
    vBox.getChildren().add(tilePane);
  }
}
