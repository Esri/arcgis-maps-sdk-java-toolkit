package com.esri.arcgisruntime.toolkit;

import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.toolkit.skins.TableOfContentsTreeViewSkin;
import com.esri.arcgisruntime.toolkit.utils.ListenableListUtils;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

import java.util.Objects;

/**
 * Control for showing and interacting with the layers of a GeoView's map or scene.
 */
public class TableOfContents extends Control {

  private final ReadOnlyListWrapper<Layer> operationalLayers;

  private final ReadOnlyListWrapper<Layer> basemapLayers;

  private ReadOnlyObjectWrapper<GeoView> geoView;

  private SimpleBooleanProperty showBasemapLayers = new SimpleBooleanProperty(false);

  /**
   * Creates a table of contents for the given GeoView.
   *
   * @param geoView a GeoView
   */
  public TableOfContents(GeoView geoView) {
    this.geoView = new ReadOnlyObjectWrapper<>(Objects.requireNonNull(geoView));

    // initialize the layerContents property from the map or scene in the geo view
    if (geoView instanceof MapView) {
      ArcGISMap map = Objects.requireNonNull(((MapView) geoView).getMap());
      basemapLayers = new ReadOnlyListWrapper<>(ListenableListUtils.toObservableList(map.getBasemap().getBaseLayers()));
      operationalLayers = new ReadOnlyListWrapper<>(ListenableListUtils.toObservableList(map.getOperationalLayers()));
      map.loadAsync();
    } else {
      ArcGISScene scene = Objects.requireNonNull(((SceneView) geoView).getArcGISScene());
      basemapLayers = new ReadOnlyListWrapper<>(ListenableListUtils.toObservableList(scene.getBasemap().getBaseLayers()));
      operationalLayers = new ReadOnlyListWrapper<>(ListenableListUtils.toObservableList(scene.getOperationalLayers()));
      scene.loadAsync();
    }
  }

  @Override
  protected Skin<?> createDefaultSkin() {
    return new TableOfContentsTreeViewSkin(this);
  }

  /**
   * Gets the GeoView associated with this table of contents.
   *
   * @return geo view
   */
  public GeoView getGeoView() {
    return geoViewProperty().get();
  }

  /**
   * The GeoView associated with this table of contents.
   *
   * @return geo view property
   */
  public ReadOnlyObjectProperty<GeoView> geoViewProperty() {
    return this.geoView.getReadOnlyProperty();
  }

  public ObservableList<Layer> getOperationalLayers() {
    return operationalLayers.get();
  }

  public ReadOnlyListWrapper<Layer> operationalLayersProperty() {
    return operationalLayers;
  }

  public boolean getShowBasemapLayers() {
    return showBasemapLayers.get();
  }

  public SimpleBooleanProperty showBasemapLayersProperty() {
    return showBasemapLayers;
  }

  public void setShowBasemapLayers(boolean showBasemapLayers) {
    this.showBasemapLayers.set(showBasemapLayers);
  }

  public ObservableList<Layer> getBasemapLayers() {
    return basemapLayers.get();
  }

  public ReadOnlyListWrapper<Layer> basemapLayersProperty() {
    return basemapLayers;
  }

  public void setBasemapLayers(ObservableList<Layer> basemapLayers) {
    this.basemapLayers.set(basemapLayers);
  }
}
