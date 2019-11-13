package com.esri.arcgisruntime.toolkit;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.Bookmark;
import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.toolkit.utils.ListenableListUtils;
import javafx.beans.NamedArg;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;

import java.util.Objects;

/**
 * A control for viewing bookmarks from a map or scene. Selecting a bookmark sets the geoView's viewpoint to the
 * selected bookmark's viewpoint.
 */
public abstract class BookmarkView extends Control {

  private final ReadOnlyListProperty<Bookmark> bookmarks;

  private final ReadOnlyObjectWrapper<GeoView> geoView;

  /**
   * Creates an instance for the GeoView.
   *
   * @param geoView A GeoView
   */
  public BookmarkView(@NamedArg("geoView") GeoView geoView) {
    this.geoView = new ReadOnlyObjectWrapper<>(Objects.requireNonNull(geoView));

    // initialize the bookmarks property from the map or scene in the geo view
    if (geoView instanceof MapView) {
      ArcGISMap map = Objects.requireNonNull(((MapView) geoView).getMap());
      bookmarks = new ReadOnlyListWrapper<>(ListenableListUtils.toObservableList(map.getBookmarks()));
    } else {
      ArcGISScene scene = Objects.requireNonNull(((SceneView) geoView).getArcGISScene());
      bookmarks = new ReadOnlyListWrapper<>(ListenableListUtils.toObservableList(scene.getBookmarks()));
    }
  }

  /**
   * Gets the GeoView used to create the control.
   *
   * @return geoView used to create the control
   */
  public GeoView getGeoView() {
    return geoViewProperty().get();
  }

  /**
   * The GeoView used to create the control.
   *
   * @return geoView property
   */
  public ReadOnlyObjectProperty<GeoView> geoViewProperty() {
    return geoView.getReadOnlyProperty();
  }

  /**
   * Gets the bookmarks in the list.
   *
   * @return bookmarks in the list
   */
  public ObservableList<Bookmark> getBookmarks() {
    return bookmarks;
  }

  public ReadOnlyListProperty<Bookmark> bookmarksProperty() {
    return bookmarks;
  }
}
