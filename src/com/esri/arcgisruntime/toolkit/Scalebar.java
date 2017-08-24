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

package com.esri.arcgisruntime.toolkit;

import com.esri.arcgisruntime.UnitSystem;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.toolkit.skins.AlternatingBarScalebarSkin;
import com.esri.arcgisruntime.toolkit.skins.BarScalebarSkin;
import com.esri.arcgisruntime.toolkit.skins.DualUnitScalebarSkin;
import com.esri.arcgisruntime.toolkit.skins.GraduatedLineScalebarSkin;
import com.esri.arcgisruntime.toolkit.skins.LineScaleBarSkin;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.HPos;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

import java.util.Objects;

public final class Scalebar extends Control {

  /**
   * Scalebar styles.
   */
  public enum SkinStyle {
    LINE,
    BAR,
    GRADUATED_LINE,
    ALTERNATING_BAR_LINE,
    DUAL_UNIT_LINE,
  }

  private static final double WIDTH = 100.0;

  private SkinStyle skinStyle;
  final private SimpleObjectProperty<HPos> alignmentProperty = new SimpleObjectProperty<>();
  final private SimpleObjectProperty<UnitSystem> unitSystemProperty = new SimpleObjectProperty<>();
  final private SimpleObjectProperty<MapView> mapViewProperty = new SimpleObjectProperty<>();

  public Scalebar(MapView mapView) {
    this(mapView, SkinStyle.LINE, HPos.CENTER);
  }

  public Scalebar(MapView mapView, SkinStyle style) {
    this(mapView, style, HPos.CENTER);
  }

  public Scalebar(MapView mapView, SkinStyle style, HPos alignment) {
    mapViewProperty.set(Objects.requireNonNull(mapView, "mapView cannot be null"));
    skinStyle = Objects.requireNonNull(style,"style cannot be null");
    alignmentProperty.set(Objects.requireNonNull(alignment, "alignment cannot be null"));

    unitSystemProperty.set(UnitSystem.METRIC);

    setPrefWidth(WIDTH);
    setPrefHeight(USE_COMPUTED_SIZE);
    setMaxHeight(USE_PREF_SIZE);
    setMaxWidth(USE_PREF_SIZE);
  }

  public SimpleObjectProperty<HPos> alignmentProperty() {
    return alignmentProperty;
  }

  public HPos getAlignment() {
    return alignmentProperty.get();
  }

  public void setAlignment(HPos hPos) {
    alignmentProperty.set(hPos);
  }

  public SkinStyle getSkinStyle() {
    return skinStyle;
  }

  public void setSkinStyle(SkinStyle style) {
    super.setSkin(createSkin(Objects.requireNonNull(style, "style cannot be null")));
  }

  public ReadOnlyObjectProperty<MapView> mapViewProperty() {
    return mapViewProperty;
  }

  public MapView getMapView() {
    return mapViewProperty.get();
  }

  public SimpleObjectProperty<UnitSystem> unitSystemProperty() {
    return unitSystemProperty;
  }

  public void setUnitSystem(UnitSystem units) {
    unitSystemProperty.set(Objects.requireNonNull(units, "units cannot be null"));
  }

  public UnitSystem getUnitSystem() {
    return unitSystemProperty.get();
  }

  @Override
  protected Skin<?> createDefaultSkin() {
    return createSkin(skinStyle);
  }

  private Skin<?> createSkin(SkinStyle style) {
    switch (style) {
      case LINE:
        return new LineScaleBarSkin(this);
      case BAR:
        return new BarScalebarSkin(this);
      case GRADUATED_LINE:
        return new GraduatedLineScalebarSkin(this);
      case ALTERNATING_BAR_LINE:
        return new AlternatingBarScalebarSkin(this);
      case DUAL_UNIT_LINE:
        return new DualUnitScalebarSkin(this);
    }
    return super.createDefaultSkin();
  }
}
