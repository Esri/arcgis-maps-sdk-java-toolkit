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

import com.esri.arcgisruntime.UnitSystem;
import com.esri.arcgisruntime.geometry.LinearUnit;
import com.esri.arcgisruntime.geometry.LinearUnitId;
import com.esri.arcgisruntime.toolkit.Scalebar;
import com.esri.arcgisruntime.toolkit.ScalebarUtil;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.StrokeLineCap;

/**
 * A scalebar skin that displays the distance in both metric and imperial units. The line is the same as
 * {@link LineScaleBarSkin} with the addition of an extra mark on the bottom for the alternate measurement.
 */
public final class DualUnitScalebarSkin extends ScalebarSkin {

  private final VBox vBox = new VBox();
  private final Pane primaryLabelPane = new Pane();
  private final Pane secondaryLabelPane = new Pane();
  private final Path line = new Path();

  /**
   * Creates a new skin instance.
   *
   * @param scalebar the scalebar this skin is for
   */
  public DualUnitScalebarSkin(Scalebar scalebar) {
    super(scalebar);
    
    line.setStroke(LINE_COLOR);
    line.setStrokeWidth(STROKE_WIDTH);
    line.setStrokeLineCap(StrokeLineCap.ROUND);
    line.setEffect(new DropShadow(1.0, SHADOW_OFFSET, SHADOW_OFFSET, SHADOW_COLOR));

    getVBox().getChildren().addAll(primaryLabelPane, line, secondaryLabelPane);
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

    // do the same calculations for the secondary units which will be on the bottom of the line
    UnitSystem secondaryUnitSystem = getUnitSystem() == UnitSystem.METRIC ? UnitSystem.IMPERIAL : UnitSystem.METRIC;
    LinearUnit secondaryBaseUnit = secondaryUnitSystem == UnitSystem.METRIC ? new LinearUnit(LinearUnitId.METERS) : new LinearUnit(LinearUnitId.FEET);
    double secondaryMaxDistance = calculateDistance(getSkinnable().mapViewProperty().get(), secondaryBaseUnit, availableWidth);

    double secondaryDisplayDistance = ScalebarUtil.calculateBestScalebarLength(secondaryMaxDistance, secondaryBaseUnit, false);
    double secondaryDisplayWidth = calculateDisplayWidth(secondaryDisplayDistance, secondaryMaxDistance, availableWidth);
    LinearUnit secondaryDisplayUnits = ScalebarUtil.selectLinearUnit(secondaryDisplayDistance, secondaryUnitSystem);
    secondaryDisplayDistance = ScalebarUtil.calculateDistanceInDisplayUnits(secondaryDisplayDistance, secondaryBaseUnit, secondaryDisplayUnits);

    // the line width is the longest of the two display widths
    double lineWidth = Math.max(displayWidth, secondaryDisplayWidth);

    primaryLabelPane.getChildren().clear();
    primaryLabelPane.setMaxWidth(lineWidth);
    secondaryLabelPane.getChildren().clear();
    secondaryLabelPane.setMaxWidth(lineWidth);

    // update the line
    line.getElements().clear();
    line.getElements().addAll(
      new MoveTo(0.0, HEIGHT * 2.0),
      new LineTo(0.0, 0.0),
      new MoveTo(0.0, HEIGHT),
      new LineTo(lineWidth, HEIGHT),
      new MoveTo(displayWidth, HEIGHT),
      new LineTo(displayWidth, 0.0),
      new MoveTo(secondaryDisplayWidth, HEIGHT * 2.0),
      new LineTo(secondaryDisplayWidth, HEIGHT));

    // label the ticks
    // the last label is aligned so its end is at the end of the line so it is done outside the loop
    Label primaryLabel = new Label(ScalebarUtil.labelString(displayDistance));
    // translate it into the correct position
    primaryLabel.setTranslateX(displayWidth - calculateRegionWidth(primaryLabel));
    // then add the units on so the end of the number aligns with the end of the bar and the unit is off the end
    primaryLabel.setText(ScalebarUtil.labelString(displayDistance) + displayUnits.getAbbreviation());
    primaryLabel.setTextFill(TEXT_COLOR);
    primaryLabelPane.getChildren().add(primaryLabel);

    Label secondaryLabel = new Label(ScalebarUtil.labelString(secondaryDisplayDistance));
    secondaryLabel.setTranslateX(secondaryDisplayWidth - calculateRegionWidth(secondaryLabel));
    // then add the units on so the end of the number aligns with the end of the bar and the unit is off the end
    secondaryLabel.setText(ScalebarUtil.labelString(secondaryDisplayDistance) + secondaryDisplayUnits.getAbbreviation());
    secondaryLabel.setTextFill(TEXT_COLOR);
    secondaryLabelPane.getChildren().add(secondaryLabel);

    // the unit label that will be at the end of the line
    Label endUnits = new Label(displayWidth >= secondaryDisplayWidth ? displayUnits.getAbbreviation() : secondaryDisplayUnits.getAbbreviation());

    // move the line and labels into their final position - slightly off center due to the units
    line.setTranslateX(-calculateRegionWidth(endUnits) / 2.0);
    primaryLabelPane.setTranslateX(-calculateRegionWidth(new Label(displayUnits.getAbbreviation())) / 2.0);
    secondaryLabelPane.setTranslateX(-calculateRegionWidth(new Label(secondaryDisplayUnits.getAbbreviation())) / 2.0);

    // adjust for left/right/center alignment
    getVBox().setTranslateX(calculateAlignmentTranslationX(width, lineWidth + calculateRegionWidth(endUnits)));

    // set invisible if distance is zero
    getVBox().setVisible(displayDistance > 0);
  }

  @Override
  protected double calculateAvailableWidth(double width) {
    return width - (calculateRegionWidth(new Label("mm"))) - STROKE_WIDTH - SHADOW_OFFSET; // TODO - use correct font
  }
}
