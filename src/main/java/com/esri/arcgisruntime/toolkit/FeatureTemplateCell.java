/*
 COPYRIGHT 1995-2019 ESRI

 TRADE SECRETS: ESRI PROPRIETARY AND CONFIDENTIAL
 Unpublished material - all rights reserved under the
 Copyright Laws of the United States.

 For additional information, contact:
 Environmental Systems Research Institute, Inc.
 Attn: Contracts Dept
 380 New York Street
 Redlands, California, USA 92373

 email: contracts@esri.com
 */

package com.esri.arcgisruntime.toolkit;

import com.esri.arcgisruntime.data.Feature;
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

public class FeatureTemplateCell extends Control {

  private SimpleObjectProperty<TemplatePicker.Template> templateProperty = new SimpleObjectProperty<>();
  private SimpleIntegerProperty imageWidthProperty = new SimpleIntegerProperty(50);
  private SimpleIntegerProperty imageHeightProperty = new SimpleIntegerProperty(50);

  private SimpleBooleanProperty showNameProperty = new SimpleBooleanProperty(false);

  // define a psuedo class that will highlight the control if it is selected
  private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");
  private BooleanProperty selectedProperty;

  public FeatureTemplateCell(FeatureLayer featureLayer, FeatureTemplate featureTemplate) {
    templateProperty.set(new TemplatePicker.Template(featureLayer, featureTemplate));

    getStyleClass().add("template-cell");
    String styleSheet = getClass().getResource("skins/template-cell.css").toExternalForm();
    getStylesheets().add(styleSheet);
  }

  public ReadOnlyObjectProperty<TemplatePicker.Template> templateProperty() {
    return templateProperty;
  }

  public SimpleIntegerProperty imageWidthProperty() {
    return imageWidthProperty;
  }

  public SimpleIntegerProperty imageHeightProperty() {
    return imageHeightProperty;
  }

  public SimpleBooleanProperty showNameProperty() {
    return showNameProperty;
  }

  public final void setSelected(boolean selected) {
    selectedProperty().set(selected);
  }

  public final boolean isSelected() {
    return selectedProperty != null && selectedProperty.get();
  }

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

  @Override
  protected Skin<?> createDefaultSkin() {
    return new FeatureTemplateCellSkin(this);
  }
}
