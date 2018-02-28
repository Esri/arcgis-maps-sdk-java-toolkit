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

import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.toolkit.skins.OverviewMapSkin;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class OverviewMap  extends Control {

  private static final double WIDTH = 200.0;
  private static final double HEIGHT = 132.0;

  final private SimpleObjectProperty<GeoView> geoViewProperty = new SimpleObjectProperty<>();

  public OverviewMap(GeoView geoView) {
    geoViewProperty.set(Objects.requireNonNull(geoView, "geoView cannot be null"));

    setPrefHeight(HEIGHT);
    setPrefWidth(WIDTH);
    setMaxHeight(USE_PREF_SIZE);
    setMaxWidth(USE_PREF_SIZE);
    setMinHeight(USE_PREF_SIZE);
    setMinWidth(USE_PREF_SIZE);
  }

  @Override
  protected Skin<?> createDefaultSkin() {
    return new OverviewMapSkin(this);
  }

  public ReadOnlyObjectProperty<GeoView> geoViewPropertyProperty() {
    return geoViewProperty;
  }
}
