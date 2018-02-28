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
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.InteractionListener;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.toolkit.OverviewMap;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.StackPane;

public class OverviewMapSkin extends SkinBase<OverviewMap> {

  private boolean invalid;
  private final StackPane stackPane = new StackPane();

  private final MapView mapView = new MapView();
  private final ArcGISMap map = new ArcGISMap(Basemap.createTopographic());

  private final Graphic indicatorGraphic = new Graphic();
  private final GraphicsOverlay indicatorOverlay = new GraphicsOverlay();

  /**
   * Creates an instance of the skin.
   *
   * @param control the {@link OverviewMap} control this skin represents
   */
  public OverviewMapSkin(OverviewMap control) {
    super(control);

    control.widthProperty().addListener(observable -> invalid = true);
    control.heightProperty().addListener(observable -> invalid = true);
    control.insetsProperty().addListener(observable -> invalid = true);

    GeoView geoView = control.geoViewPropertyProperty().get();
    geoView.addViewpointChangedListener(v -> {
      if (geoView instanceof MapView) {
        Viewpoint viewpoint = geoView.getCurrentViewpoint(Viewpoint.Type.BOUNDING_GEOMETRY);
        indicatorGraphic.setGeometry(viewpoint.getTargetGeometry());
      } else {
        Viewpoint viewpoint = geoView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE);
        indicatorGraphic.setGeometry(viewpoint.getTargetGeometry());
      }
    });

    mapView.setMap(map);
    stackPane.getChildren().add(mapView);
    getChildren().add(stackPane);

    // add indicator graphic
    if (geoView instanceof MapView) {
      indicatorGraphic.setSymbol(new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0x7F000000, null));
    } else {
      indicatorGraphic.setSymbol(new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CROSS, 0x7F000000, 20));
    }
    indicatorOverlay.getGraphics().add(indicatorGraphic);
    mapView.getGraphicsOverlays().add(indicatorOverlay);

    // disable map view interaction
    mapView.setInteractionListener(new InteractionListener() {});

    // hide attribution
    mapView.setAttributionTextVisible(false);
  }

  @Override
  protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
    if (invalid) {
      update(contentWidth, contentHeight);
      invalid = false;
    }
    layoutInArea(stackPane, contentX, contentY, contentWidth, contentHeight, -1, HPos.CENTER, VPos.CENTER);
  }

  private void update(double width, double height) {
  }
}
