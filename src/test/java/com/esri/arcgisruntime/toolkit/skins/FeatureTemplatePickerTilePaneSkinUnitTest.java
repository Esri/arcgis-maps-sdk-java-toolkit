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

package com.esri.arcgisruntime.toolkit.skins;

import com.esri.arcgisruntime.toolkit.FeatureTemplatePicker;
import com.esri.arcgisruntime.toolkit.util.PlatformUtils;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Feature template picker tile pane skin unit tests.
 */
@DisplayName("feature template picker tile pane skin unit tests")
public class FeatureTemplatePickerTilePaneSkinUnitTest {

  /**
   * Starts the JavaFX platform before all tests.
   */
  @BeforeAll
  static void startPlatform() {
    if (!PlatformUtils.isPlatformStarted()) {
      Platform.startup(PlatformUtils::setPlatformStarted);
    }
  }

  /**
   * Tests constructor with null argument throws null pointer.
   */
  @Test
  @DisplayName("null control throws exception")
  void nullControlConstructor() {
    assertThrows(IllegalArgumentException.class, () -> new FeatureTemplatePickerTilePaneSkin(null));
  }

  /**
   * Tests that skin correctly initializes its children.
   */
  @Test
  @DisplayName("child nodes initialize")
  void initializesChildNodes() {
    FeatureTemplatePickerTilePaneSkin skin = new FeatureTemplatePickerTilePaneSkin(new FeatureTemplatePicker());
    assertEquals(1, skin.getChildren().size());
    assertTrue(skin.getChildren().get(0) instanceof ScrollPane);
  }

  /**
   * Tests that the content node inside the scroll pane changes orientation when the picker's orientation changes.
   */
  @Test
  @DisplayName("content node changes when orientation changes")
  void contentNodeOrientation() {
    FeatureTemplatePicker featureTemplatePicker = new FeatureTemplatePicker();
    FeatureTemplatePickerTilePaneSkin skin = new FeatureTemplatePickerTilePaneSkin(featureTemplatePicker);
    ScrollPane scrollPane = (ScrollPane) skin.getChildren().get(0);
    featureTemplatePicker.setOrientation(Orientation.HORIZONTAL);
    assertTrue(scrollPane.getContent() instanceof HBox);
    featureTemplatePicker.setOrientation(Orientation.VERTICAL);
    assertTrue(scrollPane.getContent() instanceof VBox);
  }
}
