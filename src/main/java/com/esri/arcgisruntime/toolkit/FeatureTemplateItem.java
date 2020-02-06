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
import javafx.beans.property.*;
import javafx.scene.control.ToggleGroup;

/**
 * A control which shows a cell containing a visual representation of a single feature template and allows marking the
 * cell as selected.
 *
 * @since 100.6.0
 */
public final class FeatureTemplateItem {

  private final ReadOnlyObjectProperty<FeatureLayer> featureLayer;
  private final ReadOnlyObjectProperty<FeatureTemplate> featureTemplate;
  private final IntegerProperty symbolHeight;
  private final IntegerProperty symbolWidth;
  private final ObjectProperty<ToggleGroup> toggleGroup;

  /**
   * Creates an instance.
   *
   *
   * @param featureLayer feature layer
   * @param featureTemplate the feature template
   * @since 100.6.0
   */
  FeatureTemplateItem(FeatureLayer featureLayer, FeatureTemplate featureTemplate) {
    this.featureLayer = new ReadOnlyObjectWrapper<>(featureLayer);
    this.featureTemplate = new ReadOnlyObjectWrapper<>(featureTemplate);
    this.symbolHeight = new SimpleIntegerProperty(50);
    this.symbolWidth = new SimpleIntegerProperty(50);
    this.toggleGroup = new SimpleObjectProperty<>(new ToggleGroup());
  }

  public FeatureLayer getFeatureLayer() {
    return featureLayer.get();
  }

  public ReadOnlyObjectProperty<FeatureLayer> featureLayerProperty() {
    return featureLayer;
  }

  public FeatureTemplate getFeatureTemplate() {
    return featureTemplate.get();
  }

  public ReadOnlyObjectProperty<FeatureTemplate> featureTemplateProperty() {
    return featureTemplate;
  }

  /**
   * A property controlling the width of the symbol used in the cell.
   *
   * @return the symbol width property
   * @since 100.6.0
   */
  public IntegerProperty symbolWidthProperty() {
    return symbolWidth;
  }

  /**
   * Sets the value of the {@link #symbolWidthProperty()}.
   *
   * @param width the width
   * @since 100.6.0
   */
  public void setSymbolWidth(int width) {
    symbolWidthProperty().set(width);
  }

  /**
   * Gets the value of the {@link #symbolWidthProperty()}.
   *
   * @return the width
   * @since 100.6.0
   */
  public int getSymbolWidth() {
    return symbolWidthProperty().get();
  }

  /**
   * A property controlling the height of the symbol used in the cell.
   *
   * @return the height property
   * @since 100.6.0
   */
  public IntegerProperty symbolHeightProperty() {
    return symbolHeight;
  }

  /**
   * Sets the value of the {@link #symbolHeightProperty()}.
   *
   * @param height the height
   * @since 100.6.0
   */
  public void setSymbolHeight(int height) {
    symbolHeightProperty().set(height);
  }

  /**
   * Gets the value of the {@link #symbolHeightProperty()}.
   *
   * @return the height
   * @since 100.6.0
   */
  public int getSymbolHeight() {
    return symbolHeightProperty().get();
  }

  public ToggleGroup getToggleGroup() {
    return toggleGroup.get();
  }

  public ObjectProperty<ToggleGroup> toggleGroupProperty() {
    return toggleGroup;
  }

  public void setToggleGroup(ToggleGroup toggleGroup) {
    this.toggleGroup.set(toggleGroup);
  }
}
