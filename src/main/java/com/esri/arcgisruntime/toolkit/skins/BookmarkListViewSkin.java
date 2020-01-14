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

package com.esri.arcgisruntime.toolkit.skins;

import com.esri.arcgisruntime.mapping.Bookmark;
import com.esri.arcgisruntime.toolkit.BookmarksView;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SkinBase;
import javafx.util.Callback;

/**
 * A Skin to display the BookmarkListView as a ListView. Selecting a bookmark will move the GeoView to its viewpoint.
 * Navigating away from the selected bookmark's viewpoint will deselect it in the list.
 */
public class BookmarkListViewSkin extends SkinBase<BookmarksView> {

  private final ListView<Bookmark> listView;

  /**
   * Creates and applies the skin to the given control.
   *
   * @param control bookmarks list control to skin
   */
  public BookmarkListViewSkin(BookmarksView control) {
    super(control);

    // show the control as a list view
    listView = new ListView<>();
    getChildren().add(listView);

    // bind the items from the control
    listView.itemsProperty().bind(control.bookmarksProperty());

    // default to showing the bookmark's name
    listView.setCellFactory(new BookmarkListCellFactory());

    // change the selection on the list view if the control property changes
    control.selectedBookmarkProperty().addListener(((observable, oldValue, newValue) -> {
      if (newValue == null) {
        listView.getSelectionModel().clearSelection();
      } else {
        listView.getSelectionModel().select(newValue);
      }
    }));

    // update the control property if an item is selected
    listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
        control.setSelectedBookmark(newValue)
    );

  }

  /**
   * Gets the cell factory callback used to display the cells in the list. Defaults to showing the name of the bookmark.
   *
   * @return cell factory callback
   */
  public Callback<ListView<Bookmark>, ListCell<Bookmark>> getCellFactory() {
    return cellFactoryProperty().get();
  }

  /**
   * The cell factory callback used to display the cells in the list. Defaults to showing the name of the bookmark.
   *
   * @return cell factory callback property
   */
  public ObjectProperty<Callback<ListView<Bookmark>, ListCell<Bookmark>>> cellFactoryProperty() {
    return listView.cellFactoryProperty();
  }

  /**
   * Sets the cell factory callback used to display the cells in the list.
   *
   * @param cellFactory cell factory callback
   */
  public void setCellFactory(Callback<ListView<Bookmark>, ListCell<Bookmark>> cellFactory) {
    cellFactoryProperty().set(cellFactory);
  }

  /**
   * A simple ListCell for Bookmarks to show the bookmark's name.
   */
  private static class BookmarkListCell extends ListCell<Bookmark> {

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
  private static class BookmarkListCellFactory implements Callback<ListView<Bookmark>, ListCell<Bookmark>> {

    @Override
    public ListCell<Bookmark> call(ListView<Bookmark> param) {
      return new BookmarkListCell();
    }
  }

}
