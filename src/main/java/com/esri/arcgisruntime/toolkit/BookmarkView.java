package com.esri.arcgisruntime.toolkit;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.Bookmark;
import com.esri.arcgisruntime.mapping.BookmarkList;
import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SceneView;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
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

  private final ReadOnlyObjectWrapper<GeoView> geoView;

  /**
   * Creates an instance for the GeoView.
   *
   * @param geoView A GeoView
   */
  public BookmarkView(GeoView geoView) {
    this.geoView = new ReadOnlyObjectWrapper<>(Objects.requireNonNull(geoView));

    // initialize the bookmarks property from the map or scene in the geo view
    final ObservableList<Bookmark> bookmarksInternal = FXCollections.observableArrayList();
    bookmarks = new ReadOnlyListWrapper<>(bookmarksInternal);
    if (geoView instanceof MapView) {
      ArcGISMap map = ((MapView) geoView).getMap();
      if (map != null) {
        map.addDoneLoadingListener(() -> {
          BookmarkList bookmarkList = map.getBookmarks();
          bookmarksInternal.addAll(bookmarkList);
          bookmarkList.addListChangedListener(listChangedEvent -> {
            bookmarksInternal.clear();
            bookmarksInternal.addAll(bookmarkList);
          });
        });
      } else {
        throw new IllegalStateException("Map cannot be null");
      }
    } else if (geoView instanceof SceneView) {
      ArcGISScene scene = ((SceneView) geoView).getArcGISScene();
      if (scene != null) {
        scene.addDoneLoadingListener(() -> {
          BookmarkList bookmarkList = scene.getBookmarks();
          bookmarksInternal.addAll(bookmarkList);
          bookmarkList.addListChangedListener(listChangedEvent -> {
            bookmarksInternal.clear();
            bookmarksInternal.addAll(bookmarkList);
          });
        });
      } else {
        throw new IllegalStateException("Scene cannot be null");
      }
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
    return bookmarksProperty().get();
  }

  /**
   * Gets the read-only bookmarks list property.
   * @return read-only bookmarks list property
   */
  public ReadOnlyListProperty<Bookmark> bookmarksProperty() {
    return bookmarks;
  }

}
