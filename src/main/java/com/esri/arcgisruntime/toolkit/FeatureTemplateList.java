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

public final class FeatureTemplateList extends Control {

  private final SimpleObjectProperty<FeatureLayer> featureLayerProperty = new SimpleObjectProperty<>();

  private final SimpleBooleanProperty showLayerNameProperty = new SimpleBooleanProperty(true);
  private final SimpleBooleanProperty showTemplateNameProperty = new SimpleBooleanProperty(false);
  private final SimpleBooleanProperty disableIfCannotAddFeatureProperty = new SimpleBooleanProperty(false);

  private final SimpleIntegerProperty symbolWidthProperty = new SimpleIntegerProperty(50);
  private final SimpleIntegerProperty symbolHeightProperty = new SimpleIntegerProperty(50);

  private final SimpleObjectProperty<FeatureTemplate> selectedTemplateProperty = new SimpleObjectProperty<>();

  public FeatureTemplateList(FeatureLayer featureLayer) {
    featureLayerProperty.set(Objects.requireNonNull(featureLayer));
    if (!(featureLayer.getFeatureTable() instanceof ArcGISFeatureTable)) {
      throw new IllegalArgumentException("FeatureLayer's table must be an ArcGISFeatureTable");
    }
  }

  public ReadOnlyObjectProperty<FeatureLayer> featureLayerProperty() {
    return featureLayerProperty;
  }

  public FeatureLayer getFeatureLayer() {
    return featureLayerProperty().get();
  }

  public SimpleBooleanProperty showLayerNameProperty() {
    return showLayerNameProperty;
  }

  public void setShowLayerName(boolean showLayername) {
    showLayerNameProperty().set(showLayername);
  }

  public boolean isShowLayerName() {
    return showLayerNameProperty().get();
  }

  public SimpleBooleanProperty showTemplateNameProperty() {
    return showTemplateNameProperty;
  }

  public void setShowTemplateName(boolean showTemplateName) {
    showTemplateNameProperty().set(showTemplateName);
  }

  public boolean isShowTemplateName() {
    return showTemplateNameProperty().get();
  }

  public SimpleIntegerProperty symbolWidthProperty() {
    return symbolWidthProperty;
  }

  public void setSymbolWidth(int width) {
    symbolWidthProperty().set(width);
  }

  public int getSymbolWidth() {
    return symbolWidthProperty().get();
  }

  public SimpleIntegerProperty symbolHeightProperty() {
    return symbolHeightProperty;
  }

  public void setSymbolHeight(int height) {
    symbolHeightProperty().set(height);
  }

  public int getSymbolHeight() {
    return symbolHeightProperty().get();
  }

  public SimpleObjectProperty<FeatureTemplate> selectedTemplateProperty() {
    return selectedTemplateProperty;
  }

  public FeatureTemplate getSelectedTemplate() {
    return selectedTemplateProperty().get();
  }

  public SimpleBooleanProperty disableIfCannotAddFeaturesProperty() {
    return disableIfCannotAddFeatureProperty;
  }

  public void setDisableIfCannotAddFeatures(boolean disableCannotAdd) {
    disableIfCannotAddFeaturesProperty().set(disableCannotAdd);
  }

  public boolean isDisableIfCannotAdds() {
    return disableIfCannotAddFeaturesProperty().get();
  }

  public void clearSelection() {
    selectedTemplateProperty.set(null);
  }

  @Override
  protected Skin<?> createDefaultSkin() {
    return new FeatureTemplateListSkin(this);
  }
}
