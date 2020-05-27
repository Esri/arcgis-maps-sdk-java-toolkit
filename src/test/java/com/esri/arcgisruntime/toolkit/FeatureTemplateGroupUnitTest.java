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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Feature template group unit tests.
 */
@DisplayName("feature template group unit tests")
public class FeatureTemplateGroupUnitTest {

  private static final String WILDFIRE_RESPONSE_URL = "https://sampleserver6.arcgisonline" +
      ".com/arcgis/rest/services/Wildfire/FeatureServer/0";

  private static final int LATCH_TIMEOUT_SEC = 10;

  /**
   * Starts the JavaFX platform before all tests.
   */
  @BeforeAll
  private static void startPlatform() {
    try {
      Platform.startup(() -> {});
    } catch (Exception ex) {
      // toolkit already initialized
    }
  }

  /**
   * Tests constructor with a null feature layer.
   */
  @Test
  @DisplayName("null feature layer throws")
  void nullFeatureLayer() {
    assertThrows(NullPointerException.class, () -> new FeatureTemplateGroup(null));
  }

  /**
   * Tests the feature layer getter.
   */
  @Test
  @DisplayName("can get feature layer via getter")
  void getFeatureLayer() {
    FeatureLayer featureLayer = new FeatureLayer(new ServiceFeatureTable(WILDFIRE_RESPONSE_URL));
    FeatureTemplateGroup featureTemplateGroup = new FeatureTemplateGroup(featureLayer);
    assertEquals(featureLayer, featureTemplateGroup.getFeatureLayer());
  }

  /**
   * Tests the feature layer property.
   */
  @Test
  @DisplayName("can get feature layer via property")
  void featureLayerProperty() {
    FeatureLayer featureLayer = new FeatureLayer(new ServiceFeatureTable(WILDFIRE_RESPONSE_URL));
    FeatureTemplateGroup featureTemplateGroup = new FeatureTemplateGroup(featureLayer);
    assertEquals(featureLayer, featureTemplateGroup.featureLayerProperty().get());
  }

  /**
   * Tests that feature template items load asynchronously after construction.
   *
   * @throws InterruptedException exception
   */
  @Test
  @DisplayName("feature template items load")
  void loadFeatureTemplateItems() throws InterruptedException {
    FeatureLayer featureLayer = new FeatureLayer(new ServiceFeatureTable(WILDFIRE_RESPONSE_URL));
    FeatureTemplateGroup featureTemplateGroup = new FeatureTemplateGroup(featureLayer);
    assertEquals(0, featureTemplateGroup.getFeatureTemplateItems().size());
    CountDownLatch countDownLatch = new CountDownLatch(1);
    featureLayer.addDoneLoadingListener(countDownLatch::countDown);
    countDownLatch.await(LATCH_TIMEOUT_SEC, TimeUnit.SECONDS);
    assertFalse(featureTemplateGroup.getFeatureTemplateItems().isEmpty());
  }
}
