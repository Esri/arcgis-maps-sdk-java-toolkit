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

import com.esri.arcgisruntime.toolkit.Scalebar;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;

public final class LineScaleBarSkin extends ScalebarSkin {

  public LineScaleBarSkin(Scalebar scalebar) {
    super(scalebar);
  }

  @Override
  protected void update(double width, double height) {
    getStackPane().getChildren().clear();

    Line line = new Line();
    line.setStartX(0.0);
    line.setEndX(width);
    line.setStroke(Color.rgb(0xFF, 0xFF, 0xFF));
    line.setStrokeWidth(getLineWidth());
    line.setStrokeLineCap(StrokeLineCap.SQUARE);
    line.setStrokeType(StrokeType.CENTERED);
    line.setEffect(new DropShadow(1.0, 1.5, 1.5, Color.rgb(0x6E, 0x84, 0x8D)));

    getStackPane().getChildren().addAll(line);
  }
}
