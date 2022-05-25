package com.esri.arcgisruntime.toolkit.examples.model;

import com.esri.arcgisruntime.mapping.view.GeoView;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;

import java.util.List;

public interface Example {

    VBox getSettings();

    String getExampleName();

    List<Tab> getTabs();

    String getDescription();

    List<GeoView> getGeoViews();
}
