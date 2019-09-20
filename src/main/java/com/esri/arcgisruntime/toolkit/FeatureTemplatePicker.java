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

import java.util.ArrayList;

import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.toolkit.skins.FeatureTemplatePickerSkin;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class FeatureTemplatePicker extends Control {

  private final ObservableList<FeatureLayer> featureLayers = FXCollections.observableList(new ArrayList<>());
  private final SimpleListProperty<FeatureLayer> featureLayerListProperty = new SimpleListProperty<>(featureLayers);
  private final SimpleObjectProperty<TemplatePicker.Template> selectedTemplateProperty = new SimpleObjectProperty<>();
  private final SimpleIntegerProperty symbolWidthProperty = new SimpleIntegerProperty(50);
  private final SimpleIntegerProperty symbolHeightProperty = new SimpleIntegerProperty(50);
  private final SimpleBooleanProperty showTemplateNamesProperty = new SimpleBooleanProperty(false);
  private final SimpleBooleanProperty showFeatureLayerNamesProperty = new SimpleBooleanProperty(true);
  private final SimpleBooleanProperty disableCannotAddFeatureLayersProperty = new SimpleBooleanProperty(true);
  private final SimpleObjectProperty<Orientation> orientationProperty = new SimpleObjectProperty<>(Orientation.VERTICAL);

  public FeatureTemplatePicker() {
  }

  public ListProperty<FeatureLayer> featureLayerListProperty() {
    return featureLayerListProperty;
  }

  public SimpleObjectProperty<TemplatePicker.Template> selectedTemplateProperty() {
    return selectedTemplateProperty;
  }

  public SimpleIntegerProperty symbolWidthProperty() {
    return symbolWidthProperty;
  }

  public SimpleIntegerProperty symbolHeightProperty() {
    return symbolHeightProperty;
  }

  public SimpleBooleanProperty showTemplateNamesProperty() {
    return showTemplateNamesProperty;
  }

  public SimpleBooleanProperty showFeatureLayerNamesProperty() {
    return showFeatureLayerNamesProperty;
  }

  public SimpleBooleanProperty disableCannotAddFeatureLayersProperty() {
    return disableCannotAddFeatureLayersProperty;
  }

  public SimpleObjectProperty<Orientation> orientationProperty() {
    return orientationProperty;
  }

  public void clearSelection() {
    selectedTemplateProperty.set(null);
  }

  @Override
  protected Skin<?> createDefaultSkin() {
    return new FeatureTemplatePickerSkin(this);
  }
}
