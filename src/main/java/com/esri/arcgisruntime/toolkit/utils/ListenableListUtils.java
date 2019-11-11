package com.esri.arcgisruntime.toolkit.utils;

import com.esri.arcgisruntime.util.ListenableList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Utility methods for ListenableLists.
 */
public class ListenableListUtils {

  /**
   * Returns an ObservableList which mirrors the given ListenableList. ListChangedEvents are forwarded to the
   * ObservableList's ListChangedListener.
   *
   * @param listenableList original listenable list
   * @param <T> list content type
   *
   * @return an observable list that mirrors the listenable list
   */
  public static <T> ObservableList<T> toObservableList(ListenableList<T> listenableList) {
    ObservableList<T> observableList = FXCollections.observableArrayList(listenableList);
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
    return observableList;
  }

}
