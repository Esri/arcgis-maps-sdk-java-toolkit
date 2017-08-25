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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;

public final class GraduatedLineScalebarSkin extends ScalebarSkin {

  private final static double HEIGHT = 8.0;
  private final static double TICK_HEIGHT = 0.75 * HEIGHT;

  private final VBox vBox = new VBox();
  private final HBox hBox = new HBox();
  private final Path line = new Path();

  public GraduatedLineScalebarSkin(Scalebar scalebar) {
    super(scalebar);

    //hBox.setManaged(false);

    // use a vbox to arrange the bar above the labels
    vBox.setAlignment(Pos.CENTER);
    vBox.getChildren().addAll(line, hBox);
    StackPane.setAlignment(vBox, Pos.CENTER);

    // the line
    //line.setManaged(false);
    line.setStroke(Color.WHITE);
    line.setStrokeWidth(3.0);
    line.setStrokeLineCap(StrokeLineCap.ROUND);
    line.setEffect(new DropShadow(1.0, 1.5, 1.5, Color.rgb(0x6E, 0x84, 0x8D)));

    getStackPane().getChildren().addAll(vBox);
  }

  @Override
  protected void update(double width, double height) {
    // work out the scalebar width, the distance it represents and the correct unit label
    double availableWidth = width - calculateRegionWidth(new Label("mm")); // TODO - use correct font
    double maxDistance = calculateDistance(getSkinnable().mapViewProperty().get(),
      getBaseUnit(), availableWidth);
    double displayDistance = ScalebarUtil.calculateBestScalebarLength(maxDistance, getBaseUnit(), true);
    double displayWidth = displayDistance / maxDistance * availableWidth;
    LinearUnit displayUnits = ScalebarUtil.selectLinearUnit(displayDistance, getSkinnable().getUnitSystem());
    if (displayUnits != getBaseUnit()) {
      displayDistance = getBaseUnit().convertTo(displayUnits, displayDistance);
      System.out.println(getBaseUnit().convertTo(displayUnits, maxDistance) + displayUnits.getAbbreviation());
    }

    // update the line
    line.getElements().clear();
    //line.setTranslateX((width - displayWidth) / 2.0);
    line.getElements().addAll(new MoveTo(0.0, -HEIGHT), new LineTo(0.0, 0.0), new LineTo(displayWidth, 0.0), new LineTo(displayWidth, -HEIGHT));

    // create a label to use to work out how many labels can fit in the scale bar width
    String sampleLabelString = ScalebarUtil.labelString(displayDistance);
    // possibly the total distance string is shorter than the other labels if they have decimal parts so
    // make sure we use a minimum of 3 characters
    if (sampleLabelString.length() < 3) {
      sampleLabelString = "9.9";
    }
    Label sampleLabel = new Label(sampleLabelString);

    double widthOfLabel = calculateRegionWidth(new Label());
    int maximumNumberOfSegments = (int) (displayWidth / widthOfLabel);

    int bestNumberOfSegments = ScalebarUtil.calculateOptimalNumberOfSegments(displayDistance, maximumNumberOfSegments);
//    System.out.println(maximumNumberOfSegments + " " + bestNumberOfSegments + " " + displayDistance + " " + maxDistance);

    double segmentWidth = displayWidth / bestNumberOfSegments;
    double segmentDistance = displayDistance / bestNumberOfSegments;

    hBox.getChildren().clear();
    //hBox.setManaged(false);
    //hBox.setAlignment(Pos.CENTER);
    //hBox.setSpacing(10);
    Label l = new Label("0");
//    l.setMinWidth(segmentWidth);
//    l.setAlignment(Pos.CENTER);
//    hBox.getChildren().add(l);
//    for (int i = 1; i < bestNumberOfSegments; ++i) {
//      l = new Label(ScalebarUtil.labelString(i * segmentDistance));
//      l.setMinWidth(segmentWidth);
//      hBox.getChildren().add(l);
//    }
    l = new Label(ScalebarUtil.labelString(displayDistance));
    l.setMinWidth(segmentWidth);
    l.setAlignment(Pos.CENTER_LEFT);
    hBox.getChildren().add(l);
  }

  @Override
  protected double calculateMaximumScalebarWidth() {
    return getSkinnable().getWidth() - calculateRegionWidth(new Label("mm"));
  }
}
