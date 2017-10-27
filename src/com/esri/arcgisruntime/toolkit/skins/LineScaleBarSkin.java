/*
 * Copyright 2017 Esri
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
import javafx.scene.layout.VBox;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.StrokeLineCap;

/**
 * A scalebar skin that displays the distance as a line with vertical marks at the start and end and a single distance
 * label.
 */
public final class LineScaleBarSkin extends ScalebarSkin {

  private final VBox vBox = new VBox();
  private final Label distanceLabel = new Label();
  private final Path line = new Path();

  /**
   * Creates a new skin instance.
   *
   * @param scalebar the scalebar this skin is for
   */
  public LineScaleBarSkin(Scalebar scalebar) {
    super(scalebar);
    
    line.setStroke(LINE_COLOR);
    line.setStrokeWidth(STROKE_WIDTH);
    line.setStrokeLineCap(StrokeLineCap.ROUND);
    line.setEffect(new DropShadow(1.0, SHADOW_OFFSET, SHADOW_OFFSET, SHADOW_COLOR));

    distanceLabel.setTextFill(TEXT_COLOR);

    getVBox().getChildren().addAll(line, distanceLabel);
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

    // update the line
    line.getElements().clear();
    line.getElements().addAll(
      new MoveTo(0.0, HEIGHT),
      new LineTo(0.0, 0.0),
      new MoveTo(0.0, HEIGHT),
      new LineTo(displayWidth, HEIGHT),
      new LineTo(displayWidth, 0.0));

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
}
