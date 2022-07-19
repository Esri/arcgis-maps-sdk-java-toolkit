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
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Model class representing a group of feature templates belonging to a specific feature layer.
 *
 * @since 100.7.0
 */
public final class FeatureTemplateGroup {

  private final ReadOnlyObjectWrapper<FeatureLayer> featureLayer;
  private final ReadOnlyListWrapper<FeatureTemplateItem> featureTemplateItems;

  /**
   * Creates a new instance based on the given feature layer. No feature template items will be created for the group
   * if the feature layer's feature table is not an instance of {@link ArcGISFeatureTable}.
   *
   * @param featureLayer the feature layer
   * @throws NullPointerException if feature layer is null
   * @since 100.7.0
   */
  FeatureTemplateGroup(FeatureLayer featureLayer) {
    this.featureLayer = new ReadOnlyObjectWrapper<>(Objects.requireNonNull(featureLayer));
    this.featureTemplateItems = new ReadOnlyListWrapper<>(FXCollections.observableArrayList());

    featureLayer.loadAsync();
    featureLayer.addDoneLoadingListener(() -> {
      if (featureLayer.getLoadStatus() == LoadStatus.LOADED &&
          featureLayer.getFeatureTable() instanceof ArcGISFeatureTable) {
        ArcGISFeatureTable arcGISFeatureTable = (ArcGISFeatureTable) featureLayer.getFeatureTable();
        Stream<FeatureTemplate> featureTemplateStream = arcGISFeatureTable.getFeatureTemplates().stream();
        Stream<FeatureTemplate> featureTemplateTypesTemplateStream = arcGISFeatureTable.getFeatureTypes().stream()
            .flatMap(ft -> ft.getTemplates().stream());
        featureTemplateItems.addAll(Stream.concat(featureTemplateStream, featureTemplateTypesTemplateStream)
            .map(ft -> new FeatureTemplateItem(featureLayer, ft))
            .collect(Collectors.toList()));
      }
    });
  }

  /**
   * Gets the associated feature layer.
   *
   * @return the feature layer
   * @since 100.7.0
   */
  public FeatureLayer getFeatureLayer() {
    return featureLayerProperty().get();
  }

  /**
   * The associated feature layer.
   *
   * @return feature layer read-only property
   * @since 100.7.0
   */
  public ReadOnlyObjectProperty<FeatureLayer> featureLayerProperty() {
    return featureLayer.getReadOnlyProperty();
  }

  /**
   * Gets the feature template items belonging to this group.
   *
   * @return read-only list of feature template items in the group
   * @since 100.7.0
   */
  public ObservableList<FeatureTemplateItem> getFeatureTemplateItems() {
    return featureTemplateItems.get();
  }

  /**
   * The feature template items belonging to this group.
   *
   * @return feature template items read-only list property
   * @since 100.7.0
   */
  public ReadOnlyListProperty<FeatureTemplateItem> featureTemplateItemsProperty() {
    return featureTemplateItems.getReadOnlyProperty();
  }
}
