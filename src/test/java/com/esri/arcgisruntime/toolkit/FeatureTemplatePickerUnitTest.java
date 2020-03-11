/*
 * Copyright 2020 Esri
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

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Automated integration tests for feature template picker.
 */
@DisplayName("feature template picker unit tests")
public class FeatureTemplatePickerUnitTest {

  private static final String WILDFIRE_RESPONSE_URL = "https://sampleserver6.arcgisonline" +
      ".com/arcgis/rest/services/Wildfire/FeatureServer/0";

  /**
   * Starts the JavaFX platform before all tests.
   */
  @BeforeAll
  private static void startPlatform() {
    Platform.startup(() -> {
    });
  }

  /**
   * Tests constructor with a null observable feature layers list.
   */
  @Test
  @DisplayName("constructor with null observable feature layers list")
  void nullObservableFeatureLayers() {
    assertThrows(NullPointerException.class, () -> new FeatureTemplatePicker((ObservableList<FeatureLayer>) null));
  }

  /**
   * Tests constructor with a null feature layer variable arg.
   */
  @Test
  @DisplayName("constructor with null feature layer")
  void nullFeatureLayer() {
    assertThrows(NullPointerException.class, () -> new FeatureTemplatePicker((FeatureLayer) null));
  }

  /**
   * Tests constructor with a feature layer array including null.
   */
  @Test
  @DisplayName("constructor with null in feature layer array")
  void nullFeatureLayers() {
    assertThrows(NullPointerException.class, () -> new FeatureTemplatePicker(new FeatureLayer[]{null}));
  }

  @Test
  @DisplayName("can get the feature layers")
  void getFeatureLayers() {
    FeatureLayer featureLayer = new FeatureLayer(new ServiceFeatureTable(WILDFIRE_RESPONSE_URL));
    FeatureTemplatePicker featureTemplatePicker = new FeatureTemplatePicker(featureLayer);
    assertEquals(featureLayer, featureTemplatePicker.getFeatureLayers().get(0));
  }

  @Test
  @DisplayName("layers backed by observable list")
  void observableFeatureLayersList() {
    ObservableList<FeatureLayer> featureLayers = FXCollections.observableArrayList();
    FeatureTemplatePicker featureTemplatePicker = new FeatureTemplatePicker(featureLayers);
    assertEquals(featureLayers, featureTemplatePicker.getFeatureLayers());
    assertEquals(0, featureTemplatePicker.getFeatureLayers().size());
    FeatureLayer featureLayer = new FeatureLayer(new ServiceFeatureTable(WILDFIRE_RESPONSE_URL));
    featureLayers.add(featureLayer);
    assertEquals(1, featureTemplatePicker.getFeatureLayers().size());
    assertEquals(featureLayer, featureTemplatePicker.getFeatureLayers().get(0));
  }

  @Test
  @DisplayName("add layers via observable list")
  void featureLayersObservableList() {
    FeatureTemplatePicker featureTemplatePicker = new FeatureTemplatePicker();
    FeatureLayer featureLayer = new FeatureLayer(new ServiceFeatureTable(WILDFIRE_RESPONSE_URL));
    featureTemplatePicker.getFeatureLayers().add(featureLayer);
    assertEquals(1, featureTemplatePicker.getFeatureLayers().size());
    assertEquals(featureLayer, featureTemplatePicker.getFeatureLayers().get(0));
  }

  @Test
  @DisplayName("bind feature layers to external property")
  void bindFeatureLayersProperty() {
    ListProperty<FeatureLayer> externalFeatureLayers = new SimpleListProperty<>(FXCollections.observableArrayList());
    FeatureTemplatePicker featureTemplatePicker = new FeatureTemplatePicker();
    featureTemplatePicker.featureLayersProperty().bind(externalFeatureLayers);
    FeatureLayer featureLayer = new FeatureLayer(new ServiceFeatureTable(WILDFIRE_RESPONSE_URL));
    externalFeatureLayers.add(featureLayer);
    assertEquals(1, featureTemplatePicker.getFeatureLayers().size());
    assertEquals(featureLayer, featureTemplatePicker.getFeatureLayers().get(0));
  }

  @Test
  @DisplayName("can bind over initialized list")
  void bindOverInitializeList() {
    FeatureLayer featureLayer = new FeatureLayer(new ServiceFeatureTable(WILDFIRE_RESPONSE_URL));
    ObservableList<FeatureLayer> featureLayers = FXCollections.observableArrayList(featureLayer);
    FeatureTemplatePicker featureTemplatePicker = new FeatureTemplatePicker(featureLayers);
    ListProperty<FeatureLayer> emptyListProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    featureTemplatePicker.featureLayersProperty().bind(emptyListProperty);
    assertEquals(0, featureTemplatePicker.getFeatureLayers().size());
  }

}
