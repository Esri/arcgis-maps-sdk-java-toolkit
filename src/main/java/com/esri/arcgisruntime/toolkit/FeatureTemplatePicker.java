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
import java.util.List;
import java.util.Objects;

import com.esri.arcgisruntime.data.FeatureTemplate;
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

/**
 * A control which provides a view of the feature templates available in a list of feature layers and allows a template
 * to be selected. The templates can be displayed in a horizontal or vertical layout.
 *
 * @since 100.6.0
 */
public final class FeatureTemplatePicker extends Control {

  private final ObservableList<FeatureLayer> featureLayers = FXCollections.observableList(new ArrayList<>());
  private final SimpleListProperty<FeatureLayer> featureLayersProperty = new SimpleListProperty<>(featureLayers);
  private final SimpleObjectProperty<Template> selectedTemplateProperty = new SimpleObjectProperty<>();
  private final SimpleIntegerProperty symbolWidthProperty = new SimpleIntegerProperty(50);
  private final SimpleIntegerProperty symbolHeightProperty = new SimpleIntegerProperty(50);
  private final SimpleBooleanProperty showTemplateNamesProperty = new SimpleBooleanProperty(false);
  private final SimpleBooleanProperty showFeatureLayerNamesProperty = new SimpleBooleanProperty(true);
  private final SimpleBooleanProperty disableIfCannotAddFeatureLayersProperty = new SimpleBooleanProperty(true);
  private final SimpleObjectProperty<Orientation> orientationProperty = new SimpleObjectProperty<>(Orientation.VERTICAL) {
    @Override
    public void set(Orientation orientation) {
      super.set(Objects.requireNonNull(orientation, "Orientation cannot be null"));
    }
  };

  /**
   * Property containing the list of feature layers displayed. The order of display matches the list order.
   * @return the property
   */
  public ListProperty<FeatureLayer> featureLayersProperty() {
    return featureLayersProperty;
  }

  /**
   * Gets the value of the {@link #featureLayersProperty()}.
   *
   * @return the list of feature layers
   * @since 100.6.0
   */
  public List<FeatureLayer> getFeatureLayers() {
    return featureLayersProperty.get();
  }

  /**
   * Sets the value of the {@link #featureLayersProperty()}.
   *
   * @param featureLayers the list of feature layers
   * @since 100.6.0
   */
  public void setFeatureLayers(List<FeatureLayer> featureLayers) {
    featureLayersProperty.setAll(featureLayers);
  }

  /**
   * Property containing the selected template or null if no template is selected.
   *
   * @return the property
   * @since 100.6.0
   */
  public SimpleObjectProperty<Template> selectedTemplateProperty() {
    return selectedTemplateProperty;
  }

  /**
   * Gets the value of the {@link #selectedTemplateProperty()}.
   *
   * @return the selected template or null if there is no selection
   * @since 100.6.0
   */
  public Template getSelectedTemplate() {
    return selectedTemplateProperty.get();
  }

  /**
   * Property controlling the width of the template symbols. The width is specified in device independent pixels.
   *
   * @return the property
   * @since 100.6.0
   */
  public SimpleIntegerProperty symbolWidthProperty() {
    return symbolWidthProperty;
  }

  /**
   * Sets the value of the {@link #symbolWidthProperty()}.
   *
   * @param width the width
   * @since 100.6.0
   */
  public void setSymbolWidth(int width) {
    symbolWidthProperty.set(width);
  }

  /**
   * Gets the value of the {@link #symbolWidthProperty()}.
   *
   * @return the width
   * @since 100.6.0
   */
  public int getSymbolWidth() {
    return symbolWidthProperty.get();
  }

  /**
   * Property controlling the height of the template symbols. The height is specified in device independent pixels.
   *
   * @return the property
   * @since 100.6.0
   */
  public SimpleIntegerProperty symbolHeightProperty() {
    return symbolHeightProperty;
  }

  /**
   * Sets the value of the {@link #symbolHeightProperty()}.
   *
   * @param height the height
   * @since 100.6.0
   */
  public void setSymbolHeight(int height) {
    symbolHeightProperty.set(height);
  }

  /**
   * Gets the value of the {@link #symbolHeightProperty()}.
   *
   * @return the height
   * @since 100.6.0
   */
  public int getSymbolHeight() {
    return symbolHeightProperty.get();
  }

  /**
   * A property controlling if the template names should be shown. Set this to true to display the template names, false
   * otherwise.
   *
   * @return the property
   * @since 100.6.0
   */
  public SimpleBooleanProperty showTemplateNamesProperty() {
    return showTemplateNamesProperty;
  }

