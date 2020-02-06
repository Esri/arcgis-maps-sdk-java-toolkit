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
import com.esri.arcgisruntime.mapping.LayerList;
import com.esri.arcgisruntime.toolkit.skins.FeatureTemplatePickerFlowPaneSkin;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.ToggleGroup;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A control which provides a view of the feature templates available in a list of feature layers and allows a template
 * to be selected. The templates can be displayed in a horizontal or vertical layout.
 *
 * @since 100.6.0
 */
public final class FeatureTemplatePicker extends Control {

  private final ListProperty<FeatureLayer> featureLayers;
  private final ReadOnlyListWrapper<FeatureTemplateGroup> featureTemplateGroups;
  private final ObjectProperty<FeatureTemplateItem> selectedFeatureTemplateItem;
  private final ObjectProperty<Orientation> orientation;
  private final IntegerProperty symbolWidth;
  private final IntegerProperty symbolHeight;
  private final ObjectProperty<ToggleGroup> toggleGroup;

  public FeatureTemplatePicker(ObservableList<FeatureLayer> featureLayers) {
    this.featureLayers = new SimpleListProperty<>(Objects.requireNonNull(featureLayers));
    this.featureTemplateGroups = new ReadOnlyListWrapper<>(featureLayers.stream()
        .map(FeatureTemplateGroup::new)
        .collect(Collectors.toCollection(FXCollections::observableArrayList)));
    this.selectedFeatureTemplateItem = new SimpleObjectProperty<>();
    this.orientation = new SimpleObjectProperty<>(Orientation.VERTICAL);
    this.symbolWidth = new SimpleIntegerProperty(20);
    this.symbolHeight = new SimpleIntegerProperty(20);
    this.toggleGroup = new SimpleObjectProperty<>(new ToggleGroup());

    this.featureLayers.addListener((ListChangeListener<FeatureLayer>) c -> {
      while (c.next()) {
        if (c.wasAdded()) {
          // create a feature template group control for each feature table
          List<FeatureTemplateGroup> featureTemplateGroups = c.getAddedSubList().stream().map(FeatureTemplateGroup::new).collect(Collectors.toList());
          // bind template group properties to picker's properties
          featureTemplateGroups.forEach(featureTemplateGroup -> {
            featureTemplateGroup.selectedFeatureTemplateItemProperty().bind(this.selectedFeatureTemplateItem);
          });
          // add the new feature template groups to this control
          this.featureTemplateGroups.addAll(c.getFrom(), featureTemplateGroups);
        } else if (c.wasRemoved()) {
          this.featureTemplateGroups.remove(c.getFrom(), c.getFrom() + c.getRemovedSize());
        }
      }
    });
  }

  public FeatureTemplatePicker(FeatureLayer... featureLayers) {
    this(FXCollections.observableArrayList(featureLayers));
  }

  public FeatureTemplatePicker() {
    this(FXCollections.observableArrayList());
  }

  /**
   * Property containing the list of feature layers displayed. The order of display matches the list order.
   * @return the property
   */
  public ListProperty<FeatureLayer> featureLayersProperty() {
    return featureLayers;
  }

  /**
   * Gets the value of the {@link #featureLayersProperty()}.
   *
   * @return the list of feature layers
   * @since 100.6.0
   */
  public ObservableList<FeatureLayer> getFeatureLayers() {
    return this.featureLayers.get();
  }

  /**
   * Sets the value of the {@link #featureLayersProperty()}.
   *
   * @param featureLayers the list of feature layers
   * @since 100.6.0
   */
  public void setFeatureLayers(ObservableList<FeatureLayer> featureLayers) {
    this.featureLayers.set(featureLayers);
  }

  public ObservableList<FeatureTemplateGroup> getFeatureTemplateGroups() {
    return featureTemplateGroups.get();
  }

  public ReadOnlyListWrapper<FeatureTemplateGroup> featureTemplateGroupsProperty() {
    return featureTemplateGroups;
  }

  public void setFeatureTemplateGroups(ObservableList<FeatureTemplateGroup> featureTemplateGroups) {
    this.featureTemplateGroups.set(featureTemplateGroups);
  }

  public Orientation getOrientation() {
    return orientation.get();
  }

  public ObjectProperty<Orientation> orientationProperty() {
    return orientation;
  }

  public void setOrientation(Orientation orientation) {
    this.orientation.set(orientation);
  }

  /**
   * Property containing the selected template or null if no template is selected.
   *
   * @return the property
   * @since 100.6.0
   */
  public ObjectProperty<FeatureTemplateItem> selectedFeatureTemplateItemProperty() {
    return selectedFeatureTemplateItem;
  }

  /**
   * Gets the value of the {@link #selectedFeatureTemplateItemProperty()}.
   *
   * @return the selected template or null if there is no selection
   * @since 100.6.0
   */
  public FeatureTemplateItem getSelectedFeatureTemplateItem() {
    return selectedFeatureTemplateItem.get();
  }

  public void setSelectedFeatureTemplateItem(FeatureTemplateItem selectedFeatureTemplateItem) {
    this.selectedFeatureTemplateItem.set(selectedFeatureTemplateItem);
  }

  public int getSymbolWidth() {
    return symbolWidth.get();
  }

  public IntegerProperty symbolWidthProperty() {
    return symbolWidth;
  }

  public void setSymbolWidth(int symbolWidth) {
    this.symbolWidth.set(symbolWidth);
  }

  public int getSymbolHeight() {
    return symbolHeight.get();
  }

  public IntegerProperty symbolHeightProperty() {
    return symbolHeight;
  }

  public void setSymbolHeight(int symbolHeight) {
    this.symbolHeight.set(symbolHeight);
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

  @Override
  protected Skin<?> createDefaultSkin() {
    return new FeatureTemplatePickerFlowPaneSkin(this);
  }

  @Override
  public String getUserAgentStylesheet() {
    return this.getClass().getResource("skins/template-cell.css").toExternalForm();
  }
}
