/*
 COPYRIGHT 1995-2022 ESRI

 TRADE SECRETS: ESRI PROPRIETARY AND CONFIDENTIAL
 Unpublished material - all rights reserved under the
 Copyright Laws of the United States.

 For additional information, contact:
 Environmental Systems Research Institute, Inc.
 Attn: Contracts Dept
 380 New York Street
 Redlands, California, USA 92373

 email: contracts@esri.com
 */

package com.esri.arcgisruntime.toolkit;

import java.util.Comparator;
import java.util.Objects;
import java.util.logging.Logger;

import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.floor.FloorFacility;
import com.esri.arcgisruntime.mapping.floor.FloorLevel;
import com.esri.arcgisruntime.mapping.floor.FloorManager;
import com.esri.arcgisruntime.mapping.floor.FloorSite;
import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.toolkit.skins.FloorFilterSkin;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * <p>A floor filter control that visualizes data for a floor-aware ArcGISMap / ArcGISScene.</p>
 *
 * <p>The FloorFilter takes a GeoView and manages data for FloorSites, FloorFacilities and FloorLevels via the
 * FloorManager attached to a floor-aware ArcGISMap/ArcGISScene. If a map or scene is not floor-aware, the FloorManager
 * will be null.</p>
 *
 * <p>The model keeps a selected FloorSite, selected FloorFacility and selected FloorLevel in-sync. A FloorLevel will
 * always have one associated FloorFacility. Depending on the data used to create the FloorFilter, a FloorFacility may
 * optionally be linked to a single FloorSite. A FloorSite may optionally have a list of facilities.</p>
 *
 * <p>The visibility of a FloorLevel, generally seen as a floor plan on a GeoView, can be toggled true/false.</p>
 *
 * @since 100.14.0
 */
public class FloorFilter extends Control {

  /**
   * Creates a FloorFilter for a given GeoView.
   *
   * @param geoView the GeoView to connect to
   * @throws NullPointerException if geoView is null
   * @since 100.14.0
   */
  public FloorFilter(GeoView geoView) {
    geoViewProperty.set(geoView);
    setupFloorManager();
  }

  @Override
  protected Skin<?> createDefaultSkin() {
    return new FloorFilterSkin(this);
  }

  @Override
  public String getUserAgentStylesheet() {
    return Objects.requireNonNull(this.getClass().getResource("skins/floor-filter.css")).toExternalForm();
  }

  /**
   * A property for the GeoView this FloorFilter is linked to.
   *
   * @since 100.14.0
   */
  private final SimpleObjectProperty<GeoView> geoViewProperty = new SimpleObjectProperty<>() {
    @Override
    public void set(GeoView newValue) {
      super.set(Objects.requireNonNull(newValue, "GeoView cannot be null"));
    }
  };

  /**
   * Gets the GeoView that the FloorFilter is linked to.
   *
   * @return the GeoView
   * @since 100.14.0
   */
  public GeoView getGeoView() {
    return geoViewProperty.get();
  }

  /**
   * Returns a read-only property containing the GeoView this FloorFilter is linked to.
   *
   * @return the GeoView Property
   * @since 100.14.0
   */
  public ReadOnlyObjectProperty<GeoView> geoViewProperty() {
    return geoViewProperty;
  }

  /**
   * A property for the FloorManager this FloorFilter is linked to. The FloorManager is on the GeoModel attached
   * to the GeoView.
   *
   * @since 100.14.0
   */
  private final SimpleObjectProperty<FloorManager> floorManagerProperty = new SimpleObjectProperty<>();

  /**
   * Gets the FloorManager this FloorFilter is linked to.
   *
   * @return the FloorManager, or null if none.
   * @since 100.14.0
   */
  public FloorManager getFloorManager() {
    return floorManagerProperty.get();
  }

  /**
   * Returns a read-only property containing the FloorManager this FloorFilter is linked to.
   *
   * @return a read-only FloorManager Property, or null if none.
   * @since 100.14.0
   */
  public ReadOnlyObjectProperty<FloorManager> floorManagerProperty() {
    return floorManagerProperty;
  }

