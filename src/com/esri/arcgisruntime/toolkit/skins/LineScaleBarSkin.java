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

import com.esri.arcgisruntime.geometry.LinearUnit;
import com.esri.arcgisruntime.toolkit.Scalebar;
import com.esri.arcgisruntime.toolkit.ScalebarUtil;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;

public final class LineScaleBarSkin extends ScalebarSkin {

  private final static double HEIGHT = 8.0;

  private final VBox vBox = new VBox();
  private final Label distanceLabel = new Label();
  private final Path line = new Path();

  public LineScaleBarSkin(Scalebar scalebar) {
    super(scalebar);

    // create the nodes for the bar and label

    // use a vbox to arrange the bar above the label
    vBox.setAlignment(Pos.CENTER);
    vBox.getChildren().addAll(line, distanceLabel);
    StackPane.setAlignment(vBox, Pos.CENTER);

    // the line
    //line.setManaged(false);
    //line.setStrokeType(StrokeType.INSIDE);
    line.setStroke(Color.WHITE);
    line.setStrokeWidth(3.0);
    line.setStrokeLineCap(StrokeLineCap.ROUND);
    line.setEffect(new DropShadow(1.0, 1.5, 1.5, Color.rgb(0x6E, 0x84, 0x8D)));

    getStackPane().getChildren().addAll(vBox);
  }

  @Override
  protected void update(double width, double height) {
    // work out the scalebar width, the distance it represents and the correct unit label
    double maxDistance = calculateDistance(getSkinnable().mapViewProperty().get(),
      getBaseUnit(), width);
    double displayDistance = ScalebarUtil.calculateBestScalebarLength(maxDistance, getBaseUnit(), false);
    //double displayWidth = (width / maxDistance) * displayDistance;
    double displayWidth = displayDistance / maxDistance * width;
    LinearUnit displayUnits = ScalebarUtil.selectLinearUnit(displayDistance, getSkinnable().getUnitSystem());
    if (displayUnits != getBaseUnit()) {
      displayDistance = getBaseUnit().convertTo(displayUnits, displayDistance);
      System.out.println("Line: " + getBaseUnit().convertTo(displayUnits, maxDistance) + displayUnits.getAbbreviation());
    }

    // update the line
    line.getElements().clear();
    //line.setTranslateX((width - displayWidth) / 2.0);
    line.getElements().addAll(new MoveTo(0.0, -HEIGHT), new LineTo(0.0, 0.0), new LineTo(displayWidth, 0.0), new LineTo(displayWidth, -HEIGHT));

    // update the label
    distanceLabel.setText(ScalebarUtil.labelString(displayDistance) + displayUnits.getAbbreviation());

    // adjust for left/right/center alignment
//    double translateX = calculateAlignmentTranslationX(width, displayWidth);
//    line.setTranslateX(line.getTranslateX() + translateX);
//    distanceLabel.setTranslateX(translateX);
    getStackPane().setTranslateX(calculateAlignmentTranslationX(width, displayWidth));
    distanceLabel.setVisible(displayDistance > 0); // hide the label if the distance is zero
  }

  @Override
  protected double calculateMaximumScalebarWidth() {
    return getSkinnable().getWidth();
  }
}
