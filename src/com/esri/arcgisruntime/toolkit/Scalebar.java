/*
 * Copyright 2017 Esri
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

package toolkit;

import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public final class Scalebar extends Control {

  /**
   * Scalebar styles.
   */
  public enum Style {
    LINE,
    BAR,
    GRADUATED_LINE,
    ALTERNATING_BAR_LINE,
    DUAL_UNIT_LINE,
  }

  private Style skinStyle;

  public Scalebar(Style style) {
    skinStyle = style != null ? style : Style.LINE;
  }

  @Override
  protected Skin<?> createDefaultSkin() {
    switch (skinStyle) {
      case LINE:
        break;
      case BAR:
        break;
      case GRADUATED_LINE:
        break;
      case ALTERNATING_BAR_LINE:
        break;
      case DUAL_UNIT_LINE:
        break;
    }
    return super.createDefaultSkin();
  }
}
