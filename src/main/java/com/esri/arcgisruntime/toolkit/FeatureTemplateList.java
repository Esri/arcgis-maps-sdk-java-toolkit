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

import com.esri.arcgisruntime.data.ArcGISFeatureTable;
import com.esri.arcgisruntime.data.FeatureTemplate;
import com.esri.arcgisruntime.toolkit.skins.FeatureTemplateListSkin;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class FeatureTemplateList extends Control {

  private final SimpleObjectProperty<ArcGISFeatureTable> featureTableProperty = new SimpleObjectProperty<>();

  private final SimpleBooleanProperty showLayerNameProperty = new SimpleBooleanProperty(true);
  private final SimpleBooleanProperty showTemplateNameProperty = new SimpleBooleanProperty(false);

  private final SimpleIntegerProperty symbolWidthProperty = new SimpleIntegerProperty(50);
  private final SimpleIntegerProperty symbolHeightProperty = new SimpleIntegerProperty(50);

  private final SimpleObjectProperty<FeatureTemplate> selectedTemplateProperty = new SimpleObjectProperty<>();

  public FeatureTemplateList(ArcGISFeatureTable featureTable) {
    featureTableProperty.set(featureTable);
  }

  public ReadOnlyObjectProperty<ArcGISFeatureTable> featureTableProperty() {
    return featureTableProperty;
  }

  public SimpleBooleanProperty showLayerNameProperty() {
    return showLayerNameProperty;
  }

  public SimpleBooleanProperty showTemplateNameProperty() {
    return showTemplateNameProperty;
  }

  public SimpleIntegerProperty symbolWidthProperty() {
    return symbolWidthProperty;
  }

  public SimpleIntegerProperty symbolHeightProperty() {
    return symbolHeightProperty;
  }

  public SimpleObjectProperty<FeatureTemplate> selectedTemplateProperty() {
    return selectedTemplateProperty;
  }

  public void clearSelection() {
    ((FeatureTemplateListSkin) getSkin()).clearSelection();
    selectedTemplateProperty.set(null);
  }

  @Override
  protected Skin<?> createDefaultSkin() {
    return new FeatureTemplateListSkin(this);
  }
}
