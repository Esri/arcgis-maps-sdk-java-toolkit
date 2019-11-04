package com.esri.arcgisruntime.toolkit.skins;

import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.layers.LayerContent;
import com.esri.arcgisruntime.toolkit.TableOfContents;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

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
        if (empty) {
          setText(null);
          setGraphic(null);
        } else {
          setText(item.getName());
        }
        // handle type-specific behavior
        if (item instanceof Layer) {
          ((Layer) item).loadAsync();
          ((Layer) item).addDoneLoadingListener(() -> setText(item.getName()));
        }
      }
    });

    // create an empty root node which will stay hidden
    TreeItem<LayerContent> root = new TreeItem<>();
    treeView.setRoot(root);
    treeView.setShowRoot(false);

    // add all of the layers from the control as child nodes
    control.getOperationalLayers().forEach(layer -> root.getChildren().add(new TreeItem<>(layer)));

  }

}
