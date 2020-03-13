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

import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.toolkit.skins.FeatureTemplatePickerTilePaneSkin;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A control which shows the feature templates available from a list of feature layers. Templates are grouped by
 * feature layer. See skin for styling options.
 *
 * @see FeatureTemplatePickerTilePaneSkin
 * @since 100.7.0
 */
public final class FeatureTemplatePicker extends Control {

  private final ListProperty<FeatureLayer> featureLayers;
  private final ReadOnlyListWrapper<FeatureTemplateGroup> featureTemplateGroups;
  private final ObjectProperty<Orientation> orientation = new SimpleObjectProperty<>(Orientation.VERTICAL);
  private final ObjectProperty<FeatureTemplateItem> selectedFeatureTemplateItem = new SimpleObjectProperty<>();
  private final IntegerProperty symbolSize = new SimpleIntegerProperty(20);

  /**
   * Creates an instance backed by the given observable list. Only feature layers with an ArcGISFeatureTable will be
   * displayed.
   *
   * @param featureLayers observable list of feature layers
   * @since 100.7.0
   */
  public FeatureTemplatePicker(ObservableList<FeatureLayer> featureLayers) {
    this.featureLayers = new SimpleListProperty<>(Objects.requireNonNull(featureLayers));
    this.featureTemplateGroups = new ReadOnlyListWrapper<>(featureLayers.stream()
        .map(FeatureTemplateGroup::new)
        .collect(Collectors.toCollection(FXCollections::observableArrayList)));

    // update feature template groups when feature layers list changes
    this.featureLayers.addListener((ListChangeListener<FeatureLayer>) c -> {
      while (c.next()) {
        if (c.wasAdded()) {
          // create a feature template group control for each feature table
          List<FeatureTemplateGroup> featureTemplateGroups = c.getAddedSubList().stream()
              .map(FeatureTemplateGroup::new)
              .collect(Collectors.toList());
          // add the new feature template groups to this control
          this.featureTemplateGroups.addAll(c.getFrom(), featureTemplateGroups);
        } else if (c.wasRemoved()) {
          this.featureTemplateGroups.remove(c.getFrom(), c.getFrom() + c.getRemovedSize());
        }
      }
    });
  }

  /**
   * Creates an instance initialized with the given feature layers. Only feature layers with an ArcGISFeatureTable
   * will be shown.
   *
   * @param featureLayers list of feature layers
   * @since 100.7.0
   */
  public FeatureTemplatePicker(FeatureLayer... featureLayers) {
    this(FXCollections.observableArrayList(Objects.requireNonNull(featureLayers)));
  }

  /**
   * Creates an instance with an empty list of feature layers.
   *
   * @since 100.7.0
   */
  public FeatureTemplatePicker() {
    this(FXCollections.observableArrayList());
  }

  @Override
  protected Skin<?> createDefaultSkin() {
    return new FeatureTemplatePickerTilePaneSkin(this);
  }

  @Override
  public String getUserAgentStylesheet() {
    return this.getClass().getResource("skins/feature-template-picker.css").toExternalForm();
  }

  /**
   * Property containing the list of feature layers displayed. The order of display matches the list order.
   *
   * @return the property
   * @since 100.7.0
   */
  public ListProperty<FeatureLayer> featureLayersProperty() {
    return featureLayers;
  }

  /**
   * Gets the value of the {@link #featureLayersProperty()}.
   *
   * @return the list of feature layers
   * @since 100.7.0
   */
  public ObservableList<FeatureLayer> getFeatureLayers() {
    return this.featureLayers.get();
  }

  /**
   * Sets the value of the {@link #featureLayersProperty()}.
   *
   * @param featureLayers the list of feature layers
   * @since 100.7.0
   */
  public void setFeatureLayers(ObservableList<FeatureLayer> featureLayers) {
    this.featureLayers.set(featureLayers);
  }

  /**
   * Gets the list of feature template groups in the picker.
   *
   * @return feature template groups
   * @since 100.7.0
   */
  public ObservableList<FeatureTemplateGroup> getFeatureTemplateGroups() {
    return featureTemplateGroups.get();
  }

  /**
   * Read only list of feature template groups in the picker.
   *
   * @return read-only feature template groups list property
   * @since 100.7.0
   */
  public ReadOnlyListWrapper<FeatureTemplateGroup> featureTemplateGroupsProperty() {
    return featureTemplateGroups;
  }

  /**
   * Gets the orientation of this control. See skin for effect.
   *
   * @return orientation
   * @since 100.7.0
   */
  public Orientation getOrientation() {
    return orientation.get();
  }

  /**
   * Sets the orientation of the control. See skin for effect. Defaults to {@link Orientation#VERTICAL}.
   *
   * @param orientation orientation
   * @since 100.7.0
   */
  public void setOrientation(Orientation orientation) {
    this.orientation.set(orientation);
  }

  /**
   * Orientation of the control. See skin for effect. Defaults to {@link Orientation#VERTICAL}.
   *
   * @return orientation property
   * @since 100.7.0
   */
  public ObjectProperty<Orientation> orientationProperty() {
    return orientation;
  }

  /**
   * Property containing the selected template or null if no template is selected.
   *
   * @return the property
   * @since 100.7.0
   */
  public ObjectProperty<FeatureTemplateItem> selectedFeatureTemplateItemProperty() {
    return selectedFeatureTemplateItem;
  }

  /**
   * Gets the selected feature template item.
   *
   * @return the selected feature template item or null if there is no selection
   * @since 100.7.0
   */
  public FeatureTemplateItem getSelectedFeatureTemplateItem() {
    return selectedFeatureTemplateItem.get();
  }

  /**
   * Sets the selected feature template item. Clears the selection if the given item cannot be found.
   *
   * @param selectedFeatureTemplateItem feature template item to select or null to clear the selection
   * @since 100.7.0
   */
  public void setSelectedFeatureTemplateItem(FeatureTemplateItem selectedFeatureTemplateItem) {
    this.selectedFeatureTemplateItem.set(selectedFeatureTemplateItem);
  }

  /**
   * Get the symbol size (dp) used for the feature template symbol swatches.
   *
   * @return symbol size
   * @since 100.7.0
   */
  public int getSymbolSize() {
    return symbolSize.get();
  }

  /**
   * Sets the symbol size used for the feature template symbol swatches. Defaults to 20 (dp).
   *
   * @param symbolSize symbol size
   * @since 100.7.0
   */
  public void setSymbolSize(int symbolSize) {
    this.symbolSize.set(symbolSize);
  }

  /**
   * Symbol size used for the feature template symbol swatches. Defaults to 20 (dp).
   *
   * @return symbol size property
   * @since 100.7.0
   */
  public IntegerProperty symbolSizeProperty() {
    return symbolSize;
  }
}
