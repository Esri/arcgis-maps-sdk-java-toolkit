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

package com.esri.arcgisruntime.toolkit;

import java.util.Objects;

import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.ColorUtil;
import com.esri.arcgisruntime.symbology.FillSymbol;
import com.esri.arcgisruntime.symbology.MarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.Symbol;
import com.esri.arcgisruntime.toolkit.skins.OverviewMapSkin;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;

/**
 * An overview map control that indicates the viewpoint of another map or scene view.
 *
 * @since 100.2.1
 */
public class OverviewMap extends Control {

  final static private FillSymbol FILL_SYMBOL =
    new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, ColorUtil.colorToArgb(Color.TRANSPARENT),
      new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, ColorUtil.colorToArgb(Color.RED), 1.0f));
  final static private MarkerSymbol MARKER_SYMBOL =
    new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CROSS, ColorUtil.colorToArgb(Color.RED), 20);

  final private SimpleObjectProperty<GeoView> geoViewProperty = new SimpleObjectProperty<>();
  final private SimpleObjectProperty<Basemap> basemapProperty = new SimpleObjectProperty<>();
  final private SimpleObjectProperty<Symbol> symbolProperty = new SimpleObjectProperty<>();
  final private SimpleDoubleProperty scaleFactorProperty = new SimpleDoubleProperty(25.0);

  /**
   * Creates an overview map for a GeoView using default values for the basemap and indicator symbol.
   *
   * @param geoView the GeoView to connect to this overview map
   * @throws NullPointerException if geoView is null
   * @since 100.2.1
   */
  public OverviewMap(GeoView geoView) {
    this(geoView, Basemap.createTopographic(), geoView instanceof MapView ? FILL_SYMBOL : MARKER_SYMBOL);
  }

  /**
   * Creates an overview map for a GeoView using a default indicator symbol.
   *
   * @param geoView the GeoView to connect to this overview map
   * @param basemap the basemap
   * @throws NullPointerException if geoView is null
   * @throws NullPointerException if basemap is null
   * @since 100.2.1
   */
  public OverviewMap(GeoView geoView, Basemap basemap) {
    this(geoView, basemap, geoView instanceof MapView ? FILL_SYMBOL : MARKER_SYMBOL);
  }

  /**
   * Creates an overview map for a GeoView using a default basemap.
   *
   * @param geoView the GeoView to connect to this overview map
   * @param symbol the symbol to use, for a map view use a fill symbol and for a scene view use a marker symbol
   * @throws NullPointerException if geoView is null
   * @throws NullPointerException if symbol is null
   * @since 100.2.1
   */
  public OverviewMap(GeoView geoView, Symbol symbol) {
    this(geoView, Basemap.createTopographic(), symbol);
  }

  /**
   * Creates an overview map for a GeoView.
   *
   * @param geoView the GeoView to connect to this overview map
   * @param basemap the basemap
   * @param symbol the symbol to use, for a map view use a fill symbol and for a scene view use a marker symbol
   * @throws NullPointerException if geoView is null
   * @throws NullPointerException if basemap is null
   * @throws NullPointerException if symbol is null
   * @since 100.2.1
   */
  public OverviewMap(GeoView geoView, Basemap basemap, Symbol symbol) {
    geoViewProperty.set(Objects.requireNonNull(geoView, "geoView cannot be null"));
    basemapProperty.set(Objects.requireNonNull(basemap, "basemap cannot be null"));
    symbolProperty.set(Objects.requireNonNull(symbol, "symbol cannot be null"));

    setMaxHeight(USE_PREF_SIZE);
    setMaxWidth(USE_PREF_SIZE);
    setMinHeight(USE_PREF_SIZE);
    setMinWidth(USE_PREF_SIZE);
  }

  @Override
  protected Skin<?> createDefaultSkin() {
    return new OverviewMapSkin(this);
  }

  /**
   * Gets the GeoView that this overview map is linked to.
   *
   * @return the GeoView
   * @since 100.2.1
   */
  public GeoView getGeoView() {
    return geoViewProperty.get();
  }

  /**
   * A readonly property containing the GeoView linked to this overview map.
   *
   * @return the GeoView property
   * @since 100.2.1
   */
  public ReadOnlyObjectProperty<GeoView> geoViewProperty() {
    return geoViewProperty;
  }

  /**
   * Gets the basemap being used.
   *
   * @return the basemap
   * @since 100.2.1
   */
  public Basemap getBasemap() {
    return basemapProperty.get();
  }

  /**
   * Sets the basemap to use.
   *
   * @param basemap the basemap to use
   * @since 100.2.1
   */
  public void setBasemap(Basemap basemap) {
    basemapProperty.set(basemap);
  }

  /**
   * A property containing the basemap being used in this overview map.
   *
   * @return the basemap
   * @since 100.2.1
   */
  public SimpleObjectProperty<Basemap> basemapProperty() {
    return basemapProperty;
  }

  /**
   * Gets the symbol being used to indicate the viewpoint.
   *
   * @return the symbol
   * @since 100.2.1
   */
  public Symbol getSymbol() {
    return symbolProperty.get();
  }

  /**
   * Sets the symbol to use to indicate the viewpoint.
   *
   * @param symbol the symbol, for a mapview use a fill symbol and for a scene view use a marker symbol
   * @since 100.2.1
   */
  public void setSymbol(Symbol symbol) {
    symbolProperty.set(symbol);
  }

  /**
   * A property containing the symbol being used in this overview map.
   *
   * @return the symbol property
   * @since 100.2.1
   */
  public SimpleObjectProperty<Symbol> symbolProperty() {
    return symbolProperty;
  }

  /**
   * A property containing The amount to scale the OverviewMap compared to the geoView. The default is 25.0.
   *
   * @return the scale property
   * @since 100.13.0
   */
  public SimpleDoubleProperty scaleFactorProperty() {
    return scaleFactorProperty;
  }

  /**
   * Sets the value used to scale the OverviewMap compared to the geoView. The default is 25.0.
   *
   * @since 100.13.0
   */
  public void setScaleFactor(double scaleFactor) {
    scaleFactorProperty.set(scaleFactor);
  }

  /**
   * Gets the value used to scale the OverviewMap compared to the geoView. The default is 25.0.
   *
   * @return the scale factor
   * @since 100.13.0
   */
  public double getScaleFactor() {
    return scaleFactorProperty.get();
  }
}
