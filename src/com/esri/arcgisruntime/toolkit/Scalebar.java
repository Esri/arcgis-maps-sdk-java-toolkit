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

import com.esri.arcgisruntime.toolkit.skins.AlternatingBarScalebarSkin;
import com.esri.arcgisruntime.toolkit.skins.BarScalebarSkin;
import com.esri.arcgisruntime.toolkit.skins.DualUnitScalebarSkin;
import com.esri.arcgisruntime.toolkit.skins.GraduatedLineScalebarSkin;
import com.esri.arcgisruntime.toolkit.skins.LineScaleBarSkin;
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

  public enum Units {
    METRIC,
    IMPERIAL,
  }

  private SkinStyle skinStyle;
  private SimpleObjectProperty<HPos> alignmentProperty;

  public Scalebar() {
    this(SkinStyle.LINE, HPos.LEFT);
  }

  public Scalebar(SkinStyle style, HPos alignment) {
    skinStyle = Objects.requireNonNull(style,"style cannot be null");
    alignmentProperty.set(Objects.requireNonNull(alignment, "alignment cannot be null"));
  }

  public SimpleObjectProperty<HPos> alignmentProperty() {
    return alignmentProperty;
  }

  public HPos getAlignmentProperty() {
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
