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
import javafx.geometry.Insets;
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

public final class GraduatedLineScalebarSkin extends ScalebarSkin {

  private final static double HEIGHT = 8.0;
  private final static double TICK_HEIGHT = 0.75 * HEIGHT;

  private final VBox vBox = new VBox();
  private final Pane labelPane = new Pane();
  private final Path line = new Path();

  public GraduatedLineScalebarSkin(Scalebar scalebar) {
    super(scalebar);

    // use a vbox to arrange the bar above the labels
    vBox.setAlignment(Pos.CENTER);
    vBox.getChildren().addAll(line, labelPane);
    StackPane.setAlignment(vBox, Pos.CENTER);

    // the line
    line.setStroke(Color.WHITE);
    line.setStrokeWidth(3.0);
    line.setStrokeLineCap(StrokeLineCap.ROUND);
    line.setEffect(new DropShadow(1.0, 1.5, 1.5, Color.rgb(0x6E, 0x84, 0x8D)));

    getStackPane().getChildren().addAll(vBox);
  }

  @Override
  protected void update(double width, double height) {
    // work out the scalebar width, the distance it represents and the correct unit label
    double availableWidth = width - (calculateRegionWidth(new Label("mm"))); // TODO - use correct font
    double maxDistance = calculateDistance(getSkinnable().mapViewProperty().get(),
      getBaseUnit(), width);

    maxDistance *= availableWidth / width;
    double displayDistance = ScalebarUtil.calculateBestScalebarLength(maxDistance, getBaseUnit(), true);

    double displayWidth = displayDistance / maxDistance * availableWidth;
    LinearUnit displayUnits = ScalebarUtil.selectLinearUnit(displayDistance, getSkinnable().getUnitSystem());
    if (displayUnits != getBaseUnit()) {
      displayDistance = getBaseUnit().convertTo(displayUnits, displayDistance);
    }

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
    line.getElements().add(new MoveTo(0.0, -HEIGHT));
    line.getElements().add(new LineTo(0.0, 0.0));

    Label label;
    for (int i = 0; i < bestNumberOfSegments; ++i) {
      label = new Label(ScalebarUtil.labelString(i * segmentDistance));
      label.setTranslateX((i * segmentWidth) - (calculateRegionWidth(label) / 2.0));
      labelPane.getChildren().add(label);
      // we've already drawn the first part of the line so only draw ticks for the intermediate values
      if (i > 0) {
        line.getElements().add(new LineTo(i * segmentWidth, 0.0));
        line.getElements().add(new LineTo(i * segmentWidth, -TICK_HEIGHT));
        line.getElements().add(new MoveTo(i * segmentWidth, 0.0));
      }
    }
    // the last label is aligned so its end is at the end of the line so it is done outside the loop
    label = new Label(ScalebarUtil.labelString(displayDistance));
    // translate it into the correct position
    label.setTranslateX((bestNumberOfSegments * segmentWidth) - calculateRegionWidth(label));
    // then add the units on so the end of the number aligns with the end of the bar and the unit is of the end
    label.setText(ScalebarUtil.labelString(displayDistance) + displayUnits.getAbbreviation());
    labelPane.getChildren().add(label);

    // the last part of the line
    line.getElements().add(new LineTo(displayWidth, 0.0));
    line.getElements().add(new LineTo(displayWidth, -HEIGHT));

    // move the line and labels into their final position - slightly off center due to the units
    line.setTranslateX(-calculateRegionWidth(new Label(displayUnits.getAbbreviation())) / 2.0);
    labelPane.setTranslateX(-calculateRegionWidth(new Label(displayUnits.getAbbreviation())) / 2.0);

    // adjust for left/right/center alignment
    getStackPane().setTranslateX(calculateAlignmentTranslationX(width, displayWidth + calculateRegionWidth(new Label(displayUnits.getAbbreviation()))));

    // set invisible if distance is zero
    getStackPane().setVisible(displayDistance > 0);
  }
}
