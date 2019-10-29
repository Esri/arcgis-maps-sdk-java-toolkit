package com.esri.arcgisruntime.toolkit.skins;

import com.esri.arcgisruntime.mapping.Bookmark;
import com.esri.arcgisruntime.toolkit.BookmarksWidget;
import javafx.beans.property.ObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SkinBase;
import javafx.util.Callback;

/**
 * A Skin to display the BookmarksWidget as a ListView.
 */
public class BookmarksListSkin extends SkinBase<BookmarksWidget> {

  private final ListView<Bookmark> listView;

  /**
   * Creates and applies the skin to the given control.
   *
   * @param control bookmarks list control to skin
   */
  public BookmarksListSkin(BookmarksWidget control) {
    super(control);

    // show the control as a list view
    listView = new ListView<>();
    getChildren().add(listView);

    // bind the items from the control
    listView.itemsProperty().bind(control.bookmarksProperty());

    // default to showing the bookmark's name
    listView.setCellFactory(new Callback<>() {
      @Override
      public ListCell<Bookmark> call(ListView<Bookmark> param) {
        return new ListCell<>() {
          @Override
          protected void updateItem(Bookmark item, boolean empty) {
            super.updateItem(item, empty);
            setText(empty ? null : item.getName());
            setGraphic(null);
          }
        };
      }
    });

    // set a listener for when the selected item changes
    listView.getSelectionModel().selectedItemProperty().addListener(listener -> {
      Bookmark selectedBookmark = listView.getSelectionModel().getSelectedItem();
      EventHandler<BookmarksWidget.BookmarkSelectedEvent> bookmarkSelectedEventHandler = control.getOnBookmarkSelected();
      if (bookmarkSelectedEventHandler != null) {
        // use the user-specified handler
        bookmarkSelectedEventHandler.handle(new BookmarksWidget.BookmarkSelectedEvent(selectedBookmark));
      } else if (selectedBookmark != null) {
        // default to deselecting the bookmark after changing viewpoint so it can be selected again
        control.getGeoView().setBookmarkAsync(selectedBookmark).addDoneListener(() ->
            listView.getSelectionModel().clearSelection()
        );
      }
    });
  }

  /**
   * Gets the cell factory callback used to display the cells in the list. Defaults to showing the name of the bookmark.
   *
   * @return cell factory callback
   */
  public Callback<ListView<Bookmark>, ListCell<Bookmark>> getCellFactory() {
    return listView.getCellFactory();
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
    listView.setCellFactory(cellFactory);
  }
}
