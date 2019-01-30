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
import java.util.Objects;

import com.esri.arcgisruntime.data.FeatureTemplate;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.toolkit.skins.TemplatePickerSkin;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class TemplatePicker extends Control {

  ObservableList<FeatureLayer> featureLayers = FXCollections.observableList(new ArrayList<>());
  SimpleListProperty<FeatureLayer> featureLayerListProperty = new SimpleListProperty<>(featureLayers);
  SimpleObjectProperty<Template> selectedTemplateProperty = new SimpleObjectProperty<>();
  SimpleIntegerProperty symbolSizeProperty = new SimpleIntegerProperty(50);

  @Override
  protected Skin<?> createDefaultSkin() {
    return new TemplatePickerSkin(this);
  }

  public SimpleListProperty<FeatureLayer> featureLayerListProperty() {
    return featureLayerListProperty;
  }

  public SimpleObjectProperty<Template> selectedTemplateProperty() {
    return selectedTemplateProperty;
  }

  public SimpleIntegerProperty symbolSizeProperty() {
    return symbolSizeProperty;
  }

  public static class Template {
    private FeatureLayer featureLayer;
    private FeatureTemplate featureTemplate;

    public Template(FeatureLayer featureLayer, FeatureTemplate featureTemplate) {
      this.featureLayer = Objects.requireNonNull(featureLayer);
      this.featureTemplate = Objects.requireNonNull(featureTemplate);
    }

    public FeatureLayer getFeatureLayer() {
      return featureLayer;
    }

    public FeatureTemplate getFeatureTemplate() {
      return featureTemplate;
    }
  }
}
