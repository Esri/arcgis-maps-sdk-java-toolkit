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
import javafx.scene.shape.Rectangle;

public final class BarScalebarSkin extends ScalebarSkin {

  public BarScalebarSkin(Scalebar scalebar) {
    super(scalebar);
  }

  @Override
  protected void update(double width, double height) {
    getStackPane().getChildren().clear();

    VBox vBox = new VBox();
    vBox.setAlignment(Pos.CENTER);

    Rectangle innerBar = new Rectangle();
    innerBar.setWidth(width - 4);
    innerBar.setHeight(8.0);
    innerBar.setFill(Color.rgb(0xB7, 0xCB, 0xD3));

    Rectangle outerBar = new Rectangle();
    outerBar.setWidth(width);
    outerBar.setHeight(12.0);
    outerBar.setFill(Color.rgb(0xFF, 0xFF, 0xFF));
    outerBar.setEffect(new DropShadow(1.0, 1.5, 1.5, Color.rgb(0x6E, 0x84, 0x8D)));
    outerBar.setArcWidth(5);
    outerBar.setArcHeight(5);

    barStackPane.getChildren().addAll(outerBar, innerBar);

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
    return getSkinnable().getWidth();
  }
}