  /**
   * Gets all the FloorSites associated with the FloorManager.
   *
   * @return an observable list of FloorSites, empty if FloorManager is null.
   * @since 100.14.0
   */
  public ObservableList<FloorSite> getSites() {
    if (getFloorManager() != null) {
      return FXCollections.observableArrayList(getFloorManager().getSites());
    }
    return FXCollections.observableArrayList();
  }

  /**
   * Gets all the FloorFacilities associated with the FloorManager.
   *
   * @return an observable list of FloorFacilities, empty if FloorManager is null.
   * @since 100.14.0
   */
  public ObservableList<FloorFacility> getFacilities() {
    if (getFloorManager() != null) {
      return FXCollections.observableArrayList(getFloorManager().getFacilities());
    }
    return FXCollections.observableArrayList();
  }

  /**
   * Gets all the FloorLevels associated with the FloorManager.
   *
   * @return an observable list of FloorLevels, empty if FloorManager is null.
   * @since 100.14.0
   */
  public ObservableList<FloorLevel> getLevels() {
    if (getFloorManager() != null) {
      return FXCollections.observableArrayList(getFloorManager().getLevels());
    }
    return FXCollections.observableArrayList();
  }

  /**
   * A property for the selected FloorSite.
   *
   * @since 100.14.0
   */
  private final SimpleObjectProperty<FloorSite> selectedSiteProperty = new SimpleObjectProperty<>();

  /**
   * Gets the selected FloorSite.
   *
   * @return the selected floor site, or null if none.
   * @since 100.14.0
   */
  public FloorSite getSelectedSite() {
    return selectedSiteProperty.get();
  }

  /**
   * Returns a read-only property for the selected FloorSite.
   *
   * @return a read-only property for the selected FloorSite, or null if none.
   * @since 100.14.0
   */
  public ReadOnlyObjectProperty<FloorSite> selectedSiteProperty() {
    return selectedSiteProperty;
  }

  /**
   * A method to set the selected FloorSite if it is not already selected. In addition, the method resets the currently
   * selected Facility to keep the data in-sync.
   *
   * @param newValue the selected floor site.
   * @since 100.14.0
   */
  public void setSelectedSite(FloorSite newValue) {
    if (newValue != getSelectedSite()) {
      // if the provided site is not already selected, set it to the selected site property
      this.selectedSiteProperty.set(newValue);
      // reset the currently selected facility
      this.setSelectedFacility(null);

      if (newValue != null) {
        // set the viewpoint to the selected site
        getGeoView().setViewpoint(new Viewpoint(selectedSiteProperty.get().getGeometry().getExtent()));
      }
    }
  }

  /**
   * Returns the ID of the selected FloorSite.
   *
   * @return the ID of the selected FloorSite, or null if there is no selected site.
   * @since 100.14.0
   */
  public String getSelectedSiteId() {
    if (getSelectedSite() == null) {
      return null;
    }
    return getSelectedSite().getSiteId();
  }

  /**
   * Sets the selected FloorSite by ID.
   *
   * @param id the ID of the FloorSite to select
   * @since 100.14.0
   */
  public void setSelectedSiteById(String id) {
    // find the site on the current FloorManagers sites list, or return null
    FloorSite selectedSite =
      getFloorManager().getSites().stream().filter(site -> Objects.equals(site.getSiteId(), id)).findFirst().orElse(null);
    // set the result as the selected site
    this.setSelectedSite(selectedSite);
  }

  /**
   * A property for the selected FloorFacility.
   *
   * @since 100.14.0
   */
  private final SimpleObjectProperty<FloorFacility> selectedFacilityProperty = new SimpleObjectProperty<>();

  /**
   * Gets the selected FloorFacility.
   *
   * @return the selected floor facility, or null if none.
   * @since 100.14.0
   */
  public FloorFacility getSelectedFacility() {
    return selectedFacilityProperty.get();
  }

  /**
   * Returns a read-only property for the selected FloorFacility.
   *
   * @return a read-only property for the selected FloorFacility, or null if none.
   * @since 100.14.0
   */
  public ReadOnlyObjectProperty<FloorFacility> selectedFacilityProperty() {
    return selectedFacilityProperty;
  }

