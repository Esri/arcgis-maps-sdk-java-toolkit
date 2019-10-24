package com.esri.arcgisruntime.toolkit;

import com.esri.arcgisruntime.mapping.view.GeoView;
import javafx.scene.layout.Region;

/**
 * A widget for displaying bookmarks from a map or scene. Selecting a bookmark item set's the geoView's viewpoint to
 * the selected bookmark's viewpoint.
 */
public class BookmarksView extends Region {

    /**
     * Creates an instance of BookmarksView for the GeoView.
     * @param geoView A GeoView
     */
    public BookmarksView(GeoView geoView) {
        if (geoView == null) {
            throw new IllegalArgumentException("geoView must not be null");
        }
    }
}
