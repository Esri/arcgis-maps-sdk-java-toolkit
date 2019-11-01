package com.esri.arcgisruntime.toolkit;

import com.esri.arcgisruntime.mapping.view.GeoView;
import javafx.scene.control.Control;

import java.util.Objects;

/**
 * Control for showing and interacting with the layers of a GeoView's map or scene.
 */
public class TableOfContents extends Control {

  /**
   * Creates a table of contents for the given GeoView.
   *
   * @param geoView a GeoView
   */
  public TableOfContents(GeoView geoView) {
    Objects.requireNonNull(geoView);
  }

  /**
   * Gets the GeoView associated with this table of contents.
   *
   * @return geo view
   */
  public GeoView getGeoView() {
    return null;
  }

}
