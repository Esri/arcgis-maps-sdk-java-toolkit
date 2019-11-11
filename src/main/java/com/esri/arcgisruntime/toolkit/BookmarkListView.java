package com.esri.arcgisruntime.toolkit;

import com.esri.arcgisruntime.mapping.Bookmark;
import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.toolkit.skins.BookmarksListSkin;
import javafx.beans.NamedArg;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.util.Callback;

import java.util.List;

public class BookmarkListView extends BookmarkView {

  private final ObjectProperty<Callback<ListView<Bookmark>, ListCell<Bookmark>>> cellFactory;

  /**
   * Creates an instance in which the bookmarks are bound to a GeoView's map or scene.
   *
   * @param geoView A GeoView
   */
  public BookmarkListView(@NamedArg("geoView") GeoView geoView) {
    super(geoView);
    cellFactory = new SimpleObjectProperty<>(param -> new BookmarkListCell());
  }

  public BookmarkListView(@NamedArg("bookmarks") ObservableList<Bookmark> bookmarks) {
    super(bookmarks);
    cellFactory = new SimpleObjectProperty<>(param -> new BookmarkListCell());
  }

  public BookmarkListView(@NamedArg("bookmarks") List<Bookmark> bookmarks) {
    super(bookmarks);
    cellFactory = new SimpleObjectProperty<>(param -> new BookmarkListCell());
  }

  public BookmarkListView() {
    super();
    cellFactory = new SimpleObjectProperty<>(param -> new BookmarkListCell());
  }

  @Override
  protected Skin<?> createDefaultSkin() {
    return new BookmarksListSkin(this);
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

  public static class BookmarkListCell extends ListCell<Bookmark> {

    @Override
    protected void updateItem(Bookmark item, boolean empty) {
      super.updateItem(item, empty);
      setText(empty ? null : item.getName());
      setGraphic(null);
    }
  }
}
