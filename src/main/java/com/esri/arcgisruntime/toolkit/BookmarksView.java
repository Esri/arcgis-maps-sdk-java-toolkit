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

import com.esri.arcgisruntime.mapping.Bookmark;
import com.esri.arcgisruntime.toolkit.skins.BookmarkListViewSkin;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

import java.util.List;

/**
 * Control for displaying the bookmarks of a GeoView's map or scenes in a list.
 */
public class BookmarksView extends Control {

  private final ListProperty<Bookmark> bookmarks;
  private final ObjectProperty<Bookmark> selectedBookmark;

  /**
   * Creates an instance for the GeoView.
   */
  public BookmarksView(ObservableList<Bookmark> bookmarks) {
    this.bookmarks = new SimpleListProperty<>(bookmarks);
    this.selectedBookmark = new SimpleObjectProperty<>(null);
  }

  public BookmarksView(List<Bookmark> bookmarks) {
    this(FXCollections.observableArrayList(bookmarks));
  }

  public BookmarksView() {
    this(FXCollections.observableArrayList());
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
    return bookmarks;
  }

  public void setBookmarks(ObservableList<Bookmark> bookmarks) {
    this.bookmarks.set(bookmarks);
  }

  public Bookmark getSelectedBookmark() {
    return selectedBookmark.get();
  }

  public ObjectProperty<Bookmark> selectedBookmarkProperty() {
    return selectedBookmark;
  }

  public void setSelectedBookmark(Bookmark selectedBookmark) {
    this.selectedBookmark.set(selectedBookmark);
  }
}
