package com.esri.arcgisruntime.toolkit;

import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.layers.LayerContent;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.loadable.Loadable;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.toolkit.skins.TableOfContentsTreeViewSkin;
import com.esri.arcgisruntime.toolkit.utils.ListenableListUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.util.Objects;

/**
 * Control for showing and interacting with the layers of a GeoView's map or scene.
 */
public class TableOfContents extends Control {

  private final ReadOnlyListWrapper<Layer> operationalLayers;
  private final ReadOnlyListWrapper<Layer> baseLayers;
  private final ReadOnlyListWrapper<Layer> referenceLayers;

  private ObjectProperty<GeoView> geoView;

  /**
   * Creates a table of contents view.
   */
  public TableOfContents(GeoView geoView) {
    this.geoView = new SimpleObjectProperty<>(Objects.requireNonNull(geoView));
    this.baseLayers = new ReadOnlyListWrapper<>(FXCollections.observableArrayList());
    this.operationalLayers = new ReadOnlyListWrapper<>(FXCollections.observableArrayList());
    this.referenceLayers = new ReadOnlyListWrapper<>(FXCollections.observableArrayList());
    // initialize the layer contents from the geoView
    bindLayerContents(geoView);
    // reset the layer contents if the geoView changes
    this.geoView.addListener(o -> bindLayerContents(this.getGeoView()));
  }

  private void bindLayerContents(GeoView geoView) {
    if (geoView instanceof MapView) {
      ArcGISMap map = Objects.requireNonNull(((MapView) geoView).getMap());
      baseLayers.set(ListenableListUtils.toObservableList(map.getBasemap().getBaseLayers()));
      operationalLayers.set(ListenableListUtils.toObservableList(map.getOperationalLayers()));
      referenceLayers.set(ListenableListUtils.toObservableList(map.getBasemap().getReferenceLayers()));
      map.loadAsync();
    } else if (geoView instanceof SceneView) {
      ArcGISScene scene = Objects.requireNonNull(((SceneView) geoView).getArcGISScene());
      baseLayers.set(ListenableListUtils.toObservableList(scene.getBasemap().getBaseLayers()));
      operationalLayers.set(ListenableListUtils.toObservableList(scene.getOperationalLayers()));
      referenceLayers.set(ListenableListUtils.toObservableList(scene.getBasemap().getReferenceLayers()));
      scene.loadAsync();
    } else {
      baseLayers.set(FXCollections.observableArrayList());
      operationalLayers.set(FXCollections.observableArrayList());
      referenceLayers.set(FXCollections.observableArrayList());
    }
  }

  @Override
  protected Skin<?> createDefaultSkin() {
    return new TableOfContentsTreeViewSkin(this);
  }

  public ObservableList<Layer> getOperationalLayers() {
    return operationalLayersProperty().get();
  }

  public ReadOnlyListProperty<Layer> operationalLayersProperty() {
    return operationalLayers.getReadOnlyProperty();
  }

  public ObservableList<Layer> getBaseLayers() {
    return baseLayersProperty().get();
  }

  public ReadOnlyListProperty<Layer> baseLayersProperty() {
    return baseLayers.getReadOnlyProperty();
  }

  public ObservableList<Layer> getReferenceLayers() {
    return referenceLayersProperty().get();
  }

  public ReadOnlyListProperty<Layer> referenceLayersProperty() {
    return referenceLayers.getReadOnlyProperty();
  }

  public GeoView getGeoView() {
    return geoViewProperty().get();
  }

  public ObjectProperty<GeoView> geoViewProperty() {
    return geoView;
  }

  public void setGeoView(GeoView geoView) {
    geoViewProperty().set(geoView);
  }

  public static class LayerContentTreeCell extends TreeCell<LayerContent> {
    @Override
    protected void updateItem(LayerContent item, boolean empty) {
      super.updateItem(item, empty);
      if (empty) {
        setText(null);
        setGraphic(null);
      } else {
        setText(item.getName());

        HBox graphic = new HBox(5);
        graphic.setAlignment(Pos.CENTER_LEFT);
        setGraphic(graphic);

        if (item.canChangeVisibility()) {
          CheckBox visibilityToggleCheckBox = new CheckBox();
          visibilityToggleCheckBox.setSelected(item.isVisible());
          visibilityToggleCheckBox.selectedProperty().addListener(o ->
              item.setVisible(visibilityToggleCheckBox.isSelected())
          );
          graphic.getChildren().add(visibilityToggleCheckBox);
        }

        if (item instanceof Loadable && ((Loadable) item).getLoadStatus() == LoadStatus.LOADING) {
          ProgressIndicator progressIndicator = new ProgressIndicator();
          progressIndicator.setMaxSize(12, 12);
          progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
          graphic.getChildren().add(progressIndicator);
        }
      }
    }
  }

  public static class LayerContentTreeCellFactory implements Callback<TreeView<LayerContent>, TreeCell<LayerContent>> {

    @Override
    public TreeCell<LayerContent> call(TreeView<LayerContent> param) {
      return new LayerContentTreeCell();
    }
  }

  public static class LayerContentTreeItem extends TreeItem<LayerContent> {

    public LayerContentTreeItem(LayerContent value, Node graphic) {
      super(value, graphic);
      value.getSubLayerContents().forEach(layerContent ->
          getChildren().add(new LayerContentTreeItem(layerContent))
      );
    }

    public LayerContentTreeItem(LayerContent value) {
      super(value);
      if (value instanceof Layer) {
        Layer layer = (Layer) value;
        layer.loadAsync();
        layer.addDoneLoadingListener(() -> {
          if (layer.getLoadStatus() == LoadStatus.LOADED) {
            layer.getSubLayerContents().forEach(layerContent ->
                getChildren().add(new LayerContentTreeItem(layerContent))
            );
          }
        });
      }
    }
  }
}
