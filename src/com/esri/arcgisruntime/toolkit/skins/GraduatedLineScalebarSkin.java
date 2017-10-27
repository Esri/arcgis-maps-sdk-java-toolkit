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
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.StrokeLineCap;

/**
 * A scalebar skin that displays the distance as a line with vertical marks and labels evenly spaced along the line.
 */
public final class GraduatedLineScalebarSkin extends ScalebarSkin {

  private final static double TICK_HEIGHT = 0.75 * HEIGHT;

  private final VBox vBox = new VBox();
  private final Pane labelPane = new Pane();
  private final Path line = new Path();

  /**
   * Creates a new skin instance.
   *
   * @param scalebar the scalebar this skin is for
   */
  public GraduatedLineScalebarSkin(Scalebar scalebar) {
    super(scalebar);
    
    line.setStroke(LINE_COLOR);
    line.setStrokeWidth(STROKE_WIDTH);
    line.setStrokeLineCap(StrokeLineCap.ROUND);
    line.setEffect(new DropShadow(1.0, SHADOW_OFFSET, SHADOW_OFFSET, SHADOW_COLOR));

    getVBox().getChildren().addAll(line, labelPane);
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

    // create a label to use to work out how many labels can fit in the scale bar width
    String sampleLabelString = ScalebarUtil.labelString(displayDistance);
    // possibly the total distance string is shorter than the other labels if they have decimal parts so
    // make sure we use a minimum of 3 characters
    if (sampleLabelString.length() < 3) {
      sampleLabelString = "9.9";
    }
    Label sampleLabel = new Label(sampleLabelString);
    sampleLabel.setPadding(new Insets(0.0, 10.0, 0.0, 10.0));

    double widthOfLabel = calculateRegionWidth(sampleLabel);
    int maximumNumberOfSegments = (int) (displayWidth / widthOfLabel);

    int bestNumberOfSegments = ScalebarUtil.calculateOptimalNumberOfSegments(displayDistance, maximumNumberOfSegments);

    double segmentWidth = displayWidth / bestNumberOfSegments;
    double segmentDistance = displayDistance / bestNumberOfSegments;

    labelPane.getChildren().clear();
    labelPane.setMaxWidth(displayWidth);

    // update the line and labels
    line.getElements().clear();
    line.getElements().addAll(new MoveTo(0.0, 0.0), new LineTo(0.0, HEIGHT));

    Label label;

    for (int i = 0; i < bestNumberOfSegments; ++i) {
      label = new Label(ScalebarUtil.labelString(i * segmentDistance));
      label.setTextFill(TEXT_COLOR);
      // first label is aligned with its left to the edge of the bar while the intermediate
      // labels are centered on the ticks
      if (i > 0) {
        label.setTranslateX((i * segmentWidth) - (calculateRegionWidth(label) / 2.0));
      }
      labelPane.getChildren().add(label);

      line.getElements().addAll(
        new LineTo(i * segmentWidth, HEIGHT),
        new LineTo(i * segmentWidth, HEIGHT - TICK_HEIGHT),
        new MoveTo(i * segmentWidth, HEIGHT));
    }
    // the last label is aligned so its end is at the end of the line so it is done outside the loop
    label = new Label(ScalebarUtil.labelString(displayDistance));
    // translate it into the correct position
    label.setTranslateX((bestNumberOfSegments * segmentWidth) - calculateRegionWidth(label));
    // then add the units on so the end of the number aligns with the end of the bar and the unit is off the end
    label.setText(ScalebarUtil.labelString(displayDistance) + displayUnits.getAbbreviation());
    label.setTextFill(TEXT_COLOR);
    labelPane.getChildren().add(label);

    // the last part of the line
    line.getElements().addAll(new LineTo(displayWidth, HEIGHT), new LineTo(displayWidth, 0.0));

    // move the line and labels into their final position - slightly off center due to the units
    line.setTranslateX(-calculateRegionWidth(new Label(displayUnits.getAbbreviation())) / 2.0);
    labelPane.setTranslateX(-calculateRegionWidth(new Label(displayUnits.getAbbreviation())) / 2.0);

    // adjust for left/right/center alignment
    getVBox().setTranslateX(calculateAlignmentTranslationX(width,
      displayWidth + calculateRegionWidth(new Label(displayUnits.getAbbreviation()))));

    // set invisible if distance is zero
    getVBox().setVisible(displayDistance > 0);
  }

  @Override
  protected double calculateAvailableWidth(double width) {
    return width - (calculateRegionWidth(new Label("mm"))) - STROKE_WIDTH - SHADOW_OFFSET; // TODO - use correct font
  }
}
