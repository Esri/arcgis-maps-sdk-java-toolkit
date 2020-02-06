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
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ToggleGroup;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * View model for a control which shows feature template items for a feature layer.
 *
 * @since 100.6.0
 */
public final class FeatureTemplateGroup {

  private final SimpleObjectProperty<FeatureLayer> featureLayer;
  private final ReadOnlyListWrapper<FeatureTemplateItem> featureTemplateItems;
  private final SimpleObjectProperty<FeatureTemplateItem> selectedFeatureTemplateItem;
  private final IntegerProperty symbolHeight;
  private final IntegerProperty symbolWidth;
  private final ObjectProperty<ToggleGroup> toggleGroup;

  /**
   * Creates a new instance.
   *
   * @param featureLayer the feature layer
   * @throws NullPointerException if feature layer is null
   * @throws IllegalArgumentException if the feature table associated with the feature layer is not an Arc GIS feature
   * table
   * @since 100.6.0
   */
  public FeatureTemplateGroup(FeatureLayer featureLayer) {
    this.featureLayer = new SimpleObjectProperty<>(Objects.requireNonNull(featureLayer));
    this.featureTemplateItems = new ReadOnlyListWrapper<>(FXCollections.observableArrayList());
    this.selectedFeatureTemplateItem = new SimpleObjectProperty<>();
    this.symbolHeight = new SimpleIntegerProperty(50);
    this.symbolWidth = new SimpleIntegerProperty(50);
    this.toggleGroup = new SimpleObjectProperty<>(new ToggleGroup());

    featureLayer.loadAsync();
    featureLayer.addDoneLoadingListener(() -> {
      if (featureLayer.getLoadStatus() == LoadStatus.LOADED && featureLayer.getFeatureTable() instanceof ArcGISFeatureTable) {
        Stream.concat(
            ((ArcGISFeatureTable) featureLayer.getFeatureTable()).getFeatureTemplates().stream(),
            ((ArcGISFeatureTable) featureLayer.getFeatureTable()).getFeatureTypes().stream().flatMap(ft -> ft.getTemplates().stream())
        )
        .map(ft -> new FeatureTemplateItem(featureLayer, ft))
        .collect(Collectors.toCollection(() -> this.featureTemplateItems));
      }
    });

    this.featureLayer.addListener(((observable, oldValue, newValue) -> {
      this.featureTemplateItems.clear();
      if (newValue != null) {
        newValue.loadAsync();
        newValue.addDoneLoadingListener(() -> {
          if (newValue.getLoadStatus() == LoadStatus.LOADED && newValue.getFeatureTable() instanceof ArcGISFeatureTable) {
            Stream.concat(
                ((ArcGISFeatureTable) newValue.getFeatureTable()).getFeatureTemplates().stream(),
                ((ArcGISFeatureTable) newValue.getFeatureTable()).getFeatureTypes().stream().flatMap(ft -> ft.getTemplates().stream())
            )
            .map(featureTemplate -> new FeatureTemplateItem(newValue, featureTemplate))
            .collect(Collectors.toCollection(() -> this.featureTemplateItems));
          }
        });
      }
    }));
  }

  /**
   * A read only property containing the feature layer that the templates are being shown for.
   *
   * @return the property
   * @since 100.6.0
   */
  public ObjectProperty<FeatureLayer> featureLayerProperty() {
    return featureLayer;
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

  public void setFeatureLayer(FeatureLayer featureLayer) {
    this.featureLayer.set(featureLayer);
  }

  public ObservableList<FeatureTemplateItem> getFeatureTemplateItems() {
    return featureTemplateItems.get();
  }

  public ReadOnlyListWrapper<FeatureTemplateItem> featureTemplateItemsProperty() {
    return featureTemplateItems;
  }

  /**
   * A property which contains the selected {@link FeatureTemplate} or null if there is no selection.
   *
   * @return the property
   * @since 100.6.0
   */
  public SimpleObjectProperty<FeatureTemplateItem> selectedFeatureTemplateItemProperty() {
    return selectedFeatureTemplateItem;
  }

  /**
   * Gets the value of the {@link #selectedFeatureTemplateItemProperty()}.
   *
   * @return the selected template or null if there is no selection
   * @since 100.6.0
   */
  public FeatureTemplateItem getSelectedTemplateItem() {
    return selectedFeatureTemplateItem.get();
  }

  public void setSelectedFeatureTemplateItem(FeatureTemplateItem selectedFeatureTemplateItem) {
    this.selectedFeatureTemplateItem.set(selectedFeatureTemplateItem);
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