  /**
   * Sets the value of the {@link #showTemplateNamesProperty()}.
   *
   * @param show true to show template names, false otherwise
   * @since 100.6.0
   */
  public void setShowTemplateNames(boolean show) {
    showTemplateNamesProperty.set(show);
  }

  /**
   * Gets the value of the {@link #showTemplateNamesProperty()}.
   *
   * @return true if template names are shown, false otherwise
   * @since 100.6.0
   */
  public boolean isShowTemplateNames() {
    return showTemplateNamesProperty.get();
  }

  /**
   * Property controlling if the feature layer names are shown above that feature layer's templates. Set this to true to
   * display the feature layer names, false otherwise.
   *
   * @return the property
   * @since 100.6.0
   */
  public SimpleBooleanProperty showFeatureLayerNamesProperty() {
    return showFeatureLayerNamesProperty;
  }

  /**
   * Sets the value of {@link #showFeatureLayerNamesProperty()}.
   *
   * @param show true to show feature layer names, false otherwise
   * @since 100.6.0
   */
  public void setShowFeatureLayerNames(boolean show) {
    showFeatureLayerNamesProperty.set(show);
  }

  /**
   * Gets the value of the {@link #showFeatureLayerNamesProperty()}.
   *
   * @return true if feature layer names are shown, false other wise
   * @since 100.6.0
   */
  public boolean isShowFeatureLayerNames() {
    return showFeatureLayerNamesProperty.get();
  }

  /**
   * A property that controls if templates  should be disabled if their feature layer  doesn't allow adding new
   * features. Set this to true to disable the templates when adding new features is not supported by their feature
   * layer and false otherwise.
   *
   * @return the property
   * @since 100.6.0
   */
  public SimpleBooleanProperty disableIfCannotAddFeatureLayersProperty() {
    return disableIfCannotAddFeatureLayersProperty;
  }

  /**
   * Sets the value of the {@link #disableIfCannotAddFeatureLayersProperty()}.
   *
   * @param disable true to disable adding if the feature layer does not support it, false otherwise
   */
  public void setDisableIfCannotAddFeatureLayers(boolean disable) {
    disableIfCannotAddFeatureLayersProperty.set(disable);
  }

  /**
   * Gets the value of the {@link #disableIfCannotAddFeatureLayersProperty()} ()}.
   *
   * @return true if disabled, false otherwise
   * @since 100.6.0
   */
  public boolean isDisableIfCannotAddFeatureLayers() {
    return disableIfCannotAddFeatureLayersProperty.get();
  }

  /**
   * Property which controls the orientation of the control. Select {@link Orientation#HORIZONTAL} if the control is
   * wider than its height and {@link Orientation#VERTICAL} if it is taller than it is wide. By default the orientation
   * is {@code VERTICAL}.
   *
   * @return the property
   * @since 100.6.0
   */
  public SimpleObjectProperty<Orientation> orientationProperty() {
    return orientationProperty;
  }

  /**
   * Sets the value of the {@link #orientationProperty()}.
   *
   * @param orientation the orientation
   * @since 100.6.0
   */
  public void setOrientation(Orientation orientation) {
    orientationProperty.set(orientation);
  }

  /**
   * Gets the value of the {@link #orientationProperty()}.
   *
   * @return the orientatoin
   * @since 100.6.0
   */
  public Orientation getOrientation() {
    return orientationProperty.get();
  }

  /**
   * Clears any selection.
   *
   * @since 100.6.0
   */
  public void clearSelection() {
    selectedTemplateProperty.set(null);
  }

  @Override
  protected Skin<?> createDefaultSkin() {
    return new FeatureTemplatePickerSkin(this);
  }

  /**
   * A template which consists of a {@link FeatureTemplate} and the {@link FeatureLayer} to which the template is
   * associated.
   *
   * @since 100.6.0
   */
  public final static class Template {
    private final FeatureLayer featureLayer;
    private final FeatureTemplate featureTemplate;

    /**
     * Creates a new instance.
     *
     * @param featureLayer the feature layer
     * @param featureTemplate the feature template
     * @throws NullPointerException if featureLayer is null
     * @throws NullPointerException if featureTemplate is null
     * @since 100.6.0
     */
    public Template(FeatureLayer featureLayer, FeatureTemplate featureTemplate) {
      this.featureLayer = Objects.requireNonNull(featureLayer);
      this.featureTemplate = Objects.requireNonNull(featureTemplate);
    }

    /**
     * Gets the feature layer.
     *
     * @return the feature layer
     * @since 100.6.0
     */
    public FeatureLayer getFeatureLayer() {
      return featureLayer;
    }

    /**
     * Gets the feature template.
     *
     * @return the feature template
     * @since 100.6.0
     */
    public FeatureTemplate getFeatureTemplate() {
      return featureTemplate;
    }
  }
}
