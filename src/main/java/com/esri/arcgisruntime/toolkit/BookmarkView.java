package com.esri.arcgisruntime.toolkit;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.Bookmark;
import com.esri.arcgisruntime.mapping.BookmarkList;
import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.toolkit.skins.BookmarksListSkin;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

import java.util.Objects;

/**
 * A control for viewing bookmarks from a map or scene. Selecting a bookmark sets the geoView's viewpoint to the
 * selected bookmark's viewpoint.
 */
public class BookmarkView extends Control {

  private final ReadOnlyListWrapper<Bookmark> bookmarks;

  private final GeoView geoView;

  private final ObjectProperty<EventHandler<BookmarkSelectedEvent>> onBookmarkSelected;

  /**
   * Creates an instance for the GeoView.
   *
   * @param geoView A GeoView
   */
  public BookmarkView(GeoView geoView) {
    this.geoView = Objects.requireNonNull(geoView);

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

    // set this as null and leave it up to the skin
    onBookmarkSelected = new SimpleObjectProperty<>();
  }

  @Override
  protected Skin<?> createDefaultSkin() {
    return new BookmarksListSkin(this);
  }

  /**
   * Get the GeoView used to create the control.
   *
   * @return geoView used to create the control
   */
  public GeoView getGeoView() {
    return geoView;
  }

  /**
   * Gets the bookmarks in the list.
   *
   * @return bookmarks in the list
   */
  public ObservableList<Bookmark> getBookmarks() {
    return bookmarks.getReadOnlyProperty().get();
  }

  /**
   * Gets the read-only bookmarks list property.
   * @return read-only bookmarks list property
   */
  public ReadOnlyListProperty<Bookmark> bookmarksProperty() {
    return bookmarks;
  }

  /**
   * Returns the EventHandler called when a bookmark is selected. Defaults to null, in which case the geoView is
   * navigated to the selected bookmark, and then the bookmark is deselected.
   *
   * @return event handler called when a bookmark is selected
   */
  public EventHandler<BookmarkSelectedEvent> getOnBookmarkSelected() {
    return onBookmarkSelected.get();
  }

  /**
   * Called when a bookmark is selected. Defaults to null, in which case the geoView is navigated to the selected
   * bookmark, and then the bookmark is deselected.
   *
   * @return event handler property
   */
  public ObjectProperty<EventHandler<BookmarkSelectedEvent>> onBookmarkSelectedProperty() {
    return onBookmarkSelected;
  }

  /**
   * Sets the EventHandler called when a bookmark is selected.
   *
   * @param onBookmarkSelected the event handler
   */
  public void setOnBookmarkSelected(EventHandler<BookmarkSelectedEvent> onBookmarkSelected) {
    this.onBookmarkSelected.set(onBookmarkSelected);
  }

  /**
   * An Event created when a bookmark is selected from the list.
   */
  public static class BookmarkSelectedEvent extends Event {

    private Bookmark selectedBookmark;

    /**
     * Creates an event based on a selected bookmark.
     *
     * @param selectedBookmark the selected bookmark which triggered the event
     */
    public BookmarkSelectedEvent(Bookmark selectedBookmark) {
      super(ANY);
      this.selectedBookmark = selectedBookmark;
    }

    /**
     * Gets the selected bookmark which triggered the event.
     *
     * @return selected bookmark
     */
    public Bookmark getSelectedBookmark() {
      return selectedBookmark;
    }
  }
}
