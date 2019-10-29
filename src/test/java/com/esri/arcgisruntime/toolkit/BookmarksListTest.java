package com.esri.arcgisruntime.toolkit;

import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SceneView;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.testfx.framework.junit.ApplicationTest;

/**
 * Unit tests for BookmarksList.
 */
public class BookmarksListTest extends ApplicationTest {

  /**
   * Tests IllegalArgumentException from null geoView constructor arg.
   */
  @Test
  public void constructorGeoViewNull() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> new BookmarksList(null));
  }

  /**
   * Tests IllegalStateException from null map in map view.
   */
  @Test
  public void mapViewWithoutMap() {
    MapView mapView = new MapView();
    Assertions.assertThrows(IllegalStateException.class, () -> new BookmarksList(mapView));
  }

  /**
   * Tests IllegalStateException from null scene in geo view.
   */
  @Test
  public void sceneViewWithoutScene() {
    SceneView sceneView = new SceneView();
    Assertions.assertThrows(IllegalStateException.class, () -> new BookmarksList(sceneView));
  }
}
