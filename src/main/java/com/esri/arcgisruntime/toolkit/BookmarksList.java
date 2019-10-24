package com.esri.arcgisruntime.toolkit;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Bookmark;
import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.mapping.view.MapView;
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
    }

}
