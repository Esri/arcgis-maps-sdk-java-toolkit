package com.esri.arcgisruntime.toolkit;

import com.esri.arcgisruntime.mapping.view.GeoView;
import javafx.scene.layout.Region;

public class BookmarksView extends Region {

    public BookmarksView(GeoView geoView) {
        if (geoView == null) {
            throw new IllegalArgumentException("geoView must not be null");
        }
    }
}
