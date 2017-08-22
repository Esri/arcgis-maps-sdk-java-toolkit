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
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

public abstract class ScalebarSkin extends SkinBase<Scalebar> {

  private boolean invalid = true;
  private final StackPane stackPane = new StackPane();

  private static final double LINE_WIDTH = 5.0;

  private LinearUnit baseUnit;

  private final ViewpointChangedListener viewpointChangedListener = v -> recalculate();

  private final ChangeListener<UnitSystem> unitsChangedListener = (observable, oldValue, newValue) -> updateBaseUnit(newValue);

  ScalebarSkin(Scalebar control) {
    super(control);

    control.widthProperty().addListener(this::invalidated);
    control.heightProperty().addListener(this::invalidated);
    control.mapViewProperty().get().addViewpointChangedListener(viewpointChangedListener);
    control.unitSystemProperty().addListener(unitsChangedListener);

    updateBaseUnit(control.getUnitSystem());

    getChildren().add(stackPane);
  }

  @Override
  public void dispose() {
    getSkinnable().widthProperty().removeListener(this::invalidated);
    getSkinnable().heightProperty().removeListener(this::invalidated);
    getSkinnable().mapViewProperty().get().removeViewpointChangedListener(viewpointChangedListener);
    getSkinnable().unitSystemProperty().removeListener(unitsChangedListener);
  }

  /**
   * Called during layout if the control's width or height have changed.
   *
   * @param width the width
   * @param height the height
   */
  protected abstract void update(double width, double height);

  /**
   * Called when the scale bar needs to be recalulated. This method will calculate the correct scalebar display size
   * and unit etc.
   */
  protected abstract void recalculate();

  /**
   * The maxmimum width that a scalebar can have. This could be the full control width or less if the salebar
   * has a label at the end.
   *
   * @return the maximum possible width
   */
  protected abstract double calculateMaximumScalebarWidth();

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

  protected double getLineWidth() {
    return LINE_WIDTH;
  }

  protected LinearUnit getBaseUnit() {
    return baseUnit;
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
    double centerX = mapView.getWidth() / 2.0;
    double centerY = mapView.getHeight() / 2.0;
    double halfWidth = width / 2.0;

    double distance = 0.0;

    Point point1 = mapView.screenToLocation(new Point2D(centerX - halfWidth, centerY));
    Point point2 = mapView.screenToLocation(new Point2D(centerX + halfWidth, centerY));

    if (point1 != null && point2 != null) {
      PolylineBuilder polylineBuilder = new PolylineBuilder(mapView.getSpatialReference());
      polylineBuilder.addPoint(point1);
      polylineBuilder.addPoint(point2);

      distance = GeometryEngine.lengthGeodetic(polylineBuilder.toGeometry(), unit, GeodeticCurveType.GEODESIC);
    }

    System.out.println(distance + unit.getAbbreviation());
    return distance;
  }

  private void invalidated(Observable observable) {
    invalid = true;
  }

  private void updateBaseUnit(UnitSystem unitSystem) {
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
