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
import com.esri.arcgisruntime.mapping.BookmarkList;
import com.esri.arcgisruntime.toolkit.skins.BookmarkListViewSkin;
import com.esri.arcgisruntime.toolkit.utils.ListenableListUtils;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

import java.util.List;

/**
 * Control for displaying a list of bookmarks.
 */
public class BookmarksView extends Control {

  /**
   * List of bookmarks to show in the view.
   */
  private final ListProperty<Bookmark> bookmarks;

  /**
   * Selected bookmark.
   */
  private final ObjectProperty<Bookmark> selectedBookmark;

  /**
   * Creates a BookmarksView showing the given list of bookmarks.
   */
  public BookmarksView(ObservableList<Bookmark> bookmarks) {
    this.bookmarks = new SimpleListProperty<>(bookmarks);
    this.selectedBookmark = new SimpleObjectProperty<>(null);
  }

  /**
   * Creates a BookmarksView showing the given bookmark list.
   *
   * @param bookmarkList bookmark list
   */
  public BookmarksView(BookmarkList bookmarkList) {
    this.bookmarks = new SimpleListProperty<>(ListenableListUtils.toObservableList(bookmarkList));
    this.selectedBookmark = new SimpleObjectProperty<>(null);
  }

  /**
   * Creates a BookmarksView showing the given list of bookmarks.
   *
   * @param bookmarks bookmarks to show
   */
  public BookmarksView(List<Bookmark> bookmarks) {
    this(FXCollections.observableArrayList(bookmarks));
  }

  /**
   * Creates a BookmarksView with an empty list of bookmarks.
   */
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
  public ListProperty<Bookmark> bookmarksProperty() {
    return bookmarks;
  }

  /**
   * Sets the list of bookmarks to show.
   *
   * @param bookmarks list of bookmarks
   */
  public void setBookmarks(ObservableList<Bookmark> bookmarks) {
    this.bookmarks.set(bookmarks);
  }

  /**
   * Sets the list of bookmarks to show from a BookmarkList.
   *
   * @param bookmarkList bookmark list
   * @see BookmarkList
   */
  public void setBookmarks(BookmarkList bookmarkList) {
    this.bookmarks.set(ListenableListUtils.toObservableList(bookmarkList));
  }

  /**
   * Gets the selected bookmark.
   *
   * @return selected bookmark
   */
  public Bookmark getSelectedBookmark() {
    return selectedBookmark.get();
  }

  /**
   * The selected bookmark.
   *
   * @return selected bookmark property
   */
  public ObjectProperty<Bookmark> selectedBookmarkProperty() {
    return selectedBookmark;
  }

  /**
   * Sets the selected bookmark.
   *
   * @param selectedBookmark bookmark to select
   */
  public void setSelectedBookmark(Bookmark selectedBookmark) {
    this.selectedBookmark.set(selectedBookmark);
  }
}
