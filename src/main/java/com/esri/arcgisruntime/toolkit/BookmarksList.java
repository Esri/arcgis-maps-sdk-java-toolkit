package com.esri.arcgisruntime.toolkit;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.Bookmark;
import com.esri.arcgisruntime.mapping.BookmarkList;
import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SceneView;
import javafx.application.Platform;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * A list of bookmarks from a map or scene. Selecting a bookmark item set's the geoView's
 * viewpoint to the selected bookmark's viewpoint.
 */
public class BookmarksList extends ListView<Bookmark> {

    /**
     * Creates an instance for the GeoView.
     * @param geoView A GeoView
     */
    public BookmarksList(GeoView geoView) {
        if (geoView == null) {
            throw new IllegalArgumentException("geoView must not be null");
        }
        if (geoView instanceof MapView) {
            ArcGISMap map = ((MapView) geoView).getMap();
            if (map != null) {
                map.addDoneLoadingListener(() -> {
                    BookmarkList bookmarkList = map.getBookmarks();
                    this.getItems().addAll(bookmarkList);
                    Platform.runLater(() -> bookmarkList.addListChangedListener(listChangedEvent -> {
                        getItems().clear();
                        getItems().addAll(bookmarkList);
                    }));
                });
            } else {
                throw new IllegalStateException("Map cannot be null");
            }
        } else if (geoView instanceof SceneView) {
            ArcGISScene scene = ((SceneView) geoView).getArcGISScene();
            if (scene != null) {
                scene.addDoneLoadingListener(() -> {
                    BookmarkList bookmarkList = scene.getBookmarks();
                    this.getItems().addAll(bookmarkList);
                    Platform.runLater(() -> bookmarkList.addListChangedListener(listChangedEvent -> {
                        getItems().clear();
                        getItems().addAll(bookmarkList);
                    }));
                });
            } else {
                throw new IllegalStateException("Scene cannot be null");
            }
        }
        setCellFactory(new Callback<>() {
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
        getSelectionModel().selectedItemProperty().addListener(listener -> {
            Bookmark selectedBookmark = getSelectionModel().getSelectedItem();
            if (selectedBookmark != null) {
                // unselect bookmark after changing viewpoint so it can be selected again
                geoView.setBookmarkAsync(selectedBookmark).addDoneListener(() -> getSelectionModel().clearSelection());
            }
        });
    }

}
