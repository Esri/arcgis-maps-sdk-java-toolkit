package com.esri.arcgisruntime.toolkit;

import javafx.application.Platform;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class BookmarksListTest {

    /**
     * Tests IllegalArgumentException from null geoView constructor arg.
     */
    @Test
    public void constructorGeoViewNull() {
        Platform.startup(() ->
            Assertions.assertThrows(IllegalArgumentException.class, () -> new BookmarksList(null))
        );
    }
}
