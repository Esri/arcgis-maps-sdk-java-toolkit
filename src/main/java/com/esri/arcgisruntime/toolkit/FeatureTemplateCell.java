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

import com.esri.arcgisruntime.data.FeatureTemplate;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.toolkit.skins.FeatureTemplateCellSkin;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * A control which shows a cell containing a visual representation of a single feature template and allows marking the
 * cell as selected.
 *
 * @since 100.6.0
 */
public final class FeatureTemplateCell extends Control {

  private final SimpleObjectProperty<FeatureTemplatePicker.Template> templateProperty = new SimpleObjectProperty<>();
  private final SimpleIntegerProperty symbolWidthProperty = new SimpleIntegerProperty(50);
  private final SimpleIntegerProperty symbolHeightProperty = new SimpleIntegerProperty(50);

  private final SimpleBooleanProperty showNameProperty = new SimpleBooleanProperty(false);

  // define a psuedo class that will highlight the control if it is selected
  private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");
  private BooleanProperty selectedProperty;

  /**
   * Creates an instance.
   *
   * @param featureLayer the feature layer
   * @param featureTemplate the feature template
   * @since 100.6.0
   */
  public FeatureTemplateCell(FeatureLayer featureLayer, FeatureTemplate featureTemplate) {
    templateProperty.set(new FeatureTemplatePicker.Template(featureLayer, featureTemplate));

    getStyleClass().add("template-cell");
  }

  /**
   * Returns the a property containing the {@link FeatureTemplatePicker.Template} shown by the cell.
   *
   * @return a feature template
   * @since 100.6.0
   */
  public ReadOnlyObjectProperty<FeatureTemplatePicker.Template> templateProperty() {
    return templateProperty;
  }

  /**
   * A property controlling the width of the symbol used in the cell.
   *
   * @return the symbol width property
   * @since 100.6.0
   */
  public SimpleIntegerProperty symbolWidthProperty() {
    return symbolWidthProperty;
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
  public SimpleIntegerProperty symbolHeightProperty() {
    return symbolHeightProperty;
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

  /**
   * A property controlling if the cell shows the template name.
   *
   * @return the show name property
   * @since 100.6.0
   */
  public SimpleBooleanProperty showNameProperty() {
    return showNameProperty;
  }

  /**
   * Sets the value of the {@link #showNameProperty()}.
   *
   * @param showName true to show names
   * @since 100.6.0
   */
  public void setShowName(boolean showName) {
    showNameProperty().set(showName);
  }

  /**
   * Gets the value of the {@link #showNameProperty()}.
   *
   * @return true if names are shown, false otherwise
   * @since 100.6.0
   */
  public boolean isShowName() {
    return showNameProperty().get();
  }

  /**
   * Defines the selection state of the cell. Setting this to true will cause the cell to be selected and false will
   * cause it to be unselected. The selection style uses {@code -fx-accent} be defualt but can be changed by CSS for
   * example
   * <p>
   *   <pre>
   *   .template-cell: selected {
   *     -fx-background-color: pink;
   *   }
   *   </pre>
   * </p>
   *
   * @return the selected property
   * @since 100.6.0
   */
  public final BooleanProperty selectedProperty() {
    if (selectedProperty == null) {
      selectedProperty = new BooleanPropertyBase(false) {
        @Override
        protected void invalidated() {
          pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, get());
        }

        @Override
        public Object getBean() {
          return FeatureTemplateCell.this;
        }

        @Override
        public String getName() {
          return "selected";
        }
      };
    }
    return selectedProperty;
  }

  /**
   * Sets the value of the {@link #selectedProperty()}.
   *
   * @param selected true to select the cell, false to unselect
   * @since 100.6.0
   */
  public final void setSelected(boolean selected) {
    selectedProperty().set(selected);
  }

  /**
   * Gets the value of the {@link #selectedProperty()}.
   *
   * @return true if the cell is selected, false otherwise
   * @since 100.6.0
   */
  public final boolean isSelected() {
    return selectedProperty != null && selectedProperty.get();
  }

  @Override
  public String getUserAgentStylesheet() {
    return FeatureTemplateCell.class.getResource("skins/template-cell.css").toExternalForm();
  }

  @Override
  protected Skin<?> createDefaultSkin() {
    return new FeatureTemplateCellSkin(this);
  }
}
