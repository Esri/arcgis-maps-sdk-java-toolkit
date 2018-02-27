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
import com.esri.arcgisruntime.mapping.view.InteractionListener;
import com.esri.arcgisruntime.mapping.view.MapView;
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

    mapView.setMap(map);
    stackPane.getChildren().add(mapView);
    getChildren().add(stackPane);

    mapView.setInteractionListener(new InteractionListener() {
    });
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
