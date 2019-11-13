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

import java.awt.print.Book;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Unit tests for BookmarkListView.
 */
public class BookmarkListViewTest extends ApplicationTest {

  private final Bookmark guitarShapedTreesBookmark = new Bookmark("Guitar-shaped trees", new Viewpoint(-33.867886,
      -63.985, 4e4));
  private final Bookmark grandPrismaticSpringBookmark = new Bookmark("Grand Prismatic Spring", new Viewpoint(44.525049,
      -110.83819, 6e3));
  private final Bookmark strangeSymbolBookmark = new Bookmark("Strange Symbol", new Viewpoint(37.401573, -116.867808,
      6e3));
  
  /**
   * Tests that NullPointerException is thrown when map view's map is null.
   */
  @Test
  public void mapViewWithoutMap() {
    MapView mapView = new MapView();
    Assertions.assertThrows(NullPointerException.class, () -> new BookmarkListView(mapView));
  }

  /**
   * Tests that NullPointerException is thrown when scene view's scene is null.
   */
  @Test
  public void sceneViewWithoutScene() {
    SceneView sceneView = new SceneView();
    Assertions.assertThrows(NullPointerException.class, () -> new BookmarkListView(sceneView));
  }

  /**
   * Tests that the bookmarks on the underlying map can be viewed through getBookmarks().
   */
  @Test
  public void getBookmarks() {
    MapView mapView = new MapView();
    ArcGISMap map = new ArcGISMap();
    map.getBookmarks().add(guitarShapedTreesBookmark);
    mapView.setMap(map);
    BookmarkListView bookmarkListView = new BookmarkListView(mapView);
    Assertions.assertEquals(1, bookmarkListView.getBookmarks().size());
    Assertions.assertEquals(guitarShapedTreesBookmark, bookmarkListView.getBookmarks().get(0));
  }

  @Test
  public void addBookmarkToMap() {
    MapView mapView = new MapView();
    ArcGISMap map = new ArcGISMap();
    mapView.setMap(map);
    BookmarkListView bookmarkListView = new BookmarkListView(mapView);
    Assertions.assertEquals(0, bookmarkListView.getBookmarks().size());

  }

  @Test
  public void removeBookmarkFromMap() {
    MapView mapView = new MapView();
    ArcGISMap map = new ArcGISMap();
    Bookmark bookmark = new Bookmark("Guitar-shaped trees", new Viewpoint(-33.867886, -63.985, 4e4));
    map.getBookmarks().add(bookmark);
    mapView.setMap(map);
    BookmarkListView bookmarkListView = new BookmarkListView(mapView);
    Assertions.assertEquals(1, bookmarkListView.getBookmarks().size());
    map.getBookmarks().remove(bookmark);
    Assertions.assertEquals(0, bookmarkListView.getBookmarks().size());
  }

  /**
   * Tests that modifications to the underlying map's BookmarkList are seen as change events on the bookmarkProperty.
   */
  @Test
  public void bookmarksPropertyListener() {
    MapView mapView = new MapView();
    ArcGISMap map = new ArcGISMap();
    mapView.setMap(map);
    BookmarkListView bookmarkListView = new BookmarkListView(mapView);
    AtomicBoolean flag = new AtomicBoolean(false);
    bookmarkListView.bookmarksProperty().addListener((ListChangeListener<Bookmark>) c -> {
      Assertions.assertTrue(c.next());
      Assertions.assertTrue(c.wasAdded());
      flag.set(true);
    });
    map.getBookmarks().add(new Bookmark(
        "Guitar-shaped trees", new Viewpoint(-33.867886, -63.985, 4e4)));
    WaitForAsyncUtils.waitForFxEvents();
    Assertions.assertTrue(flag.get());
  }
}
