package com.esri.arcgisruntime.toolkit.utils;

import com.esri.arcgisruntime.util.ListenableList;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;

/**
 * Utility methods for ListenableLists.
 */
public class ListenableListUtils {

  /**
   * Returns an ObservableList which is bi-directionally bound to a ListenableList.
   *
   * @param listenableList a listenable list
   * @param <T> list content type
   *
   * @return an observable list
   */
  public static <T> ObservableList<T> toObservableList(ListenableList<T> listenableList) {
    ObservableList<T> observableList = FXCollections.observableArrayList(listenableList);
    // forward listenable list changes to observable list
    listenableList.addListChangedListener(listChangedEvent -> {
      switch (listChangedEvent.getAction()) {
        case ADDED:
          observableList.addAll(listChangedEvent.getIndex(), listChangedEvent.getItems());
          break;
        case REMOVED:
          observableList.removeAll(listChangedEvent.getItems());
          break;
      }
    });
    // forward observable list changes to listenable list
    observableList.addListener((ListChangeListener<T>) c -> {
      while (c.next()) {
        if (c.wasAdded() && !listenableList.containsAll(c.getAddedSubList())) {
          listenableList.addAll(c.getFrom(), c.getAddedSubList());
        } else if (c.wasRemoved() && !c.getList().containsAll(listenableList)) {
          listenableList.removeAll(new ArrayList<>(listenableList.subList(c.getFrom(), c.getRemovedSize())));
        }
      }
    });
    return observableList;
  }

}
