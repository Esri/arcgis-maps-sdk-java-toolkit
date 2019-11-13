package com.esri.arcgisruntime.toolkit.utils;

import com.esri.arcgisruntime.mapping.*;
import com.esri.arcgisruntime.util.ListenableList;
import javafx.collections.ObservableList;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;

public class ListenableListUtilsTest {

  private final Bookmark guitarShapedTreesBookmark = new Bookmark("Guitar-shaped trees", new Viewpoint(-33.867886,
      -63.985, 4e4));
  private final Bookmark grandPrismaticSpringBookmark = new Bookmark("Grand Prismatic Spring", new Viewpoint(44.525049,
      -110.83819, 6e3));

  @Test
  public void addItemsToListenableList() {
    // given an observable list from a listenable list
    ArcGISMap map = new ArcGISMap();
    ListenableList<Bookmark> listenableList = map.getBookmarks();
    ObservableList<Bookmark> observableList = ListenableListUtils.toObservableList(listenableList);

    // when items are added to the listenable list
    listenableList.addAll(Arrays.asList(guitarShapedTreesBookmark, grandPrismaticSpringBookmark));

    // the observable list is updated with the items
    Assertions.assertTrue(observableList.containsAll(listenableList));
  }

  @Test
  public void initItemsFromListenableList() {
    // given a listenable list with some items
    ArcGISMap map = new ArcGISMap();
    ListenableList<Bookmark> listenableList = map.getBookmarks();
    listenableList.addAll(Arrays.asList(guitarShapedTreesBookmark, grandPrismaticSpringBookmark));

    // when the observable list is created
    ObservableList<Bookmark> observableList = ListenableListUtils.toObservableList(listenableList);

    // the observable list is initialized with the items
    Assertions.assertTrue(observableList.containsAll(listenableList));
  }

  @Test
  public void removeItemFromListenableList() {
    // given an observable list from a listenable list with some items
    ArcGISMap map = new ArcGISMap();
    ListenableList<Bookmark> listenableList = map.getBookmarks();
    listenableList.addAll(Arrays.asList(guitarShapedTreesBookmark, grandPrismaticSpringBookmark));
    ObservableList<Bookmark> observableList = ListenableListUtils.toObservableList(listenableList);

    // when an item is removed
    listenableList.remove(guitarShapedTreesBookmark);

    // the observable list also shows the item removed
    Assertions.assertFalse(observableList.contains(guitarShapedTreesBookmark));
  }

  @Test
  public void addItemToObservableList() {
    // given an observable list from a listenable list
    ArcGISMap map = new ArcGISMap();
    ListenableList<Bookmark> listenableList = map.getBookmarks();
    ObservableList<Bookmark> observableList = ListenableListUtils.toObservableList(listenableList);

    // when items are added to the observable list
    observableList.addAll(guitarShapedTreesBookmark, grandPrismaticSpringBookmark);

    // the listenable also shows the items
    Assertions.assertTrue(listenableList.containsAll(Arrays.asList(guitarShapedTreesBookmark,
        grandPrismaticSpringBookmark)));
  }

  @Test
  public void removeItemsFromObservableList() {
    // given an observable list from a listenable list with some items
    ArcGISMap map = new ArcGISMap();
    ListenableList<Bookmark> listenableList = map.getBookmarks();
    listenableList.addAll(Arrays.asList(guitarShapedTreesBookmark, grandPrismaticSpringBookmark));
    ObservableList<Bookmark> observableList = ListenableListUtils.toObservableList(listenableList);
    Assertions.assertEquals(2, observableList.size());

    // when items are added to the observable list
    observableList.removeAll(guitarShapedTreesBookmark, grandPrismaticSpringBookmark);

    // the listenable also shows the items
    Assertions.assertEquals(0, listenableList.size());
  }
}
