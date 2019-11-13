package com.esri.arcgisruntime.toolkit;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.Bookmark;
import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.toolkit.utils.ListenableListUtils;
import javafx.beans.NamedArg;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;

import java.util.Objects;

/**
 * A control for viewing bookmarks from a map or scene. Selecting a bookmark sets the geoView's viewpoint to the
 * selected bookmark's viewpoint.
 */
public abstract class BookmarkView extends Control {

  private final ReadOnlyListWrapper<Bookmark> bookmarks;

  private final ObjectProperty<GeoView> geoView;

  /**
   * Creates an instance for the GeoView.
   *
   * @param geoView A GeoView
   * @throws NullPointerException if the geo view's map/scene is null
   */
  public BookmarkView(@NamedArg("geoView") GeoView geoView) {
    this.geoView = new SimpleObjectProperty<>(geoView);
    this.bookmarks = new ReadOnlyListWrapper<>();
    // initialize the bookmarks from the geoView
    bindBookmarks(geoView);
    // reset the bookmarks if the geoView changes
    this.geoView.addListener(o -> bindBookmarks(geoView));
  }

  /**
   * Updates the bookmarks based on the current GeoView.
   */
  private void bindBookmarks(GeoView geoView) {
    if (geoView instanceof MapView) {
      ArcGISMap map = Objects.requireNonNull(((MapView) geoView).getMap());
      bookmarks.set(ListenableListUtils.toObservableList(map.getBookmarks()));
    } else if (geoView instanceof SceneView) {
      ArcGISScene scene = Objects.requireNonNull(((SceneView) geoView).getArcGISScene());
      bookmarks.set(ListenableListUtils.toObservableList(scene.getBookmarks()));
    } else {
      bookmarks.set(FXCollections.observableArrayList());
    }
  }

  /**
   * Gets the geo view used to create the control.
   *
   * @return geoView used to create the control
   */
  public GeoView getGeoView() {
    return geoViewProperty().get();
  }

  /**
   * The geo view which has the bookmarks in its map or scene.
   *
   * @return geoView property
   */
  public ObjectProperty<GeoView> geoViewProperty() {
    return geoView;
  }

  /**
   * Sets the geo view.
   */
  public void setGeoView(GeoView geoView) {
    this.geoView.set(geoView);
  }

  /**
   * Gets the bookmarks in the list.
   *
   * @return bookmarks in the list
   */
  public ObservableList<Bookmark> getBookmarks() {
    return bookmarks;
  }

  /**
   * The bookmarks being displayed.
   *
   * @return bookmarks property
   */
  public ReadOnlyListProperty<Bookmark> bookmarksProperty() {
    return bookmarks.getReadOnlyProperty();
  }
}
