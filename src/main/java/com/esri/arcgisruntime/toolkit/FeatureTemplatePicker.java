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

import java.util.ArrayList;

import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.toolkit.skins.FeatureTemplatePickerSkin;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class FeatureTemplatePicker extends Control {

  private final ObservableList<FeatureLayer> featureLayers = FXCollections.observableList(new ArrayList<>());
  private final SimpleListProperty<FeatureLayer> featureLayerListProperty = new SimpleListProperty<>(featureLayers);
  private final SimpleObjectProperty<TemplatePicker.Template> selectedTemplateProperty = new SimpleObjectProperty<>();
  private final SimpleIntegerProperty symbolWidthProperty = new SimpleIntegerProperty(50);
  private final SimpleIntegerProperty symbolHeightProperty = new SimpleIntegerProperty(50);
  private final SimpleBooleanProperty showTemplateNamesProperty = new SimpleBooleanProperty(false);
  private final SimpleBooleanProperty showFeatureLayerNamesProperty = new SimpleBooleanProperty(true);
  private final SimpleBooleanProperty showSeparatorsProperty = new SimpleBooleanProperty(true);
  private final SimpleBooleanProperty disableCannotAddFeatureLayersProperty = new SimpleBooleanProperty(true);

  public SimpleListProperty<FeatureLayer> featureLayerListProperty() {
    return featureLayerListProperty;
  }

  public SimpleObjectProperty<TemplatePicker.Template> selectedTemplateProperty() {
    return selectedTemplateProperty;
  }

  public SimpleIntegerProperty symbolWidthProperty() {
    return symbolWidthProperty;
  }

  public SimpleIntegerProperty symbolHeightProperty() {
    return symbolHeightProperty;
  }

  public SimpleBooleanProperty showTemplateNamesProperty() {
    return showTemplateNamesProperty;
  }

  public SimpleBooleanProperty showFeatureLayerNamesProperty() {
    return showFeatureLayerNamesProperty;
  }

  public SimpleBooleanProperty showSeparatorsProperty() {
    return showSeparatorsProperty;
  }

  public SimpleBooleanProperty disableCannotAddFeatureLayersProperty() {
    return disableCannotAddFeatureLayersProperty;
  }

  @Override
  protected Skin<?> createDefaultSkin() {
    return new FeatureTemplatePickerSkin(this);
  }
}
