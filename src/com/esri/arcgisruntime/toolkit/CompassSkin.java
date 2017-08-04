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

package Toolkit;

import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.VLineTo;

public final class CompassSkin extends SkinBase<Compass> {

  private boolean invalid = true;
  private final StackPane compassStackPane = new StackPane();

  CompassSkin(Compass control) {
    super(control);

    control.widthProperty().addListener(o -> invalid = true);
    control.heightProperty().addListener(o -> invalid = true);
    // bind to the heading but also subtract rotation of the to ensure north stays pointing up
    compassStackPane.rotateProperty().bind(control.headingProperty().negate().subtract(control.rotateProperty()));

    getChildren().add(compassStackPane);
  }

  @Override
  protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
    if (invalid) {
      update(contentWidth, contentHeight);
      System.out.println(getSkinnable().getWidth() + " " + getSkinnable().getHeight());
      invalid = false;
    }
    getChildren().forEach(c -> layoutInArea(c, contentX, contentY, contentWidth, contentHeight, -1, HPos.CENTER, VPos.CENTER));
  }

//  @Override
//  protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double
//    leftInset) {
//    return computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
//  }
//
//  @Override
//  protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double
//    leftInset) {
//    return computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
//  }
//
//
//  @Override
//  protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double
//    leftInset) {
//    return computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
//  }
//
//  @Override
//  protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double
//    leftInset) {
//    return computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
//  }
//
//  @Override
//  protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double
//    leftInset) {
//    return 60 + rightInset + leftInset;
//  }
//
//  @Override
//  protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double
//    leftInset) {
//    return 60 + topInset + bottomInset;
//  }

  private void update(double width, double height) {
    compassStackPane.getChildren().clear();

    double radius = Math.min(height, width) / 2.0;
    double triangleHeight = radius * (3.0 / 5.0);
    double triangleWidth = triangleHeight / 3.0;

    PathElement northEastTrianglePath[] =
      {new MoveTo(0.0, 0.0), new VLineTo(triangleHeight), new HLineTo(triangleWidth), new ClosePath()};
    PathElement northWestTrianglePath[] =
      {new MoveTo(0.0, 0.0), new VLineTo(triangleHeight), new HLineTo(-triangleWidth), new ClosePath()};

    Path northEastTriangle = new Path(northEastTrianglePath);
    northEastTriangle.setStroke(null);
    northEastTriangle.setFill(Color.rgb(0x88, 0x00, 0x00));
    northEastTriangle.setTranslateY(-triangleHeight / 2.0);
    northEastTriangle.setTranslateX(triangleWidth / 2.0);

    Path southWestTriangle = new Path(northEastTrianglePath);
    southWestTriangle.setStroke(null);
    southWestTriangle.setFill(Color.rgb(0xA9, 0xA9, 0xA9));
    southWestTriangle.setTranslateY(triangleHeight / 2.0);
    southWestTriangle.setTranslateX(-triangleWidth / 2.0);
    southWestTriangle.setRotate(180.0);

    Path northWestTriangle = new Path(northWestTrianglePath);
    northWestTriangle.setStroke(null);
    northWestTriangle.setFill(Color.rgb(0xFF, 0x00, 0x00));
    northWestTriangle.setTranslateY(-triangleHeight / 2.0);
    northWestTriangle.setTranslateX(-triangleWidth / 2.0);

    Path southEastTriangle = new Path(northWestTrianglePath);
    southEastTriangle.setStroke(null);
    southEastTriangle.setFill(Color.rgb(0x80, 0x80, 0x80));
    southEastTriangle.setTranslateY(triangleHeight / 2.0);
    southEastTriangle.setTranslateX(triangleWidth / 2.0);
    southEastTriangle.setRotate(180.0);

    Circle pivot = new Circle();
    pivot.setRadius(triangleWidth / 3.0);
    pivot.setFill(Color.rgb(0xFF, 0xA5, 0x00));

    Circle circle = new Circle();
    circle.setRadius(radius);
    circle.setFill(Color.rgb(0xE1, 0xF1, 0xF5, 0.25));
    circle.setStroke(Color.rgb(0x80, 0x80, 0x80));
    circle.setStrokeWidth(0.1 * radius);

    compassStackPane.getChildren().addAll(circle, northEastTriangle, northWestTriangle,
      southEastTriangle, southWestTriangle, pivot);

    // fire action event if any of the compass elements are clicked
    compassStackPane.getChildren().forEach(c -> c.setOnMouseClicked(e -> getSkinnable().fireEvent(new ActionEvent())));
  }
}
