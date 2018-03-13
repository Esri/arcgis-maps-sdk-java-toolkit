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
import com.esri.arcgisruntime.symbology.FillSymbol;
import com.esri.arcgisruntime.symbology.MarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.Symbol;
import com.esri.arcgisruntime.toolkit.skins.OverviewMapSkin;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * An overview map control that indicates the viewpoint of another map or scene view.
 */
public class OverviewMap  extends Control {

  final private SimpleObjectProperty<GeoView> geoViewProperty = new SimpleObjectProperty<>();
  final private SimpleObjectProperty<Basemap> basemapProperty = new SimpleObjectProperty<>();
  final private SimpleObjectProperty<Symbol> symbolProperty = new SimpleObjectProperty<>();

  final static private FillSymbol sFillSymbol =
    new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0x7F000000, null);
  final static private MarkerSymbol sMarkerSymbol =
    new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CROSS, 0x7F000000, 20);

  /**
   * Creates an overview map for a geoview using default values for the basemap and indicator symbol.
   *
   * @param geoView the geo view to connect to this overview map
   * @throws NullPointerException if geoView is null
   */
  public OverviewMap(GeoView geoView) {
    this(geoView, Basemap.createTopographic(), geoView instanceof MapView ? sFillSymbol : sMarkerSymbol);
  }

  /**
   * Creates an overview map for a geo view using a default indicator symbol.
   *
   * @param geoView the geo view to connect to this overview map
   * @param basemap the basemap
   * @throws NullPointerException if geoView is null
   * @throws NullPointerException if basemap is null
   */
  public OverviewMap(GeoView geoView, Basemap basemap) {
    this(geoView, basemap, geoView instanceof MapView ? sFillSymbol : sMarkerSymbol);
  }

  /**
   * Creates an overview map for a geo view using a default basemap.
   *
   * @param geoView the geo view to connect to this overview map
   * @param symbol the symbol to use, for a mapview use a fill symbol and for a scene view use a marker symbol
   * @throws NullPointerException if geoView is null
   * @throws NullPointerException if symbol is null
   */
  public OverviewMap(GeoView geoView, Symbol symbol) {
    this(geoView, Basemap.createTopographic(), symbol);
  }

  /**
   * Creates an overview map for a geo view.
   *
   * @param geoView the geo view to connect to this overview map
   * @param basemap the basemap
   * @param symbol the symbol to use, for a mapview use a fill symbol and for a scene view use a marker symbol
   * @throws NullPointerException if geoView is null
   * @throws NullPointerException if basemap is null
   * @throws NullPointerException if symbol is null
   */
  public OverviewMap(GeoView geoView, Basemap basemap, Symbol symbol) {
    geoViewProperty.set(Objects.requireNonNull(geoView, "geoView cannot be null"));
    basemapProperty.set(Objects.requireNonNull(basemap));
    symbolProperty.set(Objects.requireNonNull(symbol));

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
   * Gets the geo view that this overview map is linked to.
   *
   * @return the geo view
   */
  public GeoView getGeoView() {
    return geoViewProperty.get();
  }

  /**
   * A readonly property containing the geo view linked to this overview map.
   *
   * @return the geo view property
   */
  public ReadOnlyObjectProperty<GeoView> geoViewProperty() {
    return geoViewProperty;
  }

  /**
   * Gets the basemap being used.
   *
   * @return the basemap
   */
  public Basemap getBasemap() {
    return basemapProperty.get();
  }

  /**
   * Sets the basemap to use.
   *
   * @param basemap the basemap to use
   */
  public void setBasemap(Basemap basemap) {
    basemapProperty.set(basemap);
  }

  /**
   * A property containing the basemap being used in this overview map.
   *
   * @return the basemap
   */
  public SimpleObjectProperty<Basemap> basemapProperty() {
    return basemapProperty;
  }

  /**
   * Gets the symbol being used to indicate the viewpoint.
   *
   * @return the symbol
   */
  public Symbol getSymbol() {
    return symbolProperty.get();
  }

  /**
   * Sets the symbol to use to indicate the viewpoint.
   *
   * @param symbol the symbol, for a mapview use a fill symbol and for a scene view use a marker symbol
   */
  public void setSymbol(Symbol symbol) {
    symbolProperty.set(symbol);
  }

  /**
   * A property containing the symbol being used in this overview map.
   *
   * @return the symbol property
   */
  public SimpleObjectProperty<Symbol> symbolProperty() {
    return symbolProperty;
  }
}
