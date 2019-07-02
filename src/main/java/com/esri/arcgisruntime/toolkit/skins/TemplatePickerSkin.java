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

package com.esri.arcgisruntime.toolkit.skins;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import com.esri.arcgisruntime.data.ArcGISFeatureTable;
import com.esri.arcgisruntime.data.FeatureTemplate;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.symbology.Symbol;
import com.esri.arcgisruntime.toolkit.TemplatePicker;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.css.PseudoClass;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;

/**
 * A skin for {@link TemplatePicker}.
 *
 * @since 100.5
 */
public final class TemplatePickerSkin extends SkinBase<TemplatePicker> {

  private final SimpleListProperty<FeatureLayer> featureLayers = new SimpleListProperty<>();
  private final SimpleIntegerProperty symbolWidthProperty = new SimpleIntegerProperty();
  private final SimpleIntegerProperty symbolHeightProperty = new SimpleIntegerProperty();
  private final SimpleBooleanProperty showTemplateNamesProperty = new SimpleBooleanProperty();
  private final SimpleBooleanProperty showFeatureLayerNamesProperty = new SimpleBooleanProperty();
  private final SimpleBooleanProperty showSeparatorsProperty = new SimpleBooleanProperty();
  private final SimpleBooleanProperty disableCannotAddFeatureLayersProperty = new SimpleBooleanProperty();

  private final VBox vBox = new VBox();
  private final ScrollPane scrollPane = new ScrollPane(vBox);
  private final StackPane stackPane = new StackPane();

  private boolean contentInvalid = true;
  private boolean sizeInvalid = true;

  private final LinkedHashMap<FeatureLayer, List<TemplateCell>> cellMap = new LinkedHashMap<>();
  private final ArrayList<TilePane> tilePanes = new ArrayList<>();

  /**
   * Creates a new skin instance.
   *
   * @param control the control that is to be skinned
   * @since 100.5
   * @throws NullPointerException if control is null
   */
  public TemplatePickerSkin(TemplatePicker control) {
    super(Objects.requireNonNull(control));

    scrollPane.setFitToWidth(true);
    scrollPane.setFitToHeight(true);

    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

    stackPane.getChildren().add(scrollPane);
    getChildren().add(stackPane);

    control.widthProperty().addListener(observable -> sizeInvalid = true);
    control.heightProperty().addListener(observable -> sizeInvalid = true);
    control.insetsProperty().addListener(observable -> sizeInvalid = true);

    featureLayers.bind(control.featureLayerListProperty());
    featureLayers.addListener((InvalidationListener) observable -> populate());

    symbolWidthProperty.bind(control.symbolWidthProperty());
    symbolWidthProperty.addListener(observable -> contentInvalid = true);

    symbolHeightProperty.bind(control.symbolHeightProperty());
    symbolHeightProperty.addListener(observable -> contentInvalid = true);

    showTemplateNamesProperty.bind(control.showTemplateNamesProperty());
    showTemplateNamesProperty.addListener(observable -> contentInvalid = true);

    showFeatureLayerNamesProperty.bind(control.showFeatureLayerNamesProperty());
    showFeatureLayerNamesProperty.addListener(observable -> {
      contentInvalid = true;
      control.requestLayout();
    });

    showSeparatorsProperty.bind(control.showSeparatorsProperty());
    showSeparatorsProperty.addListener(observable -> {
      contentInvalid = true;
      control.requestLayout();
    });

    disableCannotAddFeatureLayersProperty.bind(control.disableCannotAddFeatureLayersProperty());
    disableCannotAddFeatureLayersProperty.addListener(observable -> {
      contentInvalid = true;
      control.requestLayout();
    });

    populate();
  }

  /**
   * Clears any selection.
   *
   * @since 100.5
   */
  public void clearSelection() {
    cellMap.forEach(((featureLayer, templateCells) ->
      templateCells.stream().filter(TemplateCell::isSelected).findFirst().ifPresent(t -> t.setSelected(false))));
  }

