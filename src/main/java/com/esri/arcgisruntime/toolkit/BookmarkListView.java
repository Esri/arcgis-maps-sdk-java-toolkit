/*
 * Copyright 2019 Esri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.esri.arcgisruntime.toolkit;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.Bookmark;
import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.toolkit.skins.BookmarkListViewSkin;
import com.esri.arcgisruntime.toolkit.utils.ListenableListUtils;
import javafx.beans.NamedArg;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.util.Callback;

import java.util.Objects;

/**
 * Control for displaying the bookmarks of a GeoView's map or scenes in a list.
 */
public class BookmarkListView extends Control {

  private final ReadOnlyListWrapper<Bookmark> bookmarks;
  private final ObjectProperty<Callback<ListView<Bookmark>, ListCell<Bookmark>>> cellFactory;
  private final ObjectProperty<GeoView> geoView;

  /**
   * Creates an instance for the GeoView.
   *
   * @param geoView A GeoView
   * @throws NullPointerException if the geo view's map/scene is null
   */
  public BookmarkListView(@NamedArg("geoView") GeoView geoView) {
    this.geoView = new SimpleObjectProperty<>(geoView);
    this.bookmarks = new ReadOnlyListWrapper<>();
    // initialize the bookmarks from the geoView
    bindBookmarks(geoView);
    // reset the bookmarks if the geoView changes
    this.geoView.addListener(o -> bindBookmarks(geoView));
    cellFactory = new SimpleObjectProperty<>(new BookmarkListCellFactory());
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

  @Override
  protected Skin<?> createDefaultSkin() {
    return new BookmarkListViewSkin(this);
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
   * The bookmarks being displayed.
   *
   * @return bookmarks property
   */
  public ReadOnlyListProperty<Bookmark> bookmarksProperty() {
    return bookmarks.getReadOnlyProperty();
  }

  /**
   * Gets the cell factory callback used to display the cells in the list. Defaults to showing the name of the bookmark.
   *
   * @return cell factory callback
   */
  public Callback<ListView<Bookmark>, ListCell<Bookmark>> getCellFactory() {
    return cellFactory.get();
  }

  /**
   * The cell factory callback used to display the cells in the list. Defaults to showing the name of the bookmark.
   *
   * @return cell factory callback property
   */
  public ObjectProperty<Callback<ListView<Bookmark>, ListCell<Bookmark>>> cellFactoryProperty() {
    return cellFactory;
  }

  /**
   * Sets the cell factory callback used to display the cells in the list.
   *
   * @param cellFactory cell factory callback
   */
  public void setCellFactory(Callback<ListView<Bookmark>, ListCell<Bookmark>> cellFactory) {
    this.cellFactory.set(cellFactory);
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
    geoViewProperty().set(geoView);
  }

  /**
   * A simple ListCell for Bookmarks to show the bookmark's name.
   */
  public static class BookmarkListCell extends ListCell<Bookmark> {

    @Override
    protected void updateItem(Bookmark item, boolean empty) {
      super.updateItem(item, empty);
      setText(empty ? null : item.getName());
      setGraphic(null);
    }
  }

  /**
   * Cell factory using BookmarkListCells.
   */
  public static class BookmarkListCellFactory implements Callback<ListView<Bookmark>, ListCell<Bookmark>> {

    @Override
    public ListCell<Bookmark> call(ListView<Bookmark> param) {
      return new BookmarkListCell();
    }
  }
}
