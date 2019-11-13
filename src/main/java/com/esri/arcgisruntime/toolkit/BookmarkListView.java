package com.esri.arcgisruntime.toolkit;

import com.esri.arcgisruntime.mapping.Bookmark;
import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.toolkit.skins.BookmarkListViewSkin;
import javafx.beans.NamedArg;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.util.Callback;

/**
 * Control for displaying the bookmarks of a GeoView's map or scenes in a list.
 */
public class BookmarkListView extends BookmarkView {

  private final ObjectProperty<Callback<ListView<Bookmark>, ListCell<Bookmark>>> cellFactory;

  /**
   * Creates an instance using the bookmarks of the given GeoView's map or scene.
   *
   * @param geoView A GeoView
   * @throws NullPointerException if the geoView or its map/scene are null
   */
  public BookmarkListView(@NamedArg("geoView") GeoView geoView) {
    super(geoView);
    cellFactory = new SimpleObjectProperty<>(new BookmarkListCellFactory());
  }

  @Override
  protected Skin<?> createDefaultSkin() {
    return new BookmarkListViewSkin(this);
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