  /**
   * Updates the skin when the templates change.
   *
   * @param contentWidth the content width
   * @param contentHeight the content height
   * @since 100.5
   */
  private void update(double contentWidth, double contentHeight) {
    tilePanes.clear();
    vBox.getChildren().clear();
    stackPane.setMaxSize(contentWidth, contentHeight);

    cellMap.forEach(((featureLayer, templateCells) -> {
      VBox tileBox = new VBox();
      vBox.getChildren().add(tileBox);
      if (showFeatureLayerNamesProperty.get()) {
        tileBox.getChildren().add(new Label(featureLayer.getName()));
      }
      TilePane tilePane = new TilePane();
      tilePanes.add(tilePane);
      tilePane.setAlignment(Pos.TOP_LEFT);
      tilePane.setMaxSize(contentWidth, contentHeight);
      tilePane.getChildren().addAll(templateCells);
      if (disableCannotAddFeatureLayersProperty.get() && !featureLayer.getFeatureTable().canAdd()) {
        tilePane.setDisable(true);
      }
      tileBox.getChildren().add(tilePane);
      if (showSeparatorsProperty.get()) {
        vBox.getChildren().add(new Separator());
      }
    }));
  }

  @Override
  protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
//    if (contentInvalid) {
//      System.out.println("content invalid " + cellMap.size());
//      update(contentWidth, contentHeight);
//      contentInvalid = false;
//      sizeInvalid = false;
//    }
//
//    if (sizeInvalid) {
//      System.out.println("size invalid");
//      stackPane.setMaxSize(contentWidth, contentHeight);
//      tilePanes.forEach(tilePane -> tilePane.setMaxSize(contentWidth, contentHeight));
//      sizeInvalid = false;
//    }
//
    if (contentInvalid || sizeInvalid) {
      update(contentWidth, contentHeight);
//      stackPane.setMaxSize(contentWidth, contentHeight);
//      tilePanes.forEach(tilePane -> tilePane.setMaxSize(contentWidth, contentHeight));
      contentInvalid = false;
      sizeInvalid = false;
    }

