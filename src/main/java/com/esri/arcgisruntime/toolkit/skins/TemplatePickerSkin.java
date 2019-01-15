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

package com.esri.arcgisruntime.toolkit.skins;

import java.util.concurrent.ExecutionException;

import com.esri.arcgisruntime.data.FeatureTemplate;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.symbology.Renderer;
import com.esri.arcgisruntime.symbology.Symbol;
import com.esri.arcgisruntime.toolkit.TemplatePicker;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SkinBase;
import javafx.scene.image.ImageView;

public class TemplatePickerSkin extends SkinBase<TemplatePicker> {

  private final ListView<FeatureTemplate> listView = new ListView<>();
  private final SimpleObjectProperty<Renderer> rendererProperty = new SimpleObjectProperty<>();

  public TemplatePickerSkin(TemplatePicker control) {
    super(control);

    listView.itemsProperty().bind(control.templateListProperty());

    control.selectedItemProperty().bind(listView.getSelectionModel().selectedItemProperty());
    rendererProperty.bind(control.rendererProperty());

    getChildren().addAll(listView);

    listView.setCellFactory(c -> new TemplateListCell());

    //refresh if the renderer is changed
    rendererProperty.addListener(o -> listView.refresh());
  }

  class TemplateListCell extends ListCell<FeatureTemplate> {
    @Override
    protected void updateItem(FeatureTemplate item, boolean empty) {
      super.updateItem(item, empty);
      if (item != null) {
        Renderer renderer = rendererProperty.get();
        if (renderer != null) {
          Graphic graphic = new Graphic();
          graphic.getAttributes().putAll(item.getPrototypeAttributes());
          Symbol symbol = rendererProperty.get().getSymbol(graphic);
          try {
            setGraphic(new ImageView(symbol.createSwatchAsync(0x00).get()));
          } catch (InterruptedException | ExecutionException e) {
            setGraphic(null);
          }
        }
        setText(item.getName());
      } else {
        // make sure to clear any empty entries
        setText(null);
        setGraphic(null);
      }
    }
  }
}
