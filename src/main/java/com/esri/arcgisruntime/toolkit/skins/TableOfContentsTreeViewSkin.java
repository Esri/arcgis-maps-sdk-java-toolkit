package com.esri.arcgisruntime.toolkit.skins;

import com.esri.arcgisruntime.layers.LayerContent;
import com.esri.arcgisruntime.toolkit.TableOfContents;
import javafx.scene.control.SkinBase;
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

    TreeView<LayerContent> treeView = new TreeView<>();
    getChildren().add(treeView);

    TreeItem<LayerContent> root = new TreeItem<>();
    treeView.setRoot(root);
    treeView.setShowRoot(false);
  }

}
