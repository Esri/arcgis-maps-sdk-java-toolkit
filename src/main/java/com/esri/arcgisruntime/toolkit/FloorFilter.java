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

import java.util.Objects;
import java.util.logging.Logger;

import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.GeoModel;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.floor.FloorFacility;
import com.esri.arcgisruntime.mapping.floor.FloorLevel;
import com.esri.arcgisruntime.mapping.floor.FloorManager;
import com.esri.arcgisruntime.mapping.floor.FloorSite;
import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.NavigationChangedListener;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.toolkit.skins.FloorFilterSkin;
import javafx.beans.NamedArg;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
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
 * <p>If desired, the FloorFilter will automatically select the site and facility at the center of the connected
 * GeoView's extent. This is controlled by the AutomaticSelectionMode property.</p>
 *
 * @since 100.14.0
 */
public class FloorFilter extends Control {

  /**
   * The selection mode to be used. This defines how the floor filter updates the selection of sites and facilities
   * as the user navigates the connected GeoView. The default is ALWAYS.
   *
   * @since 100.14.0
   */
  public enum AutomaticSelectionMode {
    /**
     * Never update selection based on the GeoView's current viewpoint.
     *
     * @since 100.14.0
     */
    NEVER,
    /**
     * Always update selection based on the current viewpoint. Clear the selection when the user navigates away.
     *
     * @since 100.14.0
     */
    ALWAYS,
    /**
     * Only update the selection when there is a new site or facility in the current viewpoint. Don't clear selection
     * when the user navigates away.
     *
     * @since 100.14.0
     */
    ALWAYS_NON_CLEARING
  }

  private boolean blockViewpointUpdate = false;
  private boolean siteSetViaFacility = false;
  private boolean facilitySetViaLevel = false;
  private final ObservableList<FloorSite> sites = FXCollections.observableArrayList();
  private final ObservableList<FloorFacility> facilities = FXCollections.observableArrayList();
  private final ObservableList<FloorLevel> levels = FXCollections.observableArrayList();
  private final SimpleObjectProperty<FloorFilter.AutomaticSelectionMode> automaticSelectionModeProperty = new SimpleObjectProperty<>();
  private final SimpleObjectProperty<FloorManager> floorManagerProperty = new SimpleObjectProperty<>();
  private final SimpleObjectProperty<FloorSite> selectedSiteProperty = new SimpleObjectProperty<>();
  private final SimpleObjectProperty<FloorFacility> selectedFacilityProperty = new SimpleObjectProperty<>();
  private final SimpleObjectProperty<FloorLevel> selectedLevelProperty = new SimpleObjectProperty<>();
  private final SimpleObjectProperty<GeoView> geoViewProperty = new SimpleObjectProperty<>() {
    @Override
    public void set(GeoView newValue) {
      super.set(Objects.requireNonNull(newValue, "GeoView cannot be null"));
    }
  };

  /**
   * Creates a FloorFilter for a given GeoView. By default, the AutomaticSelectionMode is ALWAYS.
   *
   * @param geoView the GeoView to connect to
   * @throws NullPointerException if geoView is null
   * @since 100.14.0
   */
  public FloorFilter(@NamedArg("geoView") GeoView geoView) {
    this(geoView, AutomaticSelectionMode.ALWAYS);
  }

