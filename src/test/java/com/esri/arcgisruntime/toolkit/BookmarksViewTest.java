package com.esri.arcgisruntime.toolkit;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class BookmarksViewTest {

    /**
     * Tests IllegalArgumentException from null geoView constructor arg.
     */
    @Test
    public void constructorGeoViewNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new BookmarksView(null));
    }
}
