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

import com.esri.arcgisruntime.UnitSystem;
import com.esri.arcgisruntime.geometry.GeodeticCurveType;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.LinearUnit;
import com.esri.arcgisruntime.geometry.LinearUnitId;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PolylineBuilder;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.ViewpointChangedListener;
import com.esri.arcgisruntime.toolkit.Scalebar;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public abstract class ScalebarSkin extends SkinBase<Scalebar> {

  protected static final double HEIGHT = 10.0;
  protected final static double SHADOW_OFFSET = 1.5;
  protected final static double STROKE_WIDTH = 3.0;

  protected final static Color LINE_COLOR = Color.WHITE;
  protected final static Color FILL_COLOR = Color.rgb(0xB7, 0xCB, 0xD3);
  protected final static Color ALTERNATE_FILL_COLOR = Color.BLACK;
  protected final static Color SHADOW_COLOR = Color.rgb(0x6E, 0x84, 0x8D);
  protected final static Color TEXT_COLOR = Color.BLACK;

  Rectangle rect = new Rectangle();

  private boolean invalid = true;
  private final StackPane stackPane = new StackPane();

  private UnitSystem unitSystem;
  private LinearUnit baseUnit;
  private HPos alignment = HPos.CENTER;

  private final ViewpointChangedListener viewpointChangedListener = v -> invalidated();

  private final ChangeListener<UnitSystem> unitsChangedListener = (observable, oldValue, newValue) -> {
    updateUnits(newValue);
    invalidated();
  };

  private final ChangeListener<HPos> alignmentChangedListener = (observable, oldValue, newValue) -> {
    alignment = newValue;
    invalidated();
  };

  ScalebarSkin(Scalebar control) {
    super(control);

    control.widthProperty().addListener(this::invalidated);
    control.heightProperty().addListener(this::invalidated);
    control.mapViewProperty().get().addViewpointChangedListener(viewpointChangedListener);
    control.mapViewProperty().get().widthProperty().addListener(this::invalidated);
    control.mapViewProperty().get().heightProperty().addListener(this::invalidated);
    control.unitSystemProperty().addListener(unitsChangedListener);
    control.alignmentProperty().addListener(alignmentChangedListener);

    updateUnits(control.getUnitSystem());
    alignment = control.getAlignment();

    rect.widthProperty().bind(control.widthProperty());
    rect.heightProperty().bind(control.heightProperty());
    rect.setFill(Color.rgb(0xFF, 0xFF, 0x00, 0.5));

    getChildren().add(rect);

    getChildren().add(stackPane);
  }

  @Override
  public void dispose() {
    getSkinnable().widthProperty().removeListener(this::invalidated);
    getSkinnable().heightProperty().removeListener(this::invalidated);
    getSkinnable().mapViewProperty().get().removeViewpointChangedListener(viewpointChangedListener);
    getSkinnable().mapViewProperty().get().widthProperty().removeListener(this::invalidated);
    getSkinnable().mapViewProperty().get().heightProperty().removeListener(this::invalidated);
    getSkinnable().unitSystemProperty().removeListener(unitsChangedListener);
    getSkinnable().alignmentProperty().removeListener(alignmentChangedListener);

    stackPane.getChildren().clear();
  }

  /**
   * Called during layout when the control needs to be redrawn e.g. the size has changed or the units have been changed.
   *
   * @param width the width
   * @param height the height
   */
  protected abstract void update(double width, double height);

  @Override
  protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
    if (invalid) {
      update(contentWidth, contentHeight);
      invalid = false;
    }

    getChildren().forEach(c -> layoutInArea(c, contentX, contentY, contentWidth, contentHeight, -1, HPos.CENTER, VPos.CENTER));
  }

  protected StackPane getStackPane() {
    return stackPane;
  }

  protected LinearUnit getBaseUnit() {
    return baseUnit;
  }

  protected UnitSystem getUnitSystem() {
    return unitSystem;
  }

  protected HPos getAlignment() {
    return alignment;
  }

  /**
   * Calculates the width of a region. Used when we need to know the width before layout.
   *
   * @param region the region
   * @return the width
   */
  protected double calculateRegionWidth(Region region) {
    Group root = new Group();
    Scene dummyScene = new Scene(root);
    root.getChildren().add(region);
    root.applyCss();
    root.layout();

    return region.getWidth();
  }

  /**
   * Calculates a distance on the map view based on the maximum possible scalebar width.
   *
   * @param width the width
   * @return the distance
   */
  protected double calculateDistance(MapView mapView, LinearUnit unit, double width) {
    double maxPlanarWidth = mapView.getUnitsPerDensityIndependentPixel() * width;

    double distance = 0.0;
    Point mapCenter = mapView.getVisibleArea().getExtent().getCenter();

    if (mapCenter.isEmpty()) {
      return 0.0;
    }

    Point point1 = new Point(mapCenter.getX() - (maxPlanarWidth / 2.0), mapCenter.getY());
    Point point2 = new Point(mapCenter.getX() + (maxPlanarWidth / 2.0), mapCenter.getY());


    if (point1 != null && point2 != null) {
      PolylineBuilder polylineBuilder = new PolylineBuilder(mapView.getSpatialReference());
      polylineBuilder.addPoint(point1);
      polylineBuilder.addPoint(mapCenter);
      polylineBuilder.addPoint(point2);

      distance = GeometryEngine.lengthGeodetic(polylineBuilder.toGeometry(), unit, GeodeticCurveType.GEODESIC);
    }

    return distance;
  }

  /**
   * Calculates the X translation required to move a scalbar to the correct alighment - left or right. No translation is
   * required for center alignment.
   *
   * @param width the width of the area containing the scalebar
   * @param actualWidth the actual width of the scalebar
   * @return the X translation required
   */
  protected double calculateAlignmentTranslationX(double width, double actualWidth) {
    double translate = 0.0;
    switch (getAlignment()) {
      case LEFT:
        translate = -((width - actualWidth - STROKE_WIDTH) / 2.0);
        break;
      case RIGHT:
        translate = (width - actualWidth - STROKE_WIDTH - SHADOW_OFFSET) / 2.0;
        break;
    }
    return translate;
  }

  private void invalidated(Observable observable) {
    invalidated();
  }

  private void invalidated() {
    invalid = true;
    getSkinnable().requestLayout();
  }

  private void updateUnits(UnitSystem unitSystem) {
    this.unitSystem = unitSystem;
    switch (unitSystem) {
      case METRIC:
        baseUnit = new LinearUnit(LinearUnitId.METERS);
        break;
      case IMPERIAL:
        baseUnit = new LinearUnit(LinearUnitId.FEET);
        break;
    }
  }
}
