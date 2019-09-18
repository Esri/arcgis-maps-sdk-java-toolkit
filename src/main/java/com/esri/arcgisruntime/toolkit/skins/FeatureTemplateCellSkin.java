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

import java.util.concurrent.ExecutionException;

import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.toolkit.FeatureTemplateCell;
import com.esri.arcgisruntime.toolkit.TemplatePicker;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;

public class FeatureTemplateCellSkin extends SkinBase<FeatureTemplateCell> {

  private boolean invalid = true;
  private Label label = new Label();

  public FeatureTemplateCellSkin(FeatureTemplateCell control) {
    super(control);

    control.widthProperty().addListener(observable -> invalid = true);
    control.heightProperty().addListener(observable -> invalid = true);
    control.insetsProperty().addListener(observable -> invalid = true);

    control.imageHeightProperty().addListener(observable -> {
      invalid = true;
      control.requestLayout();
    });
    control.imageWidthProperty().addListener(observable -> {
      invalid = true;
      control.requestLayout();
    });

    control.showNameProperty().addListener(observable -> {
      invalid = true;
      control.requestLayout();
    });

    getChildren().add(label);
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
    var control = getSkinnable();
    TemplatePicker.Template template = control.templateProperty().get();

    label.setTooltip(new Tooltip(template.getFeatureLayer().getName() + " : " + template.getFeatureTemplate().getName()));

    if (control.showNameProperty().get()) {
      label.setText(template.getFeatureTemplate().getName());
    } else {
      label.setText(null);
    }

    var graphic = new Graphic();
    graphic.getAttributes().putAll(template.getFeatureTemplate().getPrototypeAttributes());
    var symbol = template.getFeatureLayer().getRenderer().getSymbol(graphic);
    try {
      Image image = symbol.createSwatchAsync(control.imageWidthProperty().get(),
        control.imageHeightProperty().get(), 1.0f / (float) Screen.getPrimary().getOutputScaleX(), 0x00).get();
      var imageView = new ImageView(image);
      //imageView.setFitHeight(control.imageHeightProperty().get());
      //imageView.setFitWidth(control.imageWidthProperty().get());
      label.setGraphic(imageView);
      //label.setPadding(new Insets(5.0));
    } catch (InterruptedException | ExecutionException e) {
      label.setGraphic(null);
    }
  }
}
