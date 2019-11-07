package com.esri.arcgisruntime.toolkit.skins;

import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.layers.LayerContent;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.toolkit.TableOfContents;
import javafx.scene.Node;
import javafx.scene.control.*;

/**
 * Skin for displaying a TableOfContents with a TreeView.
 */
public class TableOfContentsTreeViewSkin extends SkinBase<TableOfContents> {

  /**
   * Creates and skins the given TableOfContents control.
   *
   * @param control a table of contents
   */
  public TableOfContentsTreeViewSkin(TableOfContents control) {
    super(control);

    // show the table of contents as a TreeView
    TreeView<LayerContent> treeView = new TreeView<>();
    getChildren().add(treeView);

    // set a cell factory which displays the layer content's name
    treeView.setCellFactory(param -> new TreeCell<>() {

      @Override
      protected void updateItem(LayerContent item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
          setText(null);
          setGraphic(null);
        } else {
          CheckBox visibilityToggleCheckBox = new CheckBox();
          visibilityToggleCheckBox.setSelected(item.isVisible());
          visibilityToggleCheckBox.selectedProperty().addListener(o ->
              item.setVisible(visibilityToggleCheckBox.isSelected())
          );
          visibilityToggleCheckBox.setDisable(!item.canChangeVisibility());

          setText(item.getName());
          setGraphic(visibilityToggleCheckBox);

          // layer may need to be loaded before name is populated
          if (item instanceof Layer) {
            Layer layer = (Layer) item;
            layer.loadAsync();
            layer.addDoneLoadingListener(() -> setText(layer.getName()));
          }
        }
      }
    });

    // create an empty root node which will stay hidden
    TreeItem<LayerContent> root = new TreeItem<>();
    treeView.setRoot(root);
    treeView.setShowRoot(false);

    // add all of the layers from the control as child nodes
    control.getOperationalLayers().forEach(layer -> root.getChildren().add(new LayerContentTreeItem(layer)));

    if (control.getShowBasemapLayers()) {
      control.getBasemapLayers().forEach(layer -> root.getChildren().add(new LayerContentTreeItem(layer)));
    }

  }


  public static class LayerContentTreeItem extends TreeItem<LayerContent> {

    public LayerContentTreeItem(LayerContent value, Node graphic) {
      super(value, graphic);
      value.getSubLayerContents().forEach(layerContent ->
          getChildren().add(new TreeItem<>(layerContent))
      );
    }

    public LayerContentTreeItem(LayerContent value) {
      super(value);
      if (value instanceof Layer) {
        Layer layer = (Layer) value;
        layer.addDoneLoadingListener(() -> {
          if (layer.getLoadStatus() == LoadStatus.LOADED) {
            layer.getSubLayerContents().forEach(layerContent ->
                getChildren().add(new TreeItem<>(layerContent))
            );
          }
        });
      }
    }
  }

}
