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

import java.util.concurrent.ExecutionException;

import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.toolkit.FeatureTemplatePickerItem;
import com.esri.arcgisruntime.toolkit.FeatureTemplatePicker;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;

/**
 * Defines a skin for a {@link FeatureTemplatePickerItem}.
 *
 * @since 100.6.0
 */
public final class FeatureTemplatePickerItemSkin extends SkinBase<FeatureTemplatePickerItem> {

  private boolean invalid = true;
  private final Label label = new Label();

  /**
   * Creates a new skin instance.
   *
   * @param control the control this skin is for
   * @since 100.6.0
   */
  public FeatureTemplatePickerItemSkin(FeatureTemplatePickerItem control) {
    super(control);

    control.widthProperty().addListener(observable -> invalid = true);
    control.heightProperty().addListener(observable -> invalid = true);
    control.insetsProperty().addListener(observable -> invalid = true);

    control.symbolHeightProperty().addListener(observable -> {
      invalid = true;
      control.requestLayout();
    });
    control.symbolWidthProperty().addListener(observable -> {
      invalid = true;
      control.requestLayout();
    });

    control.showNameProperty().addListener(observable -> {
      invalid = true;
      control.requestLayout();
    });

    label.setContentDisplay(ContentDisplay.TOP);

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

  /**
   * Updates the skin when the content has become invalid, for example if the dimensions of the cell have been changed.
   *
   * @param contentWidth the content width
   * @param contentHeight the content height
   * @since 100.6.0
   */
  private void update(double contentWidth, double contentHeight) {
    var control = getSkinnable();
    FeatureTemplatePicker.Template template = control.templateProperty().get();

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
      Image image = symbol.createSwatchAsync(control.symbolWidthProperty().get(),
        control.symbolHeightProperty().get(), (float) Screen.getPrimary().getOutputScaleX(), 0x00).get();
      var imageView = new ImageView(image);
      imageView.setFitWidth(control.symbolWidthProperty().get());
      imageView.setFitHeight(control.symbolHeightProperty().get());

      label.setGraphic(imageView);
    } catch (InterruptedException | ExecutionException | IllegalArgumentException e) {
      label.setGraphic(null);
    }
    label.setPrefSize(control.symbolWidthProperty().get(), control.symbolHeightProperty().get());
  }
}
