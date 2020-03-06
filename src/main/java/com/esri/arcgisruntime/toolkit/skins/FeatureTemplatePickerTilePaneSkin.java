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

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.symbology.Symbol;
import com.esri.arcgisruntime.toolkit.FeatureTemplateGroup;
import com.esri.arcgisruntime.toolkit.FeatureTemplateItem;
import com.esri.arcgisruntime.toolkit.FeatureTemplatePicker;
import javafx.collections.ListChangeListener;
import javafx.css.PseudoClass;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * <p>A skin for displaying a {@link FeatureTemplatePicker} with tile panes. Feature template groups correspond to
 * tile panes and feature template items correspond to toggle buttons within the tile panes.</p>
 *
 * <p> If the picker's orientation is horizontal, tile panes will be aligned left to right. If vertical, tile panes
 * are aligned top to bottom.</p>
 *
 * <p>The symbol size determines the height and width in dp of the symbol swatch. Toggle buttons will have a
 * preferred size of 2 * symbol size. Template names which overflow this size will be clipped with ellipsis.</p>
 *
 * <p>The following css selectors can be used to style the picker:</p>
 *
 * <ul>
 * <li>.feature-template-picker: styles the content node of the scroll pane</li>
 * <li>.feature-template-group: styles the vbox containing the feature template group label and tile pane</li>
 * <li>.tile-pane: style the tile pane in a feature template group</li>
 * <li>.feature-template-item: styles the toggle button</li></p>
 * </ul>
 *
 * <p>A "horizontal" pseudo class is added to the template picker when its orientation is horizontal. The following
 * snippet shows a selector for applying a style to feature template groups only when the picker has a horizontal
 * orientation:</p>
 * <pre>
 * .feature-template-picker:horizontal .feature-template-group {
 *
 * }
 * </pre>
 *
 * @since 100.7.0
 */
public final class FeatureTemplatePickerTilePaneSkin extends SkinBase<FeatureTemplatePicker> {

  private static final PseudoClass HORIZONTAL_PSEUDO_CLASS = PseudoClass.getPseudoClass("horizontal");

  private Pane contentPane = new VBox();
  private final ScrollPane scrollPane = new ScrollPane();
  private final ToggleGroup toggleGroup = new ToggleGroup();
  private boolean swatchesNeedUpdate = false;

