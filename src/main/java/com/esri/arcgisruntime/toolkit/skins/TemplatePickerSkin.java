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

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import com.esri.arcgisruntime.data.ArcGISFeatureTable;
import com.esri.arcgisruntime.data.FeatureTemplate;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.symbology.Renderer;
import com.esri.arcgisruntime.symbology.Symbol;
import com.esri.arcgisruntime.toolkit.TemplatePicker;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class TemplatePickerSkin extends SkinBase<TemplatePicker> {

  private final SimpleListProperty<FeatureLayer> featureLayers = new SimpleListProperty<>();
  private final SimpleDoubleProperty maxLabelWidthProperty = new SimpleDoubleProperty();

  private final SimpleIntegerProperty symbolSizeProperty = new SimpleIntegerProperty();

  private final ArrayList<TemplateCell> cells = new ArrayList<>();

  private final VBox vBox = new VBox();
  private final ScrollPane scrollPane = new ScrollPane(vBox);

  public TemplatePickerSkin(TemplatePicker control) {
    super(control);

    scrollPane.prefViewportWidthProperty().bind(vBox.widthProperty());
    scrollPane.prefViewportHeightProperty().bind(vBox.heightProperty());
    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

    getChildren().add(scrollPane);

    featureLayers.bind(control.featureLayerListProperty());

    featureLayers.addListener((InvalidationListener) observable -> refresh());

    symbolSizeProperty.bind(control.symbolSizeProperty());

    refresh();
  }

  private void refresh() {
    vBox.getChildren().clear();
    cells.clear();
    maxLabelWidthProperty.set(0.0);
    getSkinnable().selectedTemplateProperty().set(null);

    featureLayers.stream().filter(entry -> entry.getFeatureTable() instanceof ArcGISFeatureTable)
      .forEach(featureLayer -> {
        switch (featureLayer.getLoadStatus()) {
          case NOT_LOADED:
            // refresh list once the layer us loaded
            featureLayer.addDoneLoadingListener(() -> refresh());
            break;
          case FAILED_TO_LOAD:
            // do nothing - layer is ignored
            break;
          case LOADED:
            // layer is loaded so add cells for each template
            ArcGISFeatureTable featureTable = (ArcGISFeatureTable) featureLayer.getFeatureTable();
            Renderer renderer = featureLayer.getRenderer();
            featureTable.getFeatureTemplates()
              .forEach(featureTemplate -> {
                TemplateCell cell = createTemplateLabel(featureTemplate, featureLayer);
                vBox.getChildren().add(cell);
                cells.add(cell);
              });
            featureTable.getFeatureTypes()
              .forEach(featureType -> featureType.getTemplates()
                .forEach(featureTemplate -> {
                  TemplateCell cell = createTemplateLabel(featureTemplate, featureLayer);
                  vBox.getChildren().add(cell);
                  cells.add(cell);
                }));
        }
      });
  }

  Region calculateRegion(Region region) {
    Group root = new Group();
    Scene dummyScene = new Scene(root);
    root.getChildren().add(region);
    root.applyCss();
    root.layout();

    return region;
  }

  private TemplateCell createTemplateLabel(FeatureTemplate featureTemplate, FeatureLayer featureLayer) {
    TemplateCell cell = new TemplateCell(new TemplatePicker.Template(featureLayer, featureTemplate), this);

    cell.setOnMouseClicked(e -> {
      getSkinnable().selectedTemplateProperty().set(cell.getTemplate());
      cells.forEach(c -> c.setSelected(false));
      cell.setSelected(true);
    });

    return cell;
  }

  static class TemplateCell extends Label {
    private final TemplatePicker.Template template;
    private final TemplatePickerSkin templatePickerSkin;

    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");
    private BooleanProperty selectedProperty;

    TemplateCell(TemplatePicker.Template template, TemplatePickerSkin templatePickerSkin) {
      this.template = Objects.requireNonNull(template);
      this.templatePickerSkin = Objects.requireNonNull(templatePickerSkin);

      getStyleClass().add("template-cell");
      String styleSheet = getClass().getResource("template-cell.css").toExternalForm();
      getStylesheets().add(styleSheet);

      setMaxWidth(Double.MAX_VALUE);
      setPadding(new Insets(5.0));

      templatePickerSkin.symbolSizeProperty.addListener(observable -> update());

      update();
    }

    private void update() {
      FeatureTemplate featureTemplate = template.getFeatureTemplate();
      FeatureLayer featureLayer = template.getFeatureLayer();

      Graphic graphic = new Graphic();
      graphic.getAttributes().putAll(featureTemplate.getPrototypeAttributes());
      Symbol symbol = featureLayer.getRenderer().getSymbol(graphic);
      try {
        int size = templatePickerSkin.symbolSizeProperty.get();
        setGraphic(new ImageView(symbol.createSwatchAsync(size, size, 1.0f, 0x00).get()));
      } catch (InterruptedException | ExecutionException e) {
        setGraphic(null);
      }
      setTooltip(new Tooltip(featureLayer.getName() + " : " + featureTemplate.getName()));
    }

    public TemplatePicker.Template getTemplate() {
      return template;
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
            return TemplateCell.this;
          }

          @Override
          public String getName() {
            return "selected";
          }
        };
      }
      return selectedProperty;
    }
  }
}