  /**
   * Sets the selected FloorFacility if it is not already selected. In addition, associated data is reset to keep the
   * data in-sync.
   *
   * @param newValue the selected floor facility.
   * @since 100.14.0
   */
  public void setSelectedFacility(FloorFacility newValue) {
    if (newValue != getSelectedFacility()) {
      // if the floor facility is not already selected, set it to the selected facility property
      this.selectedFacilityProperty.set(newValue);

      if (newValue != null) {
        // set the viewpoint to the selected facility
        getGeoView().setViewpoint(new Viewpoint(selectedFacilityProperty.get().getGeometry().getExtent()));
        // set the selected site property directly to the facility's site to keep data in-sync
        this.selectedSiteProperty.set(newValue.getSite());

        if (newValue.getLevels().isEmpty()) {
          // if the currently selected facility does not have any levels, reset the selected level
          this.setSelectedLevel(null);
        } else {
          // if the currently selected facility does have associated levels, auto-select the ground floor
          // the floors information model requires ground floor to be at vertical order 0
          var groundFloor = newValue.getLevels().stream().filter(level -> level.getVerticalOrder() == 0).findFirst().orElse(null);
          this.setSelectedLevel(groundFloor);
        }
      }
    }
  }

  /**
   * Returns the ID of the selected FloorFacility.
   *
   * @return the ID of the selected FloorFacility, or null if no facility is selected.
   * @since 100.14.0
   */
  public String getSelectedFacilityId() {
    if (getSelectedFacility() == null) {
      return null;
    }
    return getSelectedFacility().getFacilityId();
  }

  /**
   * Sets the selected FloorFacility by ID.
   *
   * @param id the ID of the FloorFacility to select
   * @since 100.14.0
   */
  public void setSelectedFacilityById(String id) {
    // find the facility on the current FloorManagers facilities list, or return null
    FloorFacility selectedFacility =
      getFloorManager().getFacilities().stream().filter(facility -> Objects.equals(facility.getFacilityId(), id))
        .findFirst().orElse(null);
    // set the result as the selected facility
    this.setSelectedFacility(selectedFacility);
  }

  /**
   * A property for the selected FloorLevel.
   *
   * @since 100.14.0
   */
  private final SimpleObjectProperty<FloorLevel> selectedLevelProperty = new SimpleObjectProperty<>();

  /**
   * Gets the selected FloorLevel.
   *
   * @return the selected floor level, or null if none.
   * @since 100.14.0
   */
  public FloorLevel getSelectedLevel() {
    return selectedLevelProperty.get();
  }

  /**
   * Returns a read-only property for the selected FloorLevel.
   *
   * @return a read-only property for the selected FloorLevel, or null if none.
   * @since 100.14.0
   */
  public ReadOnlyObjectProperty<FloorLevel> selectedLevelProperty() {
    return selectedLevelProperty;
  }

  /**
   * Sets the selected FloorLevel if it is not already selected. In addition, associated data is reset to keep the
   * data in-sync.
   *
   * @param newValue the selected floor level.
   * @since 100.14.0
   */
  public void setSelectedLevel(FloorLevel newValue) {
    if (newValue != getSelectedLevel()) {
      // if the floor level is not already selected, set it to the selected level property
      this.selectedLevelProperty.set(newValue);

      if (newValue != null && newValue.getFacility() != getSelectedFacility()) {
        // if the selected level is not null it is associated with a different facility than the currently selected
        // facility, update the facility and site data directly to keep it in-sync
        this.selectedFacilityProperty.set(newValue.getFacility());
        this.selectedSiteProperty.set(newValue.getFacility().getSite());
      }
      // filter the visibility of the floor levels to match the selected level
      filterLevelsVisibility();
    }
  }

  /**
   * Returns the ID of the selected FloorLevel.
   *
   * @return the ID of the selected FloorLevel, or null if no level is selected.
   * @since 100.14.0
   */
  public String getSelectedLevelId() {
    if (getSelectedLevel() == null) {
      return null;
    }
    return getSelectedLevel().getLevelId();
  }

