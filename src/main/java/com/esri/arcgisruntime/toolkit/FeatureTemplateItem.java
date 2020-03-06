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

import com.esri.arcgisruntime.data.FeatureTemplate;
import com.esri.arcgisruntime.layers.FeatureLayer;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

/**
 * Model class for representing a feature template with the context of its feature layer.
 *
 * @since 100.7.0
 */
public final class FeatureTemplateItem {

  private final ReadOnlyObjectProperty<FeatureLayer> featureLayer;
  private final ReadOnlyObjectProperty<FeatureTemplate> featureTemplate;

  /**
   * Creates an instance for the given feature layer and feature template.
   *
   * @param featureLayer the feature layer
   * @param featureTemplate the feature template
   * @since 100.7.0
   */
  FeatureTemplateItem(FeatureLayer featureLayer, FeatureTemplate featureTemplate) {
    this.featureLayer = new ReadOnlyObjectWrapper<>(featureLayer);
    this.featureTemplate = new ReadOnlyObjectWrapper<>(featureTemplate);
  }

  /**
   * Gets the associated feature layer.
   *
   * @return feature layer
   * @since 100.7.0
   */
  public FeatureLayer getFeatureLayer() {
    return featureLayer.get();
  }

  /**
   * The associated feature layer.
   *
   * @return feature layer read-only property
   * @since 100.7.0
   */
  public ReadOnlyObjectProperty<FeatureLayer> featureLayerProperty() {
    return featureLayer;
  }

  /**
   * Gets the associated feature template.
   *
   * @return feature template
   * @since 100.7.0
   */
  public FeatureTemplate getFeatureTemplate() {
    return featureTemplate.get();
  }

  /**
   * The associated feature template.
   *
   * @return feature template read-only property.
   * @since 100.7.0
   */
  public ReadOnlyObjectProperty<FeatureTemplate> featureTemplateProperty() {
    return featureTemplate;
  }
}
