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
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

/**
 * A scalebar skin that displays the distance as a solid bar with a single label.
 */
public final class BarScalebarSkin extends ScalebarSkin {

  private final VBox vBox = new VBox();
  private final Label distanceLabel = new Label();
  private final Rectangle bar = new Rectangle();
  private final Rectangle outerBar = new Rectangle();

  /**
   * Creates a new skin instance.
   * @param scalebar the scalebar this skin is for
   */
  public BarScalebarSkin(Scalebar scalebar) {
    super(scalebar);

    // create the nodes for the bar and label

    // use a vbox to arrange the bar above the label
    vBox.setAlignment(Pos.CENTER);
    vBox.getChildren().addAll(bar, distanceLabel);

    // the bar
    bar.setFill(FILL_COLOR);
    bar.setHeight(HEIGHT);
    bar.setStroke(LINE_COLOR);
    bar.setStrokeWidth(STROKE_WIDTH);
    bar.setEffect(new DropShadow(1.0, SHADOW_OFFSET, SHADOW_OFFSET, SHADOW_COLOR));
    bar.setArcWidth(1.5);
    bar.setArcHeight(1.5);

    distanceLabel.setTextFill(TEXT_COLOR);

    getStackPane().getChildren().addAll(vBox);
  }

  @Override
  protected void update(double width, double height) {
    // work out the scalebar width, the distance it represents and the correct unit label

    // workout how much space is available
    double availableWidth = width - STROKE_WIDTH - SHADOW_OFFSET;

    // workout the maximum distance the scalebar coudl show
    double maxDistance = calculateDistance(getSkinnable().mapViewProperty().get(), getBaseUnit(), availableWidth);

    // get a distance that is a nice looking number
    double displayDistance = ScalebarUtil.calculateBestScalebarLength(maxDistance, getBaseUnit(), false);

    // workout what the bar width is to match the distance we're going to display
    double displayWidth = displayDistance / maxDistance * availableWidth;

    // decide on the actual unit e.g. km or m
    LinearUnit displayUnits = ScalebarUtil.selectLinearUnit(displayDistance, getUnitSystem());
    if (displayUnits != getBaseUnit()) {
      displayDistance = getBaseUnit().convertTo(displayUnits, displayDistance);
    }

    // update the bar size
    bar.setWidth(displayWidth);
    outerBar.setWidth(displayWidth);

    // update the label
    distanceLabel.setText(ScalebarUtil.labelString(displayDistance) + displayUnits.getAbbreviation());

    // adjust for left/right/center alignment
    getStackPane().setTranslateX(calculateAlignmentTranslationX(width, displayWidth));

    // set invisible if distance is zero
    getStackPane().setVisible(displayDistance > 0);
  }
}
