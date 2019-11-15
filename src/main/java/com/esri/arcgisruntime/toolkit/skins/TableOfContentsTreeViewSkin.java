package com.esri.arcgisruntime.toolkit.skins;

import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.layers.LayerContent;
import com.esri.arcgisruntime.toolkit.TableOfContents;
import javafx.collections.ListChangeListener;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.stream.Collectors;

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
    treeView.setCellFactory(new TableOfContents.LayerContentTreeCellFactory());

    // create an empty root node which will stay hidden
    TreeItem<LayerContent> root = new TreeItem<>();
    treeView.setRoot(root);
    treeView.setShowRoot(false);

    // set up operational layers
    control.getOperationalLayers().forEach(layer -> {
      TreeItem<LayerContent> item = new TableOfContents.LayerContentTreeItem(layer);
      root.getChildren().add(item);
      layer.addLoadStatusChangedListener(loadStatusChangedEvent -> {
        // replace the item in the tree view whenever it's load status changes to display the status accordingly
        layer.addDoneLoadingListener(() ->
            root.getChildren().replaceAll(layerContentTreeItem ->
                layerContentTreeItem.equals(item) ? item : layerContentTreeItem
            )
        );
      });
    });

    control.getOperationalLayers().addListener((ListChangeListener<Layer>) c -> {
      while (c.next()) {
        int insertionIndex = control.getReferenceLayers().size();
        if (c.wasAdded()) {
          root.getChildren().addAll(insertionIndex + c.getFrom(),
              c.getAddedSubList().stream().map(TableOfContents.LayerContentTreeItem::new).collect(Collectors.toList()));
        } else if (c.wasRemoved()) {
          root.getChildren().remove(insertionIndex, insertionIndex + c.getRemovedSize());
        }
      }
    });

    control.operationalLayersProperty().addListener((observable, oldValue, newValue) -> {
      int insertionIndex = control.getReferenceLayers().size();
      root.getChildren().remove(insertionIndex, insertionIndex + oldValue.size());
      root.getChildren().addAll(insertionIndex,
          newValue.stream().map(TableOfContents.LayerContentTreeItem::new).collect(Collectors.toList()));
    });

    // set up base layers to show last
    control.getBaseLayers().forEach(layer -> {
      TreeItem<LayerContent> item = new TableOfContents.LayerContentTreeItem(layer);
      root.getChildren().add(item);
      layer.addLoadStatusChangedListener(loadStatusChangedEvent -> {
        // replace the item in the tree view whenever it's load status changes to display the status accordingly
        layer.addDoneLoadingListener(() ->
          root.getChildren().replaceAll(layerContentTreeItem ->
              layerContentTreeItem.equals(item) ? item : layerContentTreeItem
          )
        );
      });
    });

    control.getBaseLayers().addListener((ListChangeListener<Layer>) c -> {
      while (c.next()) {
        int insertionIndex = control.getOperationalLayers().size() + control.getReferenceLayers().size();
        if (c.wasAdded()) {
          root.getChildren().addAll(insertionIndex + c.getFrom(),
              c.getAddedSubList().stream().map(TableOfContents.LayerContentTreeItem::new).collect(Collectors.toList()));
        } else if (c.wasRemoved()) {
          root.getChildren().remove(insertionIndex + c.getFrom(), insertionIndex + c.getTo());
        }
      }
    });

    control.baseLayersProperty().addListener((observable, oldValue, newValue) -> {
      int insertionIndex = control.getOperationalLayers().size() + control.getReferenceLayers().size();
      root.getChildren().remove(insertionIndex, insertionIndex + oldValue.size());
      root.getChildren().addAll(insertionIndex,
          newValue.stream().map(TableOfContents.LayerContentTreeItem::new).collect(Collectors.toList()));
    });

  }

}
