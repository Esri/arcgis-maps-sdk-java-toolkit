package com.esri.arcgisruntime.toolkit;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Bookmark;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SceneView;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.Arrays;
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
   * Tests that the BookmarksView can be constructed from a simple list of bookmarks.
   */
  @Test
  public void listOfBookmarksConst() {
    BookmarksView bookmarksView = new BookmarksView(Arrays.asList(guitarShapedTreesBookmark,
        grandPrismaticSpringBookmark));
    // should be initialized with the bookmarks from the list
    Assertions.assertEquals(2, bookmarksView.getBookmarks().size());
    Assertions.assertEquals(guitarShapedTreesBookmark, bookmarksView.getBookmarks().get(0));
    Assertions.assertEquals(grandPrismaticSpringBookmark, bookmarksView.getBookmarks().get(1));
  }

  /**
   * Tests that the BookmarksView can be constructed from and bound to a map's BookmarkList.
   */
  @Test
  public void bookmarkListConst() {
    MapView mapView = new MapView();
    ArcGISMap map = new ArcGISMap();
    map.getBookmarks().add(guitarShapedTreesBookmark);
    mapView.setMap(map);
    BookmarksView bookmarksView = new BookmarksView(map.getBookmarks());
    // should be initialized with the bookmark from the map's BookmarkList
    Assertions.assertEquals(1, bookmarksView.getBookmarks().size());
    Assertions.assertEquals(guitarShapedTreesBookmark, bookmarksView.getBookmarks().get(0));

    // updates to the BookmarkList are seen by the BookmarksView
    map.getBookmarks().add(grandPrismaticSpringBookmark);
    Assertions.assertEquals(2, bookmarksView.getBookmarks().size());
    Assertions.assertEquals(guitarShapedTreesBookmark, bookmarksView.getBookmarks().get(0));
    Assertions.assertEquals(grandPrismaticSpringBookmark, bookmarksView.getBookmarks().get(1));
  }

  /**
   * Tests that the BookmarksView can be constructed from and bound to an observable list of bookmarks.
   */
  @Test
  public void observableListOfBookmarksConst() {
    ObservableList<Bookmark> observableList = FXCollections.observableArrayList(guitarShapedTreesBookmark,
        grandPrismaticSpringBookmark);
    BookmarksView bookmarksView = new BookmarksView(observableList);
    // should be initialized with the bookmarks from the observable list
    Assertions.assertEquals(observableList, bookmarksView.getBookmarks(), "Should reuse the given observable list");
    Assertions.assertEquals(2, bookmarksView.getBookmarks().size());
    Assertions.assertEquals(guitarShapedTreesBookmark, bookmarksView.getBookmarks().get(0));
    Assertions.assertEquals(grandPrismaticSpringBookmark, bookmarksView.getBookmarks().get(1));

    // updates to the BookmarkList are seen by the BookmarksView
    observableList.remove(guitarShapedTreesBookmark);
    Assertions.assertEquals(1, bookmarksView.getBookmarks().size());
    Assertions.assertEquals(grandPrismaticSpringBookmark, bookmarksView.getBookmarks().get(0));
  }
}
