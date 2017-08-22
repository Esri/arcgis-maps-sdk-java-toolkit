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
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;

public final class BarScalebarSkin extends ScalebarSkin {

  public BarScalebarSkin(Scalebar scalebar) {
    super(scalebar);
  }

  @Override
  protected void update(double width, double height) {
    getStackPane().getChildren().clear();

//    Line inside = new Line();
//    inside.setStartX(0.0);
//    inside.setEndX(width);
//    inside.setStroke(Color.rgb(0xB7, 0xCB, 0xD3));
//    inside.setStrokeWidth(getLineWidth());
//    inside.setStrokeLineCap(StrokeLineCap.SQUARE);
//    inside.setStrokeType(StrokeType.CENTERED);
//
//    Line outside = new Line();
//    outside.setStartX(0.0);
//    outside.setEndX(width);
//    outside.setStroke(Color.rgb(0xFF, 0xFF, 0xFF));
//    outside.setStrokeWidth(getLineWidth() * 1.75);
//    outside.setStrokeLineCap(StrokeLineCap.SQUARE);
//    outside.setStrokeType(StrokeType.CENTERED);
//    outside.setEffect(new DropShadow(1.0, 1.5, 1.5, Color.rgb(0x6E, 0x84, 0x8D)));
//
//    getStackPane().getChildren().addAll(outside, inside);

    VBox vBox = new VBox();
    vBox.setMaxSize(width, height);
    vBox.setAlignment(Pos.CENTER);

    barStackPane.setMaxSize(width, height);
    Line bar = new Line();
    bar.setStartX(0.0);
    bar.setEndX(width);
    bar.setStroke(Color.rgb(0xB7, 0xCB, 0xD3));
    barStackPane.getChildren().addAll(bar);

    vBox.getChildren().addAll(barStackPane, distanceLabel);
    StackPane.setAlignment(vBox, Pos.CENTER);
    getStackPane().getChildren().addAll(vBox);
  }

  private Label distanceLabel = new Label();
  private StackPane barStackPane = new StackPane();

  @Override
  protected void recalculate() {
    double maxScalebarWidth = calculateMaximumScalebarWidth();
    double maxDistance = calculateDistance(getSkinnable().mapViewProperty().get(),
      getBaseUnit(), maxScalebarWidth);
    double displayDistance = ScalebarUtil.calculateBestScalebarLength(maxDistance, getBaseUnit(), false);
    double displayWidth = (maxScalebarWidth / maxDistance) * displayDistance;
    LinearUnit displayUnits = ScalebarUtil.selectLinearUnit(displayDistance, getSkinnable().getUnitSystem());
    if (displayUnits != getBaseUnit()) {
      displayDistance = getBaseUnit().convertTo(displayUnits, displayDistance);
    }

//    System.out.println(maxScalebarWidth + " " + displayWidth + " " + maxDistance + " " + displayDistance + " " + displayUnits.getAbbreviation());

    distanceLabel.setText(displayDistance + displayUnits.getAbbreviation());
    barStackPane.setScaleX(displayWidth / maxScalebarWidth);
  }

  @Override
  protected double calculateMaximumScalebarWidth() {
    return getSkinnable().getWidth()/* - calculateRegionWidth(new Label("mm"))*/;
  }
}
