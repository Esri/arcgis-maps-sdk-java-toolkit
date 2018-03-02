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

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.InteractionListener;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.toolkit.OverviewMap;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.StackPane;

/**
 * Implements a skin for the {@link OverviewMap} control.
 */
public class OverviewMapSkin extends SkinBase<OverviewMap> {

  /**
   * Creates an instance of the skin.
   *
   * @param control the {@link OverviewMap} control this skin represents
   */
  public OverviewMapSkin(OverviewMap control) {
    super(control);

    // create a stack pane holding an map view
    MapView overviewMapView = new MapView();
    ArcGISMap map = new ArcGISMap(control.basemapProperty().get());
    overviewMapView.setMap(map);
    StackPane stackPane = new StackPane();
    stackPane.getChildren().add(overviewMapView);
    getChildren().add(stackPane);

    // add a listener for changes in the geo view's view point that will update the indicator graphic
    final Graphic indicatorGraphic = new Graphic();
    GeoView geoView = control.geoViewProperty().get();
    geoView.addViewpointChangedListener(v -> {
      if (geoView instanceof MapView) {
        indicatorGraphic.setGeometry(((MapView) geoView).getVisibleArea());
      } else {
        Viewpoint viewpoint = geoView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE);
        indicatorGraphic.setGeometry(viewpoint.getTargetGeometry());
      }
    });

    // add the indicator graphic to the map view
    indicatorGraphic.setSymbol(control.symbolProperty().get());
    GraphicsOverlay indicatorOverlay = new GraphicsOverlay();
    indicatorOverlay.getGraphics().add(indicatorGraphic);
    overviewMapView.getGraphicsOverlays().add(indicatorOverlay);

    // disable map view interaction
    overviewMapView.setInteractionListener(new InteractionListener() {});

    // hide attribution
    overviewMapView.setAttributionTextVisible(false);

    // listen for property changes
    control.basemapProperty().addListener((observable, oldValue, newValue) -> overviewMapView.getMap().setBasemap(newValue));
    control.symbolProperty().addListener((observable, oldValue, newValue) -> indicatorGraphic.setSymbol(newValue));
  }
}
