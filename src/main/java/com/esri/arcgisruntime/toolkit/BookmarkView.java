package com.esri.arcgisruntime.toolkit;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.Bookmark;
import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.toolkit.utils.ListenableListUtils;
import javafx.beans.NamedArg;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;

import java.util.List;
import java.util.Objects;

/**
 * A control for viewing bookmarks from a map or scene. Selecting a bookmark sets the geoView's viewpoint to the
 * selected bookmark's viewpoint.
 */
public abstract class BookmarkView extends Control {

  private ListProperty<Bookmark> bookmarks;

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
      bookmarks = new SimpleListProperty<>(ListenableListUtils.toObservableList(map.getBookmarks()));
    } else {
      ArcGISScene scene = Objects.requireNonNull(((SceneView) geoView).getArcGISScene());
      bookmarks = new SimpleListProperty<>(ListenableListUtils.toObservableList(scene.getBookmarks()));
    }
  }

  public BookmarkView(@NamedArg("bookmarks") ObservableList<Bookmark> bookmarks) {
    this.geoView = new ReadOnlyObjectWrapper<>(null);
    this.bookmarks = new SimpleListProperty<>(bookmarks);
  }

  public BookmarkView(@NamedArg("bookmarks") List<Bookmark> bookmarks) {
    this(new SimpleListProperty<>(FXCollections.observableList(bookmarks)));
  }

  public BookmarkView() {
    this(new SimpleListProperty<>(FXCollections.observableArrayList()));
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

  public ListProperty<Bookmark> bookmarksProperty() {
    return bookmarks;
  }

  public void setBookmarks(ObservableList<Bookmark> bookmarks) {
    this.bookmarks.set(bookmarks);
  }
}