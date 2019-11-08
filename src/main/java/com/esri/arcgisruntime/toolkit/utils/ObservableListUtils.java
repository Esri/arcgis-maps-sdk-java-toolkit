package com.esri.arcgisruntime.toolkit.utils;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class ObservableListUtils {

  @SafeVarargs
  public static <T> ObservableList<T> createAggregatedObservableList(ObservableList<T>... observableLists) {
    ObservableList<T> modifiableList = FXCollections.observableArrayList();
    ObservableList<T> unmodifiableList = FXCollections.unmodifiableObservableList(modifiableList);
    for (int i = 0; i < observableLists.length; i++) {
      ObservableList<T> observableList = observableLists[i];
      modifiableList.addAll(observableList);
      int index = i;
      observableList.addListener((ListChangeListener<T>) c -> {
        while (c.next()) {
          int from = c.getFrom();
          int to = c.getTo();
          if (c.wasAdded()) {
            modifiableList.addAll(from + index, c.getAddedSubList());
          } else if (c.wasRemoved()) {
            modifiableList.remove(from + index, to + index);
          }
        }
      });
    }
    return unmodifiableList;
  }
}
