/*
 COPYRIGHT 1995-2017 ESRI

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

import com.esri.arcgisruntime.UnitSystem;
import com.esri.arcgisruntime.geometry.LinearUnit;
import com.esri.arcgisruntime.geometry.LinearUnitId;
import com.esri.arcgisruntime.toolkit.Scalebar;
import com.esri.arcgisruntime.toolkit.ScalebarUtil;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.StrokeLineCap;

public final class DualUnitScalebarSkin extends ScalebarSkin {

  private final static double HEIGHT = 8.0;
  private final static double STROKE_WIDTH = 3.0;

  private final VBox vBox = new VBox();
  private final Pane primaryLabelPane = new Pane();
  private final Pane secondaryLabelPane = new Pane();
  private final Path line = new Path();

  public DualUnitScalebarSkin(Scalebar scalebar) {
    super(scalebar);

    // use a vbox to arrange the bar above the labels
    vBox.setAlignment(Pos.CENTER);
    vBox.getChildren().addAll(primaryLabelPane, line, secondaryLabelPane);
    StackPane.setAlignment(vBox, Pos.CENTER);

    // the line
    line.setStroke(Color.WHITE);
    line.setStrokeWidth(STROKE_WIDTH);
    line.setStrokeLineCap(StrokeLineCap.ROUND);
    line.setEffect(new DropShadow(1.0, SHADOW_OFFSET, SHADOW_OFFSET, Color.rgb(0x6E, 0x84, 0x8D)));

    getStackPane().getChildren().addAll(vBox);
  }

  @Override
  protected void update(double width, double height) {
    // work out the scalebar width, the distance it represents and the correct unit label
    double availableWidth = width - (calculateRegionWidth(new Label("mm"))) - STROKE_WIDTH - SHADOW_OFFSET; // TODO - use correct font
    double maxDistance = calculateDistance(getSkinnable().mapViewProperty().get(), getBaseUnit(), /*width*/availableWidth);

    //maxDistance *= availableWidth / width;
    double displayDistance = ScalebarUtil.calculateBestScalebarLength(maxDistance, getBaseUnit(), false);

    double displayWidth = displayDistance / maxDistance * availableWidth;
    LinearUnit displayUnits = ScalebarUtil.selectLinearUnit(displayDistance, getUnitSystem());
    if (displayUnits != getBaseUnit()) {
      displayDistance = getBaseUnit().convertTo(displayUnits, displayDistance);
    }

    UnitSystem secondaryUnitSystem = getUnitSystem() == UnitSystem.METRIC ? UnitSystem.IMPERIAL : UnitSystem.METRIC;
    LinearUnit secondaryBaseUnit = secondaryUnitSystem == UnitSystem.METRIC ? new LinearUnit(LinearUnitId.METERS) : new LinearUnit(LinearUnitId.FEET);
    double secondaryMaxDistance = calculateDistance(getSkinnable().mapViewProperty().get(), secondaryBaseUnit, displayWidth - STROKE_WIDTH);

    double secondaryDisplayDistance = ScalebarUtil.calculateBestScalebarLength(secondaryMaxDistance, secondaryBaseUnit, false);
    double secondaryDisplayWidth = secondaryDisplayDistance / secondaryMaxDistance * displayWidth;
    LinearUnit secondaryDisplayUnits = ScalebarUtil.selectLinearUnit(secondaryDisplayDistance, secondaryUnitSystem);
    if (secondaryDisplayUnits != secondaryBaseUnit) {
      secondaryDisplayDistance = secondaryBaseUnit.convertTo(secondaryDisplayUnits, secondaryDisplayDistance);
    }

    primaryLabelPane.getChildren().clear();
    primaryLabelPane.setMaxWidth(displayWidth);
    secondaryLabelPane.getChildren().clear();
    secondaryLabelPane.setMaxWidth(displayWidth);

    // update the line
    line.getElements().clear();
    line.getElements().addAll(new MoveTo(0.0, -HEIGHT),
      new LineTo(0.0, 0.0),
      new LineTo(0.0, HEIGHT),
      new MoveTo(0.0, 0.0),
      new LineTo(displayWidth, 0.0),
      new LineTo(displayWidth, -HEIGHT),
      new MoveTo(secondaryDisplayWidth, 0.0),
      new LineTo(secondaryDisplayWidth, HEIGHT));

    // label the ticks
    Label primaryLabel;

    // the last label is aligned so its end is at the end of the line so it is done outside the loop
    primaryLabel = new Label(ScalebarUtil.labelString(displayDistance));
    // translate it into the correct position
    primaryLabel.setTranslateX(displayWidth - calculateRegionWidth(primaryLabel));
    // then add the units on so the end of the number aligns with the end of the bar and the unit is of the end
    primaryLabel.setText(ScalebarUtil.labelString(displayDistance) + displayUnits.getAbbreviation());
    primaryLabelPane.getChildren().add(primaryLabel);

    Label secondaryLabel;
    secondaryLabel = new Label(ScalebarUtil.labelString(secondaryDisplayDistance));
    secondaryLabel.setTranslateX(secondaryDisplayWidth - calculateRegionWidth(secondaryLabel));
    // then add the units on so the end of the number aligns with the end of the bar and the unit is of the end
    secondaryLabel.setText(ScalebarUtil.labelString(secondaryDisplayDistance) + secondaryDisplayUnits.getAbbreviation());
    secondaryLabelPane.getChildren().add(secondaryLabel);

    // move the line and labels into their final position - slightly off center due to the units
    line.setTranslateX(-calculateRegionWidth(new Label(displayUnits.getAbbreviation())) / 2.0);
    primaryLabelPane.setTranslateX(-calculateRegionWidth(new Label(displayUnits.getAbbreviation())) / 2.0);
    secondaryLabelPane.setTranslateX(-calculateRegionWidth(new Label(secondaryDisplayUnits.getAbbreviation())) / 2.0);

    // adjust for left/right/center alignment
    getStackPane().setTranslateX(calculateAlignmentTranslationX(width,displayWidth + calculateRegionWidth(new Label(displayUnits.getAbbreviation()))));

    // set invisible if distance is zero
    getStackPane().setVisible(displayDistance > 0);
  }
}
