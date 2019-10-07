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

import com.esri.arcgisruntime.UnitSystem;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.toolkit.skins.AlternatingBarScalebarSkin;
import com.esri.arcgisruntime.toolkit.skins.BarScalebarSkin;
import com.esri.arcgisruntime.toolkit.skins.DualUnitScalebarSkin;
import com.esri.arcgisruntime.toolkit.skins.GraduatedLineScalebarSkin;
import com.esri.arcgisruntime.toolkit.skins.LineScaleBarSkin;
import javafx.beans.NamedArg;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.HPos;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

import java.util.Objects;

/**
 * Scalebar control that shows an accurate distance that can be used to visually gauge distances on a map view. The
 * measurement system used is controlled by {@link #unitSystemProperty}. The units used will be appropriate to the
 * distance being shown e.g. km for long distances and m for shorter distances. The scalebar can be visualized with
 * different skins e.g. an alternating bar or a graduated line.
 *
 * @since 100.2.1
 */
public final class Scalebar extends Control {

  /**
   * Scalebar styles - different visualizations of the distance.
   *
   * @since 100.2.1
   */
  public enum SkinStyle {
    /**
     * A line with end ticks and a single central distance label.
     *
     * @since 100.2.1
     */
    LINE,
    /**
     * A solid bar with a single central distance label.
     *
     * @since 100.2.1
     */
    BAR,
    /**
     * A line with ticks and distance labels.
     *
     * @since 100.2.1
     */
    GRADUATED_LINE,
    /**
     * A bar with alternating color segments with a distance label at each.
     *
     * @since 100.2.1
     */
    ALTERNATING_BAR,
    /**
     * A line with both Metric system and Imperial system distances shown. The upper measurement reflects the
     * {@link #unitSystemProperty}.
     *
     * @since 100.2.1
     */
    DUAL_UNIT_LINE,
  }

  // default width
  private static final double WIDTH = 100.0;

  // the style of the scalebar
  private SkinStyle skinStyle;

  // property to hold the alignment
  final private SimpleObjectProperty<HPos> alignmentProperty = new SimpleObjectProperty<>();

  // property to hold the measurement system
  final private SimpleObjectProperty<UnitSystem> unitSystemProperty = new SimpleObjectProperty<>();

  // property to hold the map view this scale bar is measuring
  final private SimpleObjectProperty<MapView> mapViewProperty = new SimpleObjectProperty<>();

  /**
   * Creates a scalebar with a {@link SkinStyle#ALTERNATING_BAR} style and an alignment of {@link HPos#CENTER}. By
   * default the width of the control will be 1/4 the map view width.
   *
   * @param mapView the map view this scale bar is representing
   * @throws NullPointerException if map view is null
   * @since 100.2.1
   */
  public Scalebar(@NamedArg("mapView") MapView mapView) {
    this(mapView, SkinStyle.ALTERNATING_BAR, HPos.CENTER);
  }

  /**
   * Creates a scalebar with a specified style and an alignment of {@link HPos#CENTER}. By default the width of the
   * control will be 1/4 the map view width.
   *
   * @param mapView the map view this scale bar is representing
   * @param style the skin style to use
   * @throws NullPointerException if map view is null
   * @throws NullPointerException if style is null
   * @since 100.2.1
   */
  public Scalebar(MapView mapView, SkinStyle style) {
    this(mapView, style, HPos.CENTER);
  }

  /**
   * Creates a scalebar with a specified style and alignment. By default the width of the control will be 1/4 the map
   * view width.
   *
   * @param mapView the map view this scale bar is representing
   * @param style the skin style to use
   * @param  alignment the alignment to use
   * @throws NullPointerException if map view is null
   * @throws NullPointerException if style is null
   * @throws NullPointerException if alignment is null
   * @since 100.2.1
   */
  public Scalebar(MapView mapView, SkinStyle style, HPos alignment) {
    mapViewProperty.set(Objects.requireNonNull(mapView, "mapView cannot be null"));
    skinStyle = Objects.requireNonNull(style,"style cannot be null");
    alignmentProperty.set(Objects.requireNonNull(alignment, "alignment cannot be null"));

    unitSystemProperty.set(UnitSystem.METRIC);

    setMaxHeight(USE_PREF_SIZE);
    setMaxWidth(USE_PREF_SIZE);
    setMinHeight(USE_PREF_SIZE);
    setMinWidth(USE_PREF_SIZE);
  }

