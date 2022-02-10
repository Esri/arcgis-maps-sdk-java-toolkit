/*
 * Copyright 2018 Esri
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

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.InteractionListener;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.toolkit.OverviewMap;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.StackPane;

/**
 * Implements a skin for the {@link OverviewMap} control.
 *
 * @since 100.2.1
 */
public class OverviewMapSkin extends SkinBase<OverviewMap> {

  private static final double PREF_WIDTH = 200.0;
  private static final double PREF_HEIGHT = 132.0;

  private final GeoView controlGeoView;
  private final MapView overviewMapView;
  private final Graphic indicatorGraphic = new Graphic();
  private final SimpleDoubleProperty scaleFactorProperty = new SimpleDoubleProperty();

  /**
   * Creates an instance of the skin.
   *
   * @param control the {@link OverviewMap} control this skin represents
   * @since 100.2.1
   */
  public OverviewMapSkin(OverviewMap control) {
    super(control);

    // create a stack pane holding a map view
    overviewMapView = new MapView();
    ArcGISMap map = new ArcGISMap(control.basemapProperty().get());
    overviewMapView.setMap(map);
    StackPane stackPane = new StackPane();
    stackPane.getChildren().add(overviewMapView);
    getChildren().add(stackPane);

    // add the indicator graphic to the map view
    indicatorGraphic.setSymbol(control.symbolProperty().get());
    GraphicsOverlay indicatorOverlay = new GraphicsOverlay();
    indicatorOverlay.getGraphics().add(indicatorGraphic);
    overviewMapView.getGraphicsOverlays().add(indicatorOverlay);

    // disable map view interaction
    overviewMapView.setInteractionListener(new InteractionListener() {});

    // hide the attribution
    overviewMapView.setAttributionTextVisible(false);

    // add a listener for changes in the GeoView's viewpoint so we can update the overview
    controlGeoView = control.geoViewProperty().get();
    controlGeoView.addViewpointChangedListener(v -> update());

    // listen for changes to the scale factor so that we can update the overview
    scaleFactorProperty.bind(control.scaleFactorProperty());
    scaleFactorProperty.addListener(o -> update());

    // listen for property changes
    control.basemapProperty().addListener((observable, oldValue, newValue) -> overviewMapView.getMap().setBasemap(newValue));
    control.symbolProperty().addListener((observable, oldValue, newValue) -> indicatorGraphic.setSymbol(newValue));
  }

  @Override
  protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double
    leftInset) {
    return computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
  }

  @Override
  protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double
    leftInset) {
    return computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
  }

  @Override
  protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double
    leftInset) {
    return computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
  }

  @Override
  protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double
    leftInset) {
    return computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
  }

  @Override
  protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double
    leftInset) {
    return PREF_WIDTH;
  }

  @Override
  protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double
    leftInset) {
    return PREF_HEIGHT;
  }

  @Override
  public void dispose() {
   if (overviewMapView != null) {
     overviewMapView.dispose();
   }
  }

  /**
   * Updates the overview when the GeoView's viewpoint changes.
   *
   * @since 100.13.0
   */
  private void update() {
    var viewpoint = controlGeoView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE);
    if (viewpoint != null) {
      var scale = viewpoint.getTargetScale() * scaleFactorProperty.get();
      var center = (Point) viewpoint.getTargetGeometry();

      // keep overview centered on the view's center
      overviewMapView.setViewpoint(new Viewpoint(center, scale));

      // update the graphic that indicates the visible area/center
      if (controlGeoView instanceof MapView) {
        indicatorGraphic.setGeometry(((MapView) controlGeoView).getVisibleArea());
      } else {
        indicatorGraphic.setGeometry(center);
      }
    }
  }
}
