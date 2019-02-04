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
import java.util.Objects;

import com.esri.arcgisruntime.data.FeatureTemplate;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.toolkit.skins.TemplatePickerSkin;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * Template picker control which displays {@link FeatureTemplate}s that can be selected.
 *
 * @since 100.5
 */
public final class TemplatePicker extends Control {

  private final ObservableList<FeatureLayer> featureLayers = FXCollections.observableList(new ArrayList<>());
  private final SimpleListProperty<FeatureLayer> featureLayerListProperty = new SimpleListProperty<>(featureLayers);
  private final SimpleObjectProperty<Template> selectedTemplateProperty = new SimpleObjectProperty<>();
  private final SimpleIntegerProperty symbolSizeProperty = new SimpleIntegerProperty(50);
  private final SimpleBooleanProperty showTemplateNamesProperty = new SimpleBooleanProperty(false);
  private final SimpleBooleanProperty showFeatureLayerNamesProperty = new SimpleBooleanProperty(true);
  private final SimpleBooleanProperty showSeparatorsProperty = new SimpleBooleanProperty(true);
  private final SimpleBooleanProperty disableCannotAddFeaturelayersProperty = new SimpleBooleanProperty(true);

  public TemplatePicker() {
    setMaxWidth(USE_PREF_SIZE);
    setMaxWidth(USE_PREF_SIZE);
    setMaxHeight(USE_COMPUTED_SIZE);
    setMaxHeight(USE_COMPUTED_SIZE);
  }

  @Override
  protected Skin<?> createDefaultSkin() {
    return new TemplatePickerSkin(this);
  }

  /**
   * Specifies the list of {@link FeatureLayer}s that the template picker will display feature templates
   * for.
   *
   * @return the list property
   * @since 100.5
   */
  public SimpleListProperty<FeatureLayer> featureLayerListProperty() {
    return featureLayerListProperty;
  }

  /**
   * The selected template if any.
   *
   * @return the selected template property
   * @since 100.5
   */
  public SimpleObjectProperty<Template> selectedTemplateProperty() {
    return selectedTemplateProperty;
  }

  /**
   * Specifies the symbol size for the template's graphic.
   * @return the property
   * @since 100.5
   */
  public SimpleIntegerProperty symbolSizeProperty() {
    return symbolSizeProperty;
  }

  /**
   * Specifies if the names of the templates should be shown. The default value is false.
   * @return the property
   * @since 100.5
   */
  public SimpleBooleanProperty showTemplateNamesProperty() {
    return showTemplateNamesProperty;
  }

  /**
   * Specifies if feature laayer names should be shown. The default value is true.
   * @return the property
   * @since 100.5
   */
  public SimpleBooleanProperty showFeatureLayerNamesProperty() {
    return showFeatureLayerNamesProperty;
  }

  /**
   * Specifies if separators should be shown between feature layers. The default value is true.
   * @return the property
   * @since 100.5
   */
  public SimpleBooleanProperty showSeparatorsProperty() {
    return showSeparatorsProperty;
  }

  public SimpleBooleanProperty disableCannotAddFeaturelayersProperty() {
    return disableCannotAddFeaturelayersProperty;
  }

  /**
   * A template which consists of a {@link FeatureTemplate} and the {@link FeatureLayer} to which the template is
   * associated.
   *
   * @since 100.5
   */
  public final static class Template {
    private FeatureLayer featureLayer;
    private FeatureTemplate featureTemplate;

    /**
     * Creates a new instance.
     *
     * @param featureLayer the feature layer
     * @param featureTemplate the feature template
     * @since 100.5
     * @throws NullPointerException if featureLayer is null
     * @throws NullPointerException if featuretemplate is null
     */
    public Template(FeatureLayer featureLayer, FeatureTemplate featureTemplate) {
      this.featureLayer = Objects.requireNonNull(featureLayer);
      this.featureTemplate = Objects.requireNonNull(featureTemplate);
    }

    /**
     * Gets the feature layer.
     * @return the feature layer
     * @since 100.5
     */
    public FeatureLayer getFeatureLayer() {
      return featureLayer;
    }

    /**
     * Gets the feature template.
     * @return the feature template
     * @since 100.5
     */
    public FeatureTemplate getFeatureTemplate() {
      return featureTemplate;
    }
  }
}