  /**
   * Sets the selected FloorLevel by ID.
   *
   * @param id the ID of the FloorLevel to select
   * @since 100.14.0
   */
  public void setSelectedLevelById(String id) {
    var selectedLevel = getFloorManager().getLevels().stream().filter(level -> Objects.equals(level.getLevelId(), id)).findFirst().orElse(null);
    this.setSelectedLevel(selectedLevel);
  }

  /**
   * A private method used during the setup of the FloorFilter to get the FloorManager from the ArcGISMap or
   * ArcGISScene attached to the GeoView. Both the Map/Scene and FloorManager must be loaded in order to access
   * the attached data. In addition, the Map/Scene must be floor-aware. A message is logged if the Map/Scene is not
   * floor-aware and the FloorFilter is not displayed.
   *
   * @since 100.14.0
   */
  private void setupFloorManager() {
    if (getGeoView() instanceof MapView) {
      // if the GeoView is a MapView get the ArcGISMap
      ArcGISMap map = ((MapView) getGeoView()).getMap();
      if (map != null) {
        // load the map
        map.loadAsync();
        map.addDoneLoadingListener(() -> {
          // check the map is floor aware
          if (map.getFloorManager() != null) {
            var floorManager = map.getFloorManager();
            // load the FloorManager to access data
            floorManager.loadAsync();
            floorManager.addDoneLoadingListener(() -> {
              if (floorManager.getLoadStatus() == LoadStatus.LOADED) {
                // set the loaded FloorManager to the floor manager property
                floorManagerProperty.set(floorManager);
              }
            });
          } else {
            // if not floor aware don't set the floor manager property to null
            floorManagerProperty.set(null);
            Logger logger = Logger.getLogger(FloorFilter.class.getName());
            logger.info("The ArcGISMap attached to the provided GeoView is not Floor Aware. FloorFilter will not " +
              "be displayed. Call FloorFilter.refresh() after updating the ArcGISMap to try again.");
          }
        });
      }
    } else if (getGeoView() instanceof SceneView) {
      // if the GeoView is a SceneView get the ArcGISScene
      ArcGISScene scene = ((SceneView) getGeoView()).getArcGISScene();
      if (scene != null) {
        // load the scene
        scene.loadAsync();
        scene.addDoneLoadingListener(() -> {
          // check the scene is floor aware
          if (scene.getFloorManager() != null) {
            var floorManager = scene.getFloorManager();
            // load the FloorManager to access data
            floorManager.loadAsync();
            floorManager.addDoneLoadingListener(() -> {
              if (floorManager.getLoadStatus() == LoadStatus.LOADED) {
                // set the loaded FloorManager to the floor manager property
                floorManagerProperty.set(floorManager);
              }
            });
          } else {
            // if not floor aware set the floor manager property to null
            floorManagerProperty.set(null);
            Logger logger = Logger.getLogger(FloorFilter.class.getName());
            logger.info("The ArcGISScene attached to the provided GeoView is not Floor Aware. Floor Filter will " +
              "not be displayed. Call FloorFilter.refresh() after updating the ArcGISScene to try again.");
          }
        });
      }
    }
  }

  /**
   * A method that can be called to refresh the data on the existing FloorFilter. For example, if the GeoModel attached
   * to the GeoView is updated, the FloorManager can be reloaded. Selected properties will be reset to null.
   *
   * @since 100.14.0
   */
  public void refresh() {
    setSelectedSite(null);
    setSelectedFacility(null);
    setSelectedLevel(null);
    setupFloorManager();
  }

  /**
   * If there is a selected floor level, the method sets all floor levels with the same vertical order to be visible,
   * and sets the visibility of all other levels to false. If there is no selected level, no action is taken.
   *
   * @since 100.14.0
   */
  public void filterLevelsVisibility() {
    if (getSelectedLevel() != null) {
      getLevels().forEach(level -> level.setVisible(level.getVerticalOrder() == getSelectedLevel().getVerticalOrder()));
    }
  }

  /**
   * Set the visibility of all floor levels on the FloorManager to visible.
   * This can be useful when viewing a floor aware ArcGISScene to see all floor levels in 3D.
   *
   * @since 100.14.0
   */
  public void setAllLevelsVisible() {
    getLevels().forEach(level -> level.setVisible(true));
  }
}