  /**
   * Creates a FloorFilter for a given GeoView and sets the provided AutomaticSelectionMode.
   *
   * @param geoView the GeoView to connect to
   * @param selectionMode the AutomaticSelectionMode to use
   * @throws NullPointerException if geoView is null
   * @since 100.14.0
   */
  public FloorFilter(GeoView geoView, AutomaticSelectionMode selectionMode) {
    geoViewProperty.set(geoView);
    automaticSelectionModeProperty.set(selectionMode);
    NavigationChangedListener navigationChangedListener = navigationChangedEvent -> updateSelectionIfNeeded();
    geoView.addNavigationChangedListener(navigationChangedListener);
    ChangeListener<? super FloorSite> siteListener =
      (observable, oldValue, newValue) -> handleUpdateSelectedSite(oldValue, newValue);
    selectedSiteProperty.addListener(siteListener);
    ChangeListener<? super FloorFacility> facilityListener =
      (observable, oldValue, newValue) -> handleUpdateSelectedFacility(oldValue, newValue);
    selectedFacilityProperty.addListener(facilityListener);
    ChangeListener<? super FloorLevel> levelListener =
      (observable, oldValue, newValue) -> handleUpdateSelectedLevel(oldValue, newValue);
    selectedLevelProperty.addListener(levelListener);

    setMaxHeight(USE_PREF_SIZE);
    setMaxWidth(USE_PREF_SIZE);
    setMinHeight(USE_PREF_SIZE);
    setMinWidth(USE_PREF_SIZE);

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
   * Gets the selection mode. Default is AutomaticSelectionMode.ALWAYS.
   *
   * @since 100.14.0
   */
  public AutomaticSelectionMode getAutomaticSelectionMode() {
    return automaticSelectionModeProperty.get();
  }

  /**
   * Sets the selection mode; defines how the floor filter updates its selection as the user navigated the connected
   * GeoView.
   *
   * @since 100.14.0
   */
  public void setAutomaticSelectionMode(AutomaticSelectionMode selectionMode) {
    automaticSelectionModeProperty.set(Objects.requireNonNull(selectionMode, "selection mode cannot be null"));
  }

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
   * Gets the FloorManager this FloorFilter is linked to.
   *
   * @return the FloorManager
   * @since 100.14.0
   */
  public FloorManager getFloorManager() {
    return floorManagerProperty.get();
  }

  /**
   * Returns a read-only property containing the FloorManager this FloorFilter is linked to.
   *
   * @return a read-only FloorManager Property
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
      sites.setAll(getFloorManager().getSites());
    } else {
      sites.clear();
    }
    return sites;
  }

  /**
   * Gets all the FloorFacilities associated with the FloorManager.
   *
   * @return an observable list of FloorFacilities, empty if FloorManager is null.
   * @since 100.14.0
   */
  public ObservableList<FloorFacility> getFacilities() {
    if (getFloorManager() != null) {
      facilities.setAll(getFloorManager().getFacilities());
    } else {
      facilities.clear();
    }
    return facilities;
  }

  /**
   * Gets all the FloorLevels associated with the FloorManager.
   *
   * @return an observable list of FloorLevels, empty if FloorManager is null.
   * @since 100.14.0
   */
  public ObservableList<FloorLevel> getLevels() {
    if (getFloorManager() != null) {
      levels.setAll(getFloorManager().getLevels());
    } else {
      levels.clear();
    }
    return levels;
  }

  /**
   * Gets the selected FloorSite.
   *
   * @return the selected floor site
   * @since 100.14.0
   */
  public FloorSite getSelectedSite() {
    return selectedSiteProperty.get();
  }

  /**
   * Returns a property for the selected FloorSite.
   *
   * @return a property for the selected FloorSite
   * @since 100.14.0
   */
  public SimpleObjectProperty<FloorSite> selectedSiteProperty() {
    return selectedSiteProperty;
  }

  /**
   * Sets the selected FloorSite if it is not already selected.
   * In addition, the method resets the currently selected Facility to keep the data in-sync and updates the viewpoint
   * of the GeoView to the extent of the selected FloorSite's geometry.
   *
   * @param newValue the selected floor site.
   * @since 100.14.0
   */
  public void setSelectedSite(FloorSite newValue) {
    selectedSiteProperty.set(newValue);
  }

  /**
   * Handles changes to the selected site property by keeping other data in-sync.
   *
   * @param oldValue the previously selected site
   * @param newValue the selected site
   * @since 100.14.0
   */
  public void handleUpdateSelectedSite(FloorSite oldValue, FloorSite newValue) {
    if (newValue != oldValue) {
      try {
        if (newValue != null && !blockViewpointUpdate && !siteSetViaFacility) {
          getGeoView().setViewpoint(new Viewpoint(newValue.getGeometry().getExtent()));
        }

        if (!siteSetViaFacility) {
          setSelectedFacility(null);
        }
      } finally {
        siteSetViaFacility = false;
        blockViewpointUpdate = false;
      }
    }

  }

  /**
   * Gets the selected FloorFacility.
   *
   * @return the selected floor facility
   * @since 100.14.0
   */
  public FloorFacility getSelectedFacility() {
    return selectedFacilityProperty.get();
  }

  /**
   * Returns a property for the selected FloorFacility.
   *
   * @return a property for the selected FloorFacility
   * @since 100.14.0
   */
  public SimpleObjectProperty<FloorFacility> selectedFacilityProperty() {
    return selectedFacilityProperty;
  }

  /**
   * Sets the selected FloorFacility if it is not already selected. In addition, associated data is reset to keep the
   * data in-sync, and the Viewpoint is set to the extent of the selected FloorFacility's geometry.
   *
   * @param newValue the selected floor facility.
   * @since 100.14.0
   */
  public void setSelectedFacility(FloorFacility newValue) {
    selectedFacilityProperty.set(newValue);
  }

  /**
   * Handles changes to the selected facility property by keeping other data in-sync.
   *
   * @param oldValue the previously selected facility
   * @param newValue the selected facility
   * @since 100.14.0
   */
  public void handleUpdateSelectedFacility(FloorFacility oldValue, FloorFacility newValue) {
    if (newValue != oldValue) {
      try {
        if (newValue != null && !blockViewpointUpdate) {
          getGeoView().setViewpoint(new Viewpoint(newValue.getGeometry().getExtent()));
        }

        if (newValue == null) {
          setSelectedLevel(null);
        } else {
          if (newValue.getSite() != getSelectedSite()) {
            siteSetViaFacility = true;
            setSelectedSite(newValue.getSite());
          }
          if (!facilitySetViaLevel) {
            if (newValue.getLevels().isEmpty()) {
              setSelectedLevel(null);
            } else {
              // if the currently selected facility has associated levels, auto-select the ground floor.
              // the floors information model requires ground floor to be at vertical order 0.
              // if the currently selected facility does not have any levels, reset the selected level to null.
              setSelectedLevel(newValue.getLevels().stream().filter(level -> level.getVerticalOrder() == 0).findFirst().orElse(null));
            }
          }
        }
      } finally {
        blockViewpointUpdate = false;
        facilitySetViaLevel = false;
      }
    }
  }

  /**
   * Gets the selected FloorLevel.
   *
   * @return the selected floor level
   * @since 100.14.0
   */
  public FloorLevel getSelectedLevel() {
    return selectedLevelProperty.get();
  }

  /**
   * Returns property for the selected FloorLevel.
   *
   * @return a property for the selected FloorLevel
   * @since 100.14.0
   */
  public SimpleObjectProperty<FloorLevel> selectedLevelProperty() {
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
    selectedLevelProperty.set(newValue);
  }

  /**
   * Handles changes to the selected level property by keeping other data in-sync.
   *
   * @param oldValue the previously selected level
   * @param newValue the selected level
   * @since 100.14.0
   */
  public void handleUpdateSelectedLevel(FloorLevel oldValue, FloorLevel newValue) {
    if (newValue != oldValue) {
      if (newValue != null) {
        if (newValue.getFacility() != getSelectedFacility()) {
          facilitySetViaLevel = true;
          setSelectedFacility(newValue.getFacility());
        }
        filterLevelsVisibility();
      } else {
        setLevelVisibilityToDefault();
      }
    }
  }

  /**
   * Gets the FloorManager from the GeoModel attached to the GeoView. Both the GeoModel and FloorManager must be loaded
   * in order to access the attached data. In addition, the GeoModel must be floor-aware. A message is logged if the
   * GeoModel is not floor-aware.
   *
   * @since 100.14.0
   */
  private void setupFloorManager() {
    GeoModel geoModel;
    if (getGeoView() instanceof MapView) {
      // if the GeoView is a MapView get the ArcGISMap
      geoModel = ((MapView) getGeoView()).getMap();
    } else {
      geoModel = ((SceneView) getGeoView()).getArcGISScene();
    }

    if (geoModel != null) {
      geoModel.addDoneLoadingListener(() -> {
        if (geoModel.getLoadStatus() == LoadStatus.LOADED) {
          // check the GeoModel is floor aware
          if (geoModel.getFloorManager() != null) {
            var floorManager = geoModel.getFloorManager();
            floorManager.addDoneLoadingListener(() -> {
              if (floorManager.getLoadStatus() == LoadStatus.LOADED) {
                // set the loaded floor manager to the floor manager property
                floorManagerProperty.set(floorManager);
              } else if (floorManager.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
                Logger logger = Logger.getLogger(FloorFilter.class.getName());
                logger.warning("The FloorManager failed to load with error: " + floorManager.getLoadError().getCause());
              }
            });
            // load the floor manager if it is not already loaded
            if (floorManager.getLoadStatus() != LoadStatus.LOADED) {
              floorManager.loadAsync();
            }
          } else {
            // if not floor aware set the floor manager property to null
            floorManagerProperty.set(null);
            Logger logger = Logger.getLogger(FloorFilter.class.getName());
            logger.info("The GeoModel attached to the provided GeoView is not Floor Aware. FloorFilter will not " +
              "be displayed. Call FloorFilter.refresh() after updating the GeoModel to try again.");
          }
        } else if (geoModel.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
          Logger logger = Logger.getLogger(FloorFilter.class.getName());
          logger.warning("The GeoModel failed to load with error: " + geoModel.getLoadError().getCause());
        }
      });
      if (geoModel.getLoadStatus() != LoadStatus.LOADED) {
        // load the geomodel if it is not already loaded
        geoModel.loadAsync();
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
   * For all facilities, set the visibility of the floor level at vertical order zero, the default, to visible, and all
   * other levels to false.
   *
   * @since 100.14.0
   */
  public void setLevelVisibilityToDefault() {
    if (getFacilities() != null) {
      getFacilities().forEach(facility -> facility.getLevels().forEach(level -> level.setVisible(level.getVerticalOrder() == 0)));
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

  /**
   * Used to update the selected site and/or facility depending on the observed ViewPoint and the AutomaticSelectionMode
   * being used.
   *
   * @since 100.14.0
   */
  private void updateSelectionIfNeeded() {
    // get the observed viewpoint
    Viewpoint observedViewpoint = getGeoView().getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE);

    if (observedViewpoint != null && getFloorManager() != null &&
      automaticSelectionModeProperty.get() != AutomaticSelectionMode.NEVER && !selectedSiteProperty.isBound() &&
      !selectedFacilityProperty.isBound() && !selectedLevelProperty.isBound()) {

      if (getFloorManager().getSiteLayer() != null) {
        // default to map-authored Site MinScale, or if MinScale is 0 default to 4300
        double targetScale = getFloorManager().getSiteLayer().getMinScale();
        if (targetScale == 0) {
          targetScale = 4300;
        }

        // if viewpoint is out of range, reset selection (if not non-clearing)
        if (observedViewpoint.getTargetScale() > targetScale) {
          if (automaticSelectionModeProperty.get() == AutomaticSelectionMode.ALWAYS) {
            blockViewpointUpdate = true;
            selectedSiteProperty.set(null);
          }
          // only take further action if viewpoint is within minimum scale
        } else {
          // if the centerpoint is within a site's geometry, select that site
          Geometry siteGeometry;
          if (!getSites().isEmpty() && observedViewpoint.getTargetGeometry().getSpatialReference() !=
            getSites().get(0).getGeometry().getSpatialReference()) {
            siteGeometry = GeometryEngine.project(observedViewpoint.getTargetGeometry(),
              getSites().get(0).getGeometry().getSpatialReference());
          } else {
            siteGeometry = observedViewpoint.getTargetGeometry();
          }
          var result =
            getSites().stream().filter(site -> site.getGeometry().getExtent() != null &&
              GeometryEngine.intersects(site.getGeometry().getExtent(), siteGeometry)).findFirst().orElse(null);
          if (result != null) {
            blockViewpointUpdate = true;
            selectedSiteProperty.set(result);
          } else if (automaticSelectionModeProperty.get() == AutomaticSelectionMode.ALWAYS) {
            blockViewpointUpdate = true;
            selectedSiteProperty.set(null);
          }
        }
      }

      if (getFloorManager().getFacilityLayer() != null) {
        // move onto facility selection
        // default to map-authored Facility MinScale, or if MinScale is 0 default to 1500
        double targetScale = getFloorManager().getFacilityLayer().getMinScale();
        if (targetScale == 0) {
          targetScale = 1500;
        }
        // only take action if the viewpoint is within minimum scale
        if (observedViewpoint.getTargetScale() <= targetScale) {
          Geometry facilityGeometry;
          if (!getFacilities().isEmpty() && observedViewpoint.getTargetGeometry().getSpatialReference() !=
            getFacilities().get(0).getGeometry().getSpatialReference()) {
            facilityGeometry = GeometryEngine.project(observedViewpoint.getTargetGeometry(),
              getFacilities().get(0).getGeometry().getSpatialReference());
          } else {
            facilityGeometry = observedViewpoint.getTargetGeometry();
          }
          var facilityResult =
            getFacilities().stream().filter(facility -> facility.getGeometry().getExtent() != null &&
                GeometryEngine.intersects(facility.getGeometry().getExtent(), facilityGeometry))
              .findFirst().orElse(null);

          if (facilityResult != null) {
            blockViewpointUpdate = true;
            setSelectedFacility(facilityResult);
          } else if (automaticSelectionModeProperty.get() == AutomaticSelectionMode.ALWAYS) {
            blockViewpointUpdate = true;
            setSelectedFacility(null);
          }
        }
      }
    }
  }
}
