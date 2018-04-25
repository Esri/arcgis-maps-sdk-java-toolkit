/*
 * Copyright 2018 Esri
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

import com.esri.arcgisruntime.geometry.LinearUnit;
import com.esri.arcgisruntime.toolkit.Scalebar;
import com.esri.arcgisruntime.toolkit.ScalebarUtil;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.shape.Rectangle;

/**
 * A scalebar skin that displays the distance as a solid bar with a single label.
 *
 * @since 100.2.1
 */
public final class BarScalebarSkin extends ScalebarSkin {

  private final Label distanceLabel = new Label();
  private final Rectangle bar = new Rectangle();
  private final Rectangle outerBar = new Rectangle();

  /**
   * Creates a new skin instance.
   *
   * @param scalebar the scalebar this skin is for
   * @since 100.2.1
   */
  public BarScalebarSkin(Scalebar scalebar) {
    super(scalebar);

    bar.setFill(FILL_COLOR);
    bar.setHeight(HEIGHT);
    bar.setStroke(LINE_COLOR);
    bar.setStrokeWidth(STROKE_WIDTH);
    bar.setEffect(new DropShadow(1.0, SHADOW_OFFSET, SHADOW_OFFSET, SHADOW_COLOR));
    bar.setArcWidth(1.5);
    bar.setArcHeight(1.5);

    distanceLabel.setTextFill(TEXT_COLOR);

    getVBox().getChildren().addAll(bar, distanceLabel);
  }

  @Override
  protected void update(double width, double height) {
    // workout the scalebar width, the distance it represents and the correct unit label
    // workout how much space is available
    double availableWidth = calculateAvailableWidth(width);
    // workout the maximum distance the scalebar could show
    double maxDistance = calculateDistance(getSkinnable().mapViewProperty().get(), getBaseUnit(), availableWidth);
    // get a distance that is a nice looking number
    double displayDistance = ScalebarUtil.calculateBestScalebarLength(maxDistance, getBaseUnit(), false);
    // workout what the bar width is to match the distance we're going to display
    double displayWidth = calculateDisplayWidth(displayDistance, maxDistance, availableWidth);
    // decide on the actual unit e.g. km or m
    LinearUnit displayUnits = ScalebarUtil.selectLinearUnit(displayDistance, getUnitSystem());
    // get the distance to be displayed in that unit
    displayDistance = ScalebarUtil.calculateDistanceInDisplayUnits(displayDistance, getBaseUnit(), displayUnits);

    // update the bar size
    bar.setWidth(displayWidth);
    outerBar.setWidth(displayWidth);

    // update the label
    distanceLabel.setText(ScalebarUtil.labelString(displayDistance) + displayUnits.getAbbreviation());

    // adjust for left/right/center alignment
    getVBox().setTranslateX(calculateAlignmentTranslationX(width, displayWidth));

    // set invisible if distance is zero
    getVBox().setVisible(displayDistance > 0);
  }

  @Override
  protected double calculateAvailableWidth(double width) {
    return width - STROKE_WIDTH - SHADOW_OFFSET;
  }

  @Override
  protected double computePrefHeight(
    double width, double topInset, double rightInset, double bottomInset, double leftInset) {
    return topInset + bottomInset + HEIGHT + STROKE_WIDTH + calculateRegion(new Label()).getHeight();
  }
}
