package com.esri.arcgisruntime.toolkit.utils;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Bookmark;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.util.ListenableList;
import javafx.collections.ObservableList;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for ListenableListUtils.
 */
public class ListenableListUtilsTest {

  private final Bookmark guitarShapedTreesBookmark = new Bookmark("Guitar-shaped trees", new Viewpoint(-33.867886,
      -63.985, 4e4));
  private final Bookmark grandPrismaticSpringBookmark = new Bookmark("Grand Prismatic Spring", new Viewpoint(44.525049,
      -110.83819, 6e3));
  private final Bookmark strangeSymbolBookmark = new Bookmark("Strange Symbol", new Viewpoint(37.401573, -116.867808,
      6e3));

  /**
   * Tests the initializing, adding, and removing items from the observable list created with toObservableList.
   */
  @Test
  public void toObservableList() {
    // given a listenable list with some items
    ArcGISMap map = new ArcGISMap();
    ListenableList<Bookmark> listenableList = map.getBookmarks();
    listenableList.add(guitarShapedTreesBookmark);

    // when the observable list is created from the listenable list
    ObservableList<Bookmark> observableList = ListenableListUtils.toObservableList(listenableList);

    // it will also contain the items
    Assertions.assertTrue(observableList.contains(guitarShapedTreesBookmark));

    // when items are added to the listenable list
    List<Bookmark> bookmarks = new ArrayList<>();
    bookmarks.add(grandPrismaticSpringBookmark);
    bookmarks.add(strangeSymbolBookmark);
    listenableList.addAll(bookmarks);

    // the observable list is updated with the items
    Assertions.assertTrue(observableList.containsAll(listenableList));

    // when an item is removed from the listenable list
    listenableList.remove(guitarShapedTreesBookmark);

    // it is removed from the observable list
    Assertions.assertFalse(observableList.contains(guitarShapedTreesBookmark));

    // when an item is added to the observable list
    observableList.add(guitarShapedTreesBookmark);

    // it will be reflected to the listenable list
    Assertions.assertTrue(listenableList.contains(guitarShapedTreesBookmark));

    // when the item is removed from the observable list
    observableList.remove(grandPrismaticSpringBookmark);

    // the item is also removed from the listenable list
    Assertions.assertFalse(listenableList.contains(grandPrismaticSpringBookmark));
  }
}
