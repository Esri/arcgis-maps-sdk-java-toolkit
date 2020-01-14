package com.esri.arcgisruntime.toolkit;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Bookmark;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SceneView;
import javafx.collections.ListChangeListener;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Unit tests for BookmarkListView.
 */
public class BookmarksViewTest extends ApplicationTest {

  private final Bookmark guitarShapedTreesBookmark = new Bookmark("Guitar-shaped trees", new Viewpoint(-33.867886,
      -63.985, 4e4));
  private final Bookmark grandPrismaticSpringBookmark = new Bookmark("Grand Prismatic Spring", new Viewpoint(44.525049,
      -110.83819, 6e3));
  private final Bookmark strangeSymbolBookmark = new Bookmark("Strange Symbol", new Viewpoint(37.401573, -116.867808,
      6e3));

  /**
   * Tests that the bookmarks on the underlying map can be viewed through getBookmarks().
   */
  @Test
  public void getBookmarks() {
    MapView mapView = new MapView();
    ArcGISMap map = new ArcGISMap();
    map.getBookmarks().add(guitarShapedTreesBookmark);
    mapView.setMap(map);
    BookmarksView bookmarksView = new BookmarksView(map.getBookmarks());
    Assertions.assertEquals(1, bookmarksView.getBookmarks().size());
    Assertions.assertEquals(guitarShapedTreesBookmark, bookmarksView.getBookmarks().get(0));
  }
}