    layoutInArea(stackPane, contentX, contentY, contentWidth, contentHeight, -1, HPos.CENTER, VPos.CENTER);
  }

  /**
   * Updates the skin whenever the feature layer list is changed.
   *
   * @since 100.5
   */
  private void populate() {
    cellMap.clear();
    getSkinnable().selectedTemplateProperty().set(null);
    contentInvalid = true;

    featureLayers.stream().filter(entry -> entry.getFeatureTable() instanceof ArcGISFeatureTable)
      .forEach(featureLayer -> {
        switch (featureLayer.getLoadStatus()) {
          case NOT_LOADED:
            // populate list once the layer is loaded
            featureLayer.removeDoneLoadingListener(this::populate); // avoid adding multiple instances of listener
            featureLayer.addDoneLoadingListener(this::populate);
            break;
          case FAILED_TO_LOAD:
            // do nothing - layer is ignored
            break;
          case LOADED:
            // layer is loaded so add cells for each template
            ArcGISFeatureTable featureTable = (ArcGISFeatureTable) featureLayer.getFeatureTable();
            ArrayList<TemplateCell> templateCells = new ArrayList<>();

            featureTable.getFeatureTemplates()
              .forEach(featureTemplate -> templateCells.add(createTemplateCell(featureTemplate, featureLayer)));
            featureTable.getFeatureTypes()
              .forEach(featureType -> featureType.getTemplates()
                .forEach(featureTemplate -> templateCells.add(createTemplateCell(featureTemplate, featureLayer))));
            cellMap.put(featureLayer, templateCells);
            break;
        }
      });

    getSkinnable().requestLayout();
  }

  /**
   * Creates a new {@link TemplateCell} to display a template.
   *
   * @param featureTemplate the feature template
   * @param featureLayer the feature layer
   * @return a new cell
   * @since 100.5
   */
  private TemplateCell createTemplateCell(FeatureTemplate featureTemplate, FeatureLayer featureLayer) {
    TemplateCell cell = new TemplateCell(new TemplatePicker.Template(featureLayer, featureTemplate), this);

    cell.setOnMouseClicked(e -> {
      cellMap.forEach((layer, cells) -> cells.forEach(c -> c.setSelected(false)));
      cell.setSelected(true);
      getSkinnable().selectedTemplateProperty().set(cell.getTemplate());
    });

    return cell;
  }

  /**
   * A control which displays a sample image for a feature template and optionally a template name. Also handles
   * highlighting when the control is selected.
   *
   * @since 100.5
   */
  static class TemplateCell extends Label {
    private final TemplatePicker.Template template;
    private final TemplatePickerSkin templatePickerSkin;

    // define a psuedo class that will highlight the control if it is selected
    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");
    private BooleanProperty selectedProperty;

    /**
     * Creates a new instance.
     *
     * @param template the feature template
     * @param templatePickerSkin the skin that this control will be associated with
     * @throws NullPointerException if template is null
     * @throws NullPointerException if templatePickerSkin is null
     * @since 100.5
     */
    TemplateCell(TemplatePicker.Template template, TemplatePickerSkin templatePickerSkin) {
      this.template = Objects.requireNonNull(template);
      this.templatePickerSkin = Objects.requireNonNull(templatePickerSkin);

      getStyleClass().add("template-cell");
      String styleSheet = getClass().getResource("template-cell.css").toExternalForm();
      getStylesheets().add(styleSheet);

      setMaxWidth(Double.MAX_VALUE);
      setPadding(new Insets(5.0));
      setContentDisplay(ContentDisplay.LEFT);

      templatePickerSkin.symbolWidthProperty.addListener(observable -> update());
      templatePickerSkin.symbolHeightProperty.addListener(observable -> update());

      templatePickerSkin.showTemplateNamesProperty.addListener(observable -> update());

      update();
    }

    /**
     * Updates the control when the content or appearance changes.
     *
     * @since 100.5
     */
    private void update() {
      FeatureTemplate featureTemplate = template.getFeatureTemplate();
      FeatureLayer featureLayer = template.getFeatureLayer();

      if (templatePickerSkin.showTemplateNamesProperty.get()) {
        setText(featureTemplate.getName());
      } else {
        setText(null);
      }

      Graphic graphic = new Graphic();
      graphic.getAttributes().putAll(featureTemplate.getPrototypeAttributes());
      Symbol symbol = featureLayer.getRenderer().getSymbol(graphic);
      try {
        int width = templatePickerSkin.symbolWidthProperty.get();
        int height = templatePickerSkin.symbolHeightProperty.get();
        Image image = symbol.createSwatchAsync(width, height, 1.0f / (float) Screen.getPrimary().getOutputScaleX(), 0x00).get();
        setGraphic(new ImageView(image));
      } catch (Exception e) {
        e.printStackTrace();
        setGraphic(null);
      }
      setTooltip(new Tooltip(featureLayer.getName() + " : " + featureTemplate.getName()));
    }

    /**
     * Gets the template associated with this control.
     *
     * @return the template
     */
    public TemplatePicker.Template getTemplate() {
      return template;
    }

    /**
     * Sets the control to be selected or not.
     *
     * @param selected true to select, false otherwise
     * @since 100.5
     */
    public final void setSelected(boolean selected) {
      selectedProperty().set(selected);
    }

    /**
     * Gets if the control is selected or not.
     *
     * @return true if selected, otherwise false
     * @since 100.5
     */
    public final boolean isSelected() {
      return selectedProperty != null && selectedProperty.get();
    }

    /**
     * Gets the property representing if the control is selected or not.
     *
     * @return the property
     * @since 100.5
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