  /**
   * Returns a property that holds the scalebar's alignment. The alignment controls how the scalebar will grow e.g. if
   * alignment is {@link HPos#CENTER} the scalebar will grow in both directions whereas if the alignment is
   * {@link HPos#LEFT} the scalebar will only grow towards the right.
   *
   * @return the property
   * @since 100.2.1
   */
  public SimpleObjectProperty<HPos> alignmentProperty() {
    return alignmentProperty;
  }

  /**
   * Returns the current alignment.
   *
   * @return the alignment
   * @see #alignmentProperty()
   * @since 100.2.1
   */
  public HPos getAlignment() {
    return alignmentProperty.get();
  }

  /**
   * Sets the alignment.
   *
   * @param hPos the alignment
   * @see #alignmentProperty()
   * @throws NullPointerException if hPos is null
   * @since 100.2.1
   */
  public void setAlignment(HPos hPos) {
    Objects.requireNonNull(hPos, "hPos cannot be null");
    alignmentProperty.set(hPos);
  }

  /**
   * Returns the current skin style.
   *
   * @return the style
   * @see SkinStyle
   * @since 100.2.1
   */
  public SkinStyle getSkinStyle() {
    return skinStyle;
  }

  /**
   * Sets the skin style for this scalebar.
   *
   * @param style the style
   * @see SkinStyle
   * @throws NullPointerException if style is null
   * @since 100.2.1
   */
  public void setSkinStyle(SkinStyle style) {
    skinStyle = Objects.requireNonNull(style, "style cannot be null");
    super.setSkin(createSkin(skinStyle));
  }

  /**
   * Returns a readonly property containing the map view that this scalebar is measuring.
   *
   * @return the property
   * @since 100.2.1
   */
  public ReadOnlyObjectProperty<MapView> mapViewProperty() {
    return mapViewProperty;
  }

  /**
   * Returns the map view that this scalebar is measuring.
   *
   * @return the map view
   * @since 100.2.1
   */
  public MapView getMapView() {
    return mapViewProperty.get();
  }

  /**
   * Returns a property containing the measurement system being used by the scalebar.
   *
   * @return the property
   * @see UnitSystem
   * @since 100.2.1
   */
  public SimpleObjectProperty<UnitSystem> unitSystemProperty() {
    return unitSystemProperty;
  }

  /**
   * Sets the measurement system for the scalebar to use.
   *
   * @param units the measurement system
   * @see UnitSystem
   * @since 100.2.1
   */
  public void setUnitSystem(UnitSystem units) {
    unitSystemProperty.set(Objects.requireNonNull(units, "units cannot be null"));
  }

  /**
   * Returns the measurement system being used by the scalebar.
   *
   * @return the measurement system
   * @see UnitSystem
   * @since 100.2.1
   */
  public UnitSystem getUnitSystem() {
    return unitSystemProperty.get();
  }

  @Override
  protected Skin<?> createDefaultSkin() {
    return createSkin(skinStyle);
  }

  /**
   * Creates a skin based upon the {@link SkinStyle}.
   *
   * @param style the style
   * @return a new skin
   * @since 100.2.1
   */
  private Skin<?> createSkin(SkinStyle style) {
    switch (style) {
      case LINE:
        return new LineScaleBarSkin(this);
      case BAR:
        return new BarScalebarSkin(this);
      case GRADUATED_LINE:
        return new GraduatedLineScalebarSkin(this);
      case ALTERNATING_BAR:
        return new AlternatingBarScalebarSkin(this);
      case DUAL_UNIT_LINE:
        return new DualUnitScalebarSkin(this);
    }
    return super.createDefaultSkin();
  }
}
