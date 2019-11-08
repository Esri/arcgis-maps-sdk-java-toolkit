package com.esri.arcgisruntime.toolkit;

import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.LayerList;
import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.toolkit.skins.TableOfContentsTreeViewSkin;
import com.esri.arcgisruntime.toolkit.utils.ListenableListUtils;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

import java.util.Objects;

/**
 * Control for showing and interacting with the layers of a GeoView's map or scene.
 */
public class TableOfContents extends Control {

  private final ReadOnlyListWrapper<Layer> operationalLayers;
  private final ReadOnlyListWrapper<Layer> baseLayers;
  private final ReadOnlyListWrapper<Layer> referenceLayers;

  private ReadOnlyObjectWrapper<GeoView> geoView;

  /**
   * Creates a table of contents view.
   */
  public TableOfContents(GeoView geoView) {
    this.geoView = new ReadOnlyObjectWrapper<>(Objects.requireNonNull(geoView));

    // initialize the layerContents property from the map or scene in the geo view
    if (geoView instanceof MapView) {
      ArcGISMap map = Objects.requireNonNull(((MapView) geoView).getMap());
      baseLayers = new ReadOnlyListWrapper<>(ListenableListUtils.toObservableList(map.getBasemap().getBaseLayers()));
      operationalLayers = new ReadOnlyListWrapper<>(ListenableListUtils.toObservableList(map.getOperationalLayers()));
      referenceLayers = new ReadOnlyListWrapper<>(ListenableListUtils.toObservableList(map.getBasemap().getReferenceLayers()));
      map.loadAsync();
    } else {
      ArcGISScene scene = Objects.requireNonNull(((SceneView) geoView).getArcGISScene());
      baseLayers = new ReadOnlyListWrapper<>(ListenableListUtils.toObservableList(scene.getBasemap().getBaseLayers()));
      operationalLayers = new ReadOnlyListWrapper<>(ListenableListUtils.toObservableList(scene.getOperationalLayers()));
      referenceLayers = new ReadOnlyListWrapper<>(ListenableListUtils.toObservableList(scene.getBasemap().getReferenceLayers()));
      scene.loadAsync();
    }
  }

  @Override
  protected Skin<?> createDefaultSkin() {
    return new TableOfContentsTreeViewSkin(this);
  }

  public ObservableList<Layer> getOperationalLayers() {
    return operationalLayers.get();
  }

  public ReadOnlyListWrapper<Layer> operationalLayersProperty() {
    return operationalLayers;
  }

  public ObservableList<Layer> getBaseLayers() {
    return baseLayers.get();
  }

  public ReadOnlyListWrapper<Layer> baseLayersProperty() {
    return baseLayers;
  }

  public ObservableList<Layer> getReferenceLayers() {
    return referenceLayers.get();
  }

  public ReadOnlyListWrapper<Layer> referenceLayersProperty() {
    return referenceLayers;
  }

  public GeoView getGeoView() {
    return geoView.get();
  }

  public ReadOnlyObjectWrapper<GeoView> geoViewProperty() {
    return geoView;
  }
}