  public FeatureTemplatePickerTilePaneSkin(FeatureTemplatePicker control) {
    super(control);
    // put everything in a scroll pane to handle sizing within the skin
    getChildren().add(scrollPane);

    updateOrientation();

    // use a shared toggle group for all feature template items to keep track of the current selected item
    toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) ->
      control.setSelectedFeatureTemplateItem(newValue == null ? null : (FeatureTemplateItem) newValue.getUserData())
    );
    control.selectedFeatureTemplateItemProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue == null) {
        toggleGroup.selectToggle(null);
      } else {
        // when selected programmatically, find its toggle in the toggle group and select it in the toggle group
        Toggle matchingToggle = toggleGroup.getToggles().stream()
            .filter(t -> (t.getUserData()).equals(newValue))
            .findFirst()
            .orElse(null);
        toggleGroup.selectToggle(matchingToggle);
      }
    });

    // add a tile pane for each feature template group to the view
    control.getFeatureTemplateGroups().stream()
        .map(this::createVBoxForFeatureTemplateGroup)
        .forEach(tilePane -> contentPane.getChildren().add(tilePane));

    // add or remove tile panes if the feature template group collection changes
    control.featureTemplateGroupsProperty().addListener((ListChangeListener<FeatureTemplateGroup>) c -> {
      while (c.next()) {
        if (c.wasAdded()) {
          // create vboxes for the each feature template group
          List<VBox> added = c.getAddedSubList().stream()
              .map(this::createVBoxForFeatureTemplateGroup)
              .collect(Collectors.toList());
          // add the vboxes to the view
          contentPane.getChildren().addAll(c.getFrom(), added);
        } else if (c.wasRemoved()) {
          // remove the vboxes based on the
          contentPane.getChildren().remove(c.getFrom(), c.getFrom() + c.getRemovedSize());
        }
      }
    });

    control.orientationProperty().addListener((observable, oldValue, newValue) ->
      this.updateOrientation()
    );

    // update all swatches together when the symbol size changes
    control.symbolSizeProperty().addListener((observable, oldValue, newValue) -> {
      swatchesNeedUpdate = true;
      control.requestLayout();
    });

    // update all item swatches once populated
    swatchesNeedUpdate = true;
    control.requestLayout();
  }

  @Override
  protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
    if (swatchesNeedUpdate) {
      // ensure an update is only triggered once per symbol size change
      swatchesNeedUpdate = false;
      // trigger all symbol swatch updates
      CompletableFuture<?>[] futures = contentPane.getChildren().stream()
          .flatMap(n -> ((VBox) n).getChildren().stream())
          .filter(n -> n instanceof TilePane)
          .flatMap(tilePane -> ((TilePane) tilePane).getChildren().stream())
          .filter(n -> n instanceof ToggleButton)
          .map(n -> {
            ToggleButton toggleButton = (ToggleButton) n;
            FeatureTemplateItem featureTemplateItem = (FeatureTemplateItem) toggleButton.getUserData();
            ImageView imageView = (ImageView) toggleButton.getGraphic();
            return updateSwatch(featureTemplateItem, imageView);
          })
          .toArray(CompletableFuture<?>[]::new);
      // wait until all updates finished before updating layout normally
      CompletableFuture.allOf(futures).thenRunAsync(() ->
        super.layoutChildren(contentX, contentY, contentWidth, contentHeight)
      );
    } else {
      super.layoutChildren(contentX, contentY, contentWidth, contentHeight);
    }
  }

  /**
   * Updates the control's content pane and scroll pane based on the control's orientation.
   */
  private void updateOrientation() {
    Orientation orientation = getSkinnable().getOrientation();
    // keep a reference to the current children
    List<Node> children = contentPane.getChildren();
    // create a new content pane based on the orientation
    contentPane = orientation == Orientation.HORIZONTAL ? new HBox() : new VBox();
    // add a style class so the picker can be styled via CSS
    contentPane.getStyleClass().add("feature-template-picker");
    // add a CSS pseudo class so the picker can be styled based on it orientation
    contentPane.pseudoClassStateChanged(HORIZONTAL_PSEUDO_CLASS, orientation == Orientation.HORIZONTAL);
    // copy the children back into the new content pane
    contentPane.getChildren().addAll(children);
    scrollPane.setContent(contentPane);
    // size the content pane to the scroll pane
    scrollPane.setFitToWidth(orientation == Orientation.VERTICAL);
    scrollPane.setFitToHeight(orientation == Orientation.HORIZONTAL);
  }

  /**
   * Creates a VBox with a label and tile pane representing a feature template group.
   *
   * @param featureTemplateGroup feature template group to show
   * @return VBox
   */
  private VBox createVBoxForFeatureTemplateGroup(FeatureTemplateGroup featureTemplateGroup) {
    VBox vBox = new VBox();
    // add a style class so the group can be styled via CSS
    vBox.getStyleClass().add("feature-template-group");
    vBox.setVisible(false); // wait until the group's feature layer is loaded before displaying
    vBox.managedProperty().bind(vBox.visibleProperty()); // hide the view when not visible

    // add a label to the view to show the group's feature layer's name
    Label label = new Label();
    label.managedProperty().bind(label.visibleProperty()); // hide the label when not visible
    vBox.getChildren().add(label);

    // add a tile pane to the view to display each feature template item in the group
    TilePane tilePane = new TilePane();
    tilePane.getStyleClass().add("tile-pane");
    // the tile pane's orientation should be the opposite of the control's orientation
    tilePane.setOrientation(getSkinnable().getOrientation() == Orientation.HORIZONTAL ? Orientation.VERTICAL :
            Orientation.HORIZONTAL);
    getSkinnable().orientationProperty().addListener((observable, oldValue, newValue) ->
      tilePane.setOrientation(newValue == Orientation.HORIZONTAL ? Orientation.VERTICAL : Orientation.HORIZONTAL)
    );
    // switch alignment to center when there is only one row/column, otherwise top-left.
    tilePane.widthProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue.doubleValue() < tilePane.getTileWidth() * 2) {
        tilePane.setAlignment(Pos.TOP_CENTER);
      } else {
        tilePane.setAlignment(Pos.TOP_LEFT);
      }
    });
    vBox.getChildren().add(tilePane);

    // ensure the group's feature layer is loaded to get its name for the label and its list of feature templates
    FeatureLayer featureLayer = featureTemplateGroup.getFeatureLayer();
    featureLayer.addDoneLoadingListener(() -> {
      if (featureLayer.getLoadStatus() == LoadStatus.LOADED) {
        vBox.setVisible(true);
        label.setText(featureLayer.getName());

        // add toggle buttons for each feature template item in the group
        featureTemplateGroup.getFeatureTemplateItems().stream()
            .map(this::createToggleButtonForFeatureTemplateItem)
            .forEach(toggleButton -> tilePane.getChildren().add(toggleButton));

        // add or remove tiles if the feature template item collection changes
        featureTemplateGroup.featureTemplateItemsProperty().addListener((ListChangeListener<FeatureTemplateItem>) c -> {
          while (c.next()) {
            if (c.wasAdded()) {
              // create toggle buttons for each new feature template item
              List<ToggleButton> added = c.getAddedSubList().stream()
                  .map(this::createToggleButtonForFeatureTemplateItem)
                  .collect(Collectors.toList());
              // add toggle buttons to tile pane
              tilePane.getChildren().addAll(c.getFrom(), added);
            } else if (c.wasRemoved()) {
              // remove tiles based on indices of removed items
              tilePane.getChildren().remove(c.getFrom(), c.getFrom() + c.getRemovedSize());
            }
          }
        });
      }
    });

    return vBox;
  }

  /**
   * Creates a ToggleButton to display the feature template item. The feature template's name will be displayed in
   * the toggle button's text and tooltip. The feature template's symbol swatch will be displayed in an image view
   * for the button's graphic.
   *
   * @param featureTemplateItem feature template item to show
   * @return ToggleButton
   */
  private ToggleButton createToggleButtonForFeatureTemplateItem(FeatureTemplateItem featureTemplateItem) {
    // show the feature template's name in the button text and tooltip
    ToggleButton toggleButton = new ToggleButton(featureTemplateItem.getFeatureTemplate().getName());
    toggleButton.setTooltip(new Tooltip(featureTemplateItem.getFeatureTemplate().getName()));
    // add a style class so the item can be styled via CSS
    toggleButton.getStyleClass().add("feature-template-item");
    // add the item to a shared toggle group so only one feature template item can be selected at a time
    toggleButton.setUserData(featureTemplateItem);
    toggleButton.setToggleGroup(toggleGroup);
    // bind the preferred width of the button based on the symbol size to limit overflowing template names
    toggleButton.prefWidthProperty().bind(getSkinnable().symbolSizeProperty().multiply(2));
    // use an image view for the button's graphic (to show the symbol swatch)
    ImageView imageView = new ImageView();
    toggleButton.setGraphic(imageView);
    // update the swatch shown in the image view when the button is first created
    updateSwatch(featureTemplateItem, imageView);
    return toggleButton;
  }

  /**
   * Updates the given image view with an image of the symbol swatch representing the feature template of the given
   * feature template item.
   *
   * @param featureTemplateItem feature template item
   * @param imageView image view to update
   *
   * @return completable future which completes when the image has been updated
   */
  private CompletableFuture<Void> updateSwatch(FeatureTemplateItem featureTemplateItem, ImageView imageView) {
    // create a completable future to return
    CompletableFuture<Void> updateFuture = new CompletableFuture<>();

    // create a graphic based on the feature template item's feature template
    final Graphic graphic = new Graphic();
    graphic.getAttributes().putAll(featureTemplateItem.getFeatureTemplate().getPrototypeAttributes());
    // get a symbol from the graphic based on the feature template item's feature layer's renderer
    final FeatureLayer featureLayer = featureTemplateItem.getFeatureLayer();
    Symbol symbol = featureLayer.getRenderer().getSymbol(graphic);
    // create a swatch of the symbol according to the control's symbol size property
    ListenableFuture<Image> swatch = symbol.createSwatchAsync(this.getSkinnable().getSymbolSize(),
        this.getSkinnable().getSymbolSize(), (float) Screen.getPrimary().getOutputScaleX(), 0x00);
    // update the image view's image with the swatch
    swatch.addDoneListener(() -> {
      try {
        // ensure the image view's width and height match the symbol size property
        imageView.setFitHeight(getSkinnable().getSymbolSize());
        imageView.setFitWidth(getSkinnable().getSymbolSize());
        imageView.setImage(swatch.get());
        updateFuture.complete(null);
      } catch (InterruptedException | ExecutionException | IllegalArgumentException e) {
        updateFuture.completeExceptionally(e);
      }
    });

    return updateFuture;
  }
}
