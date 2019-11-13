package com.esri.arcgisruntime.toolkit.skins;

import com.esri.arcgisruntime.mapping.Bookmark;
import com.esri.arcgisruntime.mapping.view.ViewpointChangedEvent;
import com.esri.arcgisruntime.mapping.view.ViewpointChangedListener;
import com.esri.arcgisruntime.toolkit.BookmarkListView;
import javafx.scene.control.ListView;
import javafx.scene.control.SkinBase;

/**
 * A Skin to display the BookmarkListView as a ListView. Selecting a bookmark will move the GeoView to its viewpoint.
 * Navigating away from the selected bookmark's viewpoint will deselect it in the list.
 */
public class BookmarkListViewSkin extends SkinBase<BookmarkListView> {

  private final ListView<Bookmark> listView;

  /**
   * Creates and applies the skin to the given control.
   *
   * @param control bookmarks list control to skin
   */
  public BookmarkListViewSkin(BookmarkListView control) {
    super(control);

    // show the control as a list view
    listView = new ListView<>();
    getChildren().add(listView);

    // bind the items from the control
    listView.itemsProperty().bind(control.bookmarksProperty());

    // default to showing the bookmark's name
    listView.cellFactoryProperty().bind(control.cellFactoryProperty());

    // set a listener for when the selected item changes
    listView.getSelectionModel().selectedItemProperty().addListener(listener -> {
        Bookmark selectedBookmark = listView.getSelectionModel().getSelectedItem();
        if (selectedBookmark != null && control.getGeoView() != null) {
          // deselect the bookmark after changing viewpoint so it can be selected again
          control.getGeoView().setBookmarkAsync(selectedBookmark).addDoneListener(() ->
              control.getGeoView().addViewpointChangedListener(new ViewpointChangedListener() {
                @Override
                public void viewpointChanged(ViewpointChangedEvent viewpointChangedEvent) {
                  // check that the selected item which triggered this is still selected
                  if (listView.getSelectionModel().getSelectedItem() == selectedBookmark) {
                    listView.getSelectionModel().clearSelection();
                  }
                  control.getGeoView().removeViewpointChangedListener(this);
                }
              })
          );
        }
    });
  }

}
