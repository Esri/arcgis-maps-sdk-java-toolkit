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

import java.util.Objects;

import com.esri.arcgisruntime.data.ArcGISFeatureTable;
import com.esri.arcgisruntime.data.FeatureTemplate;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.toolkit.skins.FeatureTemplateListSkin;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.ToggleGroup;

/**
 * A control which displays all of the feature templates for a feature layer and allows selecting one.
 *
 * @since 100.6.0
 */
public final class FeatureTemplateList extends Control {

  private final SimpleObjectProperty<FeatureLayer> featureLayerProperty = new SimpleObjectProperty<>();

  private final SimpleBooleanProperty showLayerNameProperty = new SimpleBooleanProperty(true);
  private final SimpleBooleanProperty showTemplateNameProperty = new SimpleBooleanProperty(false);
  private final SimpleBooleanProperty disableIfCannotAddFeatureProperty = new SimpleBooleanProperty(false);

  private final SimpleIntegerProperty symbolWidthProperty = new SimpleIntegerProperty(50);
  private final SimpleIntegerProperty symbolHeightProperty = new SimpleIntegerProperty(50);

  private final SimpleObjectProperty<FeatureTemplate> selectedTemplateProperty = new SimpleObjectProperty<>();

  private final ToggleGroup toggleGroup;

  /**
   * Creates a new instance.
   *
   * @param featureLayer the feature layer
   * @throws NullPointerException if feature layer is null
   * @throws IllegalArgumentException if the feature table associated with the feature layer is not an Arc GIS feature
   * table
   * @since 100.6.0
   */
  public FeatureTemplateList(FeatureLayer featureLayer, ToggleGroup toggleGroup) {
    featureLayerProperty.set(Objects.requireNonNull(featureLayer));
    this.toggleGroup = toggleGroup;
    if (!(featureLayer.getFeatureTable() instanceof ArcGISFeatureTable)) {
      throw new IllegalArgumentException("FeatureLayer's table must be an ArcGISFeatureTable");
    }
  }

  /**
   * A read only property containing the feature layer that the templates are being shown for.
   *
   * @return the property
   * @since 100.6.0
   */
  public ReadOnlyObjectProperty<FeatureLayer> featureLayerProperty() {
    return featureLayerProperty;
  }

  /**
   * Gets the value of the {@link #featureLayerProperty()}.
   *
   * @return the feature layer
   * @since 100.6.0
   */
  public FeatureLayer getFeatureLayer() {
    return featureLayerProperty().get();
  }

  /**
   * A property controlling if the feature layer name should be displayed. Set this to true to display the layer name,
   * false otherwise.
   *
   * @return the property
   * @since 100.6.0
   */
  public SimpleBooleanProperty showLayerNameProperty() {
    return showLayerNameProperty;
  }

  /**
   * Sets the value of the {@link #showLayerNameProperty()}.
   *
   * @param showLayername true to show the layer name, false otherwise
   * @since 100.6.0
   */
  public void setShowLayerName(boolean showLayername) {
    showLayerNameProperty().set(showLayername);
  }

  /**
   * Gets the value of the {@link #showLayerNameProperty()}.
   *
   * @return true if the layer name is shown, false otherwise
   * @since 100.6.0
   */
  public boolean isShowLayerName() {
    return showLayerNameProperty().get();
  }

  /**
   * A property controlling if the template names should be shown. Set this to true to display the template names, false
   * otherwise.
   *
   * @return the property
   * @since 100.6.0
   */
  public SimpleBooleanProperty showTemplateNameProperty() {
    return showTemplateNameProperty;
  }

  /**
   * Sets the value of the {@link #showTemplateNameProperty()}.
   *
   * @param showTemplateName true to show template names, false otherwise
   * @since 100.6.0
   */
  public void setShowTemplateName(boolean showTemplateName) {
    showTemplateNameProperty().set(showTemplateName);
  }

  /**
   * Gets the value of the {@link #showTemplateNameProperty()}.
   *
   * @return true if template names are shown, false otherwise
   * @since 100.6.0
   */
  public boolean isShowTemplateName() {
    return showTemplateNameProperty().get();
  }

  /**
   * Gets a property which controls the width of the template symbols. The width is specified in device independent
   * pixels.
   *
   * @return the property
   * @since 100.6.0
   */
  public SimpleIntegerProperty symbolWidthProperty() {
    return symbolWidthProperty;
  }

  /**
   * Sets the value of {@link #symbolWidthProperty()}.
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
   * A property which controls the height of the template symbols. The height is specified in device independent pixels.
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

  /**
   * A property which contains the selected {@link FeatureTemplate} or null if there is no selection.
   *
   * @return the property
   * @since 100.6.0
   */
  public SimpleObjectProperty<FeatureTemplate> selectedTemplateProperty() {
    return selectedTemplateProperty;
  }

  /**
   * Gets the value of the {@link #selectedTemplateProperty()}.
   *
   * @return the selected template or null if there is no selection
   * @since 100.6.0
   */
  public FeatureTemplate getSelectedTemplate() {
    return selectedTemplateProperty().get();
  }

  /**
   * A property that controls if the control's contents should be disabled if the feature layer used to construct the
   * control doesn't allow adding new features. Set this to true to disable the templates when adding new features is
   * not supported by the feature layer and false otherwise.
   *
   * @return the property
   * @since 100.6.0
   */
  public SimpleBooleanProperty disableIfCannotAddFeaturesProperty() {
    return disableIfCannotAddFeatureProperty;
  }

  /**
   * Sets the value of the {@link #disableIfCannotAddFeaturesProperty()}.
   *
   * @param disableCannotAdd true to disable adding if the feature layer does not support it, false otherwise
   */
  public void setDisableIfCannotAddFeatures(boolean disableCannotAdd) {
    disableIfCannotAddFeaturesProperty().set(disableCannotAdd);
  }

  /**
   * Gets the value of the {@link #disableIfCannotAddFeaturesProperty()}.
   *
   * @return true if disabled, false otherwise
   * @since 100.6.0
   */
  public boolean isDisableIfCannotAdds() {
    return disableIfCannotAddFeaturesProperty().get();
  }

  /**
   * Clears any selected template.
   *
   * @since 100.6.0
   */
  public void clearSelection() {
    selectedTemplateProperty.set(null);
  }

  @Override
  protected Skin<?> createDefaultSkin() {
    return new FeatureTemplateListSkin(this, toggleGroup);
  }
}
