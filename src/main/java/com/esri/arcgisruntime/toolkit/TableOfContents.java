package com.esri.arcgisruntime.toolkit;

import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.toolkit.skins.TableOfContentsTreeViewSkin;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

import java.util.Objects;

/**
 * Control for showing and interacting with the layers of a GeoView's map or scene.
 */
public class TableOfContents extends Control {

  private ReadOnlyObjectWrapper<GeoView> geoView;

  /**
   * Creates a table of contents for the given GeoView.
   *
   * @param geoView a GeoView
   */
  public TableOfContents(GeoView geoView) {
    this.geoView = new ReadOnlyObjectWrapper<>(Objects.requireNonNull(geoView));
  }

  @Override
  protected Skin<?> createDefaultSkin() {
    return new TableOfContentsTreeViewSkin(this);
  }

  /**
   * Gets the GeoView associated with this table of contents.
   *
   * @return geo view
   */
  public GeoView getGeoView() {
    return geoViewProperty().get();
  }

  /**
   * The GeoView associated with this table of contents.
   *
   * @return geo view property
   */
  public ReadOnlyObjectProperty<GeoView> geoViewProperty() {
    return this.geoView.getReadOnlyProperty();
  }

}
