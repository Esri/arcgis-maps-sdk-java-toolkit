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
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.toolkit.FeatureTemplateCell;
import com.esri.arcgisruntime.toolkit.FeatureTemplateList;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

public class FeatureTemplateListSkin extends SkinBase<FeatureTemplateList>  {

  private StackPane stackPane = new StackPane();
  private VBox vBox = new VBox();
  private boolean invalid = true;
  private boolean populated = false;
  private boolean loaded = false;

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

    selectedFeatureTemplateProperty.addListener(observable -> {
      if (selectedFeatureTemplateProperty.get() == null) {
        clearSelection();
      }
    });

    var featureTable = control.featureTableProperty().get();
    featureTable.addDoneLoadingListener(() -> {
      System.out.println(featureTable.getLoadStatus());
      if (featureTable.getLoadStatus() == LoadStatus.LOADED) {
        loaded = true;
        invalid = true;
        control.requestLayout();
      }
    });

    tilePane.setAlignment(Pos.TOP_LEFT);
    //tilePane.setOrientation(Orientation.VERTICAL);

    getChildren().addAll(vBox/*stackPane*/);
  }

  private void clearSelection() {
    templateCells.forEach(featureTemplateCell ->
      templateCells.stream().filter(FeatureTemplateCell::isSelected).findFirst().ifPresent(t -> t.setSelected(false)));
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

    //stackPane.setMaxSize(contentWidth, contentHeight);

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

    //tilePane.setPrefColumns(3);

    tilePane.getChildren().addAll(templateCells);
    vBox.getChildren().addAll(tilePane/*, new Separator()*/);
  }
}
