/*
 COPYRIGHT 1995-2018 ESRI

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
