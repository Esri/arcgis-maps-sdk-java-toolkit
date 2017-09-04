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
import javafx.scene.shape.Rectangle;

public final class AlternatingBarScalebarSkin extends ScalebarSkin {

  private static final double HEIGHT = 12.0;
  private static final double INNER_HEIGHT = 2 * (HEIGHT / 3.0);

  private final VBox vBox = new VBox();
  private final Pane labelPane = new Pane();
  private final StackPane barStackPane = new StackPane();
  private final Rectangle outerBar = new Rectangle();
  private final Path dividerPath = new Path();
  private final Pane segmentPane = new Pane();

  public AlternatingBarScalebarSkin(Scalebar scalebar) {
    super(scalebar);

    // use a vbox to arrange the bar above the labels
    vBox.setAlignment(Pos.CENTER);
    vBox.getChildren().addAll(barStackPane, labelPane);
    StackPane.setAlignment(vBox, Pos.CENTER);

    // outline of the bar
    outerBar.setFill(Color.rgb(0xFF, 0xFF, 0xFF));
    outerBar.setHeight(HEIGHT);
    outerBar.setEffect(new DropShadow(1.0, 1.5, 1.5, Color.rgb(0x6E, 0x84, 0x8D)));
    outerBar.setArcWidth(5);
    outerBar.setArcHeight(5);

    dividerPath.setStroke(Color.WHITE);
    dividerPath.setStrokeWidth(2.0);
    dividerPath.setTranslateY(INNER_HEIGHT / 4.0);

    barStackPane.getChildren().addAll(outerBar, segmentPane);

    getStackPane().getChildren().add(vBox);
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

    // clear out all the labels, segments and dividers
    labelPane.getChildren().clear();
    labelPane.setMaxWidth(displayWidth);

    segmentPane.getChildren().clear();
    segmentPane.setMaxWidth(displayWidth);

    dividerPath.getElements().clear();

    // update the bar size
    outerBar.setWidth(displayWidth);

    Label label;
    Rectangle segmentInner;

    // first dividers at the start of the bar
    dividerPath.getElements().add(new MoveTo(1.0, 0.0));
    dividerPath.getElements().add(new LineTo(1.0, INNER_HEIGHT));

    for (int i = 0; i < bestNumberOfSegments; ++i) {
      label = new Label(ScalebarUtil.labelString(i * segmentDistance));

      // first label is aligned with its left to the edge of the bar while the intermediate
      // labels are centered on the dividers
      if (i > 0) {
        label.setTranslateX((i * segmentWidth) - (calculateRegionWidth(label) / 2.0));
      }
      labelPane.getChildren().add(label);

      segmentInner = new Rectangle();
      segmentInner.setHeight(INNER_HEIGHT);
      segmentInner.setWidth(segmentWidth);
      segmentInner.setTranslateX(i * segmentWidth);
      segmentInner.setTranslateY(INNER_HEIGHT / 4.0);
      if (i % 2 != 0) {
        segmentInner.setFill(Color.BLACK);
      } else {
        segmentInner.setFill(Color.rgb(0xB7, 0xCB, 0xD3));
      }

      segmentPane.getChildren().add(segmentInner);

      if (i > 0) {
        dividerPath.getElements().add(new MoveTo(i * segmentWidth, 0.0));
        dividerPath.getElements().add(new LineTo(i * segmentWidth, INNER_HEIGHT));
      }
    }

    // last divider at the end of the bar
    dividerPath.getElements().add(new MoveTo(displayWidth - 1.0, 0.0));
    dividerPath.getElements().add(new LineTo(displayWidth - 1.0, INNER_HEIGHT));

    segmentPane.getChildren().add(dividerPath);

    // the last label is aligned so its end is at the end of the line so it is done outside the loop
    label = new Label(ScalebarUtil.labelString(displayDistance));
    // translate it into the correct position
    label.setTranslateX((bestNumberOfSegments * segmentWidth) - calculateRegionWidth(label));
    // then add the units on so the end of the number aligns with the end of the bar and the unit is of the end
    label.setText(ScalebarUtil.labelString(displayDistance) + displayUnits.getAbbreviation());
    labelPane.getChildren().add(label);

    // move the line and labels into their final position - slightly off center due to the units
    barStackPane.setTranslateX(-calculateRegionWidth(new Label(displayUnits.getAbbreviation())) / 2.0);
    labelPane.setTranslateX(-calculateRegionWidth(new Label(displayUnits.getAbbreviation())) / 2.0);

    // adjust for left/right/center alignment
    getStackPane().setTranslateX(calculateAlignmentTranslationX(width, displayWidth + calculateRegionWidth(new Label(displayUnits.getAbbreviation()))));

    // set invisible if distance is zero
    getStackPane().setVisible(displayDistance > 0);
  }
}
