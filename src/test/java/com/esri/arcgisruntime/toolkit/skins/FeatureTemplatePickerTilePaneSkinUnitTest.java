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

@DisplayName("feature template picker tile pane skin unit tests")
public class FeatureTemplatePickerTilePaneSkinUnitTest {

  @BeforeAll
  static void startPlatform() {
    Platform.startup(() -> {
    });
  }

  @Test
  @DisplayName("null control throws exception")
  void nullControlConstructor() {
    assertThrows(IllegalArgumentException.class, () -> new FeatureTemplatePickerTilePaneSkin(null));
  }

  @Test
  @DisplayName("root child is scrollpane")
  void scrollpaneChild() {
    FeatureTemplatePickerTilePaneSkin skin = new FeatureTemplatePickerTilePaneSkin(new FeatureTemplatePicker());
    assertEquals(1, skin.getChildren().size());
    assertTrue(skin.getChildren().get(0) instanceof ScrollPane);
  }

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
