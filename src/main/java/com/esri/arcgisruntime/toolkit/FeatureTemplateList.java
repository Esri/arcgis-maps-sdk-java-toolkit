/*
 * Copyright 2019 Esri
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

import com.esri.arcgisruntime.data.ArcGISFeatureTable;
import com.esri.arcgisruntime.data.FeatureTemplate;
import com.esri.arcgisruntime.toolkit.skins.FeatureTemplateListSkin;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public final class FeatureTemplateList extends Control {

  private final SimpleObjectProperty<ArcGISFeatureTable> featureTableProperty = new SimpleObjectProperty<>();

  private final SimpleBooleanProperty showLayerNameProperty = new SimpleBooleanProperty(true);
  private final SimpleBooleanProperty showTemplateNameProperty = new SimpleBooleanProperty(false);
  private final SimpleBooleanProperty disableCannotAddFeatureLayersProperty = new SimpleBooleanProperty(false);

  private final SimpleIntegerProperty symbolWidthProperty = new SimpleIntegerProperty(50);
  private final SimpleIntegerProperty symbolHeightProperty = new SimpleIntegerProperty(50);

  private final SimpleObjectProperty<FeatureTemplate> selectedTemplateProperty = new SimpleObjectProperty<>();

  public FeatureTemplateList(ArcGISFeatureTable featureTable) {
    featureTableProperty.set(featureTable);
  }

  public ReadOnlyObjectProperty<ArcGISFeatureTable> featureTableProperty() {
    return featureTableProperty;
  }

  public SimpleBooleanProperty showLayerNameProperty() {
    return showLayerNameProperty;
  }

  public SimpleBooleanProperty showTemplateNameProperty() {
    return showTemplateNameProperty;
  }

  public SimpleIntegerProperty symbolWidthProperty() {
    return symbolWidthProperty;
  }

  public SimpleIntegerProperty symbolHeightProperty() {
    return symbolHeightProperty;
  }

  public SimpleObjectProperty<FeatureTemplate> selectedTemplateProperty() {
    return selectedTemplateProperty;
  }

  public SimpleBooleanProperty disableCannotAddFeatureLayersProperty() {
    return disableCannotAddFeatureLayersProperty;
  }

  public void clearSelection() {
    selectedTemplateProperty.set(null);
  }

  @Override
  protected Skin<?> createDefaultSkin() {
    return new FeatureTemplateListSkin(this);
  }
}
