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
import javafx.scene.shape.Rectangle;

/**
 * A scalebar skin that displays the distance as an alternating color solid bar with labels on each segment.
 *
 * @since 100.2.1
 */
public final class AlternatingBarScalebarSkin extends ScalebarSkin {

  private final Pane labelPane = new Pane();
  private final Pane segmentPane = new Pane();

  /**
   * Creates a new skin instance.
   *
   * @param scalebar the scalebar this skin is for
   * @since 100.2.1
   */
  public AlternatingBarScalebarSkin(Scalebar scalebar) {
    super(scalebar);

    getVBox().getChildren().addAll(segmentPane, labelPane);
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
    // apply some padding so the labels have some space around them
    sampleLabel.setPadding(new Insets(0.0, 10.0, 0.0, 10.0));

    double widthOfLabel = calculateRegion(sampleLabel).getWidth();
    int maximumNumberOfSegments = (int) (displayWidth / widthOfLabel);

    int bestNumberOfSegments = ScalebarUtil.calculateOptimalNumberOfSegments(displayDistance, maximumNumberOfSegments);

    double segmentWidth = displayWidth / bestNumberOfSegments;
    double segmentDistance = displayDistance / bestNumberOfSegments;

    // clear out all the labels, segments and dividers
    labelPane.getChildren().clear();
    labelPane.setMaxWidth(displayWidth);

    segmentPane.getChildren().clear();
    segmentPane.setMaxWidth(displayWidth);

    Label label;
    Rectangle barSegment;

    for (int i = 0; i < bestNumberOfSegments; ++i) {
      label = new Label(ScalebarUtil.labelString(i * segmentDistance));
      label.setTextFill(TEXT_COLOR);

      // first label is aligned with its left to the edge of the bar while the intermediate
      // labels are centered on the dividers
      if (i > 0) {
        label.setTranslateX((i * segmentWidth) - (calculateRegion(label).getWidth() / 2.0));
      }
      labelPane.getChildren().add(label);

      // create a rectangle for the segment and translate it into the correct position
      barSegment = new Rectangle();
      barSegment.setHeight(HEIGHT);
      barSegment.setWidth(segmentWidth);
      barSegment.setTranslateX(i * segmentWidth);
      barSegment.setTranslateY(HEIGHT / 4.0);
      barSegment.setStroke(LINE_COLOR);
      barSegment.setStrokeWidth(STROKE_WIDTH);
      barSegment.setEffect(new DropShadow(1.0, SHADOW_OFFSET, SHADOW_OFFSET, SHADOW_COLOR));
      barSegment.setArcWidth(1.5);
      barSegment.setArcHeight(1.5);
      if (i % 2 == 0) {
        barSegment.setFill(FILL_COLOR);
      } else {
        barSegment.setFill(ALTERNATE_FILL_COLOR);
      }

      segmentPane.getChildren().add(barSegment);
    }

    // the last label is aligned so its end is at the end of the line so it is done outside the loop
    label = new Label(ScalebarUtil.labelString(displayDistance));
    // translate it into the correct position
    label.setTranslateX((bestNumberOfSegments * segmentWidth) - calculateRegion(label).getWidth());
    // then add the units on so the end of the number aligns with the end of the bar and the unit is off the end
    label.setText(ScalebarUtil.labelString(displayDistance) + displayUnits.getAbbreviation());
    label.setTextFill(TEXT_COLOR);
    labelPane.getChildren().add(label);

    // move the bar and labels into their final position - slightly off center due to the units
    Label abbreviationLabel = new Label(displayUnits.getAbbreviation());
    segmentPane.setTranslateX(-calculateRegion(abbreviationLabel).getWidth() / 2.0);
    labelPane.setTranslateX(-calculateRegion(abbreviationLabel).getWidth() / 2.0);

    // adjust for left/right/center alignment
    getVBox().setTranslateX(
      calculateAlignmentTranslationX(width,
        displayWidth + calculateRegion(new Label(displayUnits.getAbbreviation())).getWidth()));

    // set invisible if distance is zero
    getVBox().setVisible(displayDistance > 0);
  }

  @Override
  protected double calculateAvailableWidth(double width) {
    return width - (calculateRegion(new Label("mm")).getWidth()) - SHADOW_OFFSET;
  }

  @Override
  protected double computePrefHeight(
    double width, double topInset, double rightInset, double bottomInset, double leftInset) {
    return topInset + bottomInset + HEIGHT + STROKE_WIDTH + calculateRegion(new Label()).getHeight();
  }
}
