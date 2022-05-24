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

package com.esri.arcgisruntime.toolkit.skins;

import java.util.ArrayList;
import java.util.Comparator;

import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.floor.FloorFacility;
import com.esri.arcgisruntime.mapping.floor.FloorLevel;
import com.esri.arcgisruntime.mapping.floor.FloorSite;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.toolkit.FloorFilter;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

import static javafx.scene.input.MouseEvent.MOUSE_CLICKED;

/**
 * <p>
 * A skin for displaying a {@link FloorFilter}.
 *
 * <p>
 * The skin is only configured and displayed if there is a valid FloorManager loaded in the data model.
 *
 * <p>
 * FloorSite and FloorFacility selection occurs within titled panes containing list views of the sites and facilities
 * attached to the floor manager. FloorLevels are displayed in a list view when a facility that has levels is selected.
 *
 * <p>
 * If there is only 1 site on the floor manager, it is automatically selected and the sites browser will not be
 * displayed.
 *
 * <p>
 * Styles can be customized using the set style classes. A default style class is set in the {@link FloorFilter} Control.
 *
 * @since 100.14.0
 */
public class FloorFilterSkin extends SkinBase<FloorFilter> {

  // used to manage list view content
  private static final double MAX_NO_OF_ROWS = 5.0;
  private static final double CELL_SIZE = 26.0;

  private static final double PREF_WIDTH = 220.0;

  private final FloorFilter skinnable;

  // main panes and scroll pane to manage content if height overruns
  private final VBox floorFilterPane = new VBox();
  private final ScrollPane scrollPane = new ScrollPane();
  private final VBox contentPane = new VBox();

  // sites and facilities section
  private final VBox sitesAndFacilitiesVBox = new VBox();

  private final TitledPane sitesTitledPane = new TitledPane();
  private final VBox sitesVBox = new VBox();
  private final Label sitesHeading = new Label("Select a site");
  private final TextField sitesFilterTextField = new TextField();
  private final ListView<FloorSite> sitesListView = new ListView<>();
  private final CheckBox allSitesCheckbox = new CheckBox("All sites");

  private final TitledPane facilitiesTitledPane = new TitledPane();
  private final VBox facilitiesVBox = new VBox();
  private final Label facilityHeading = new Label("Select a facility");
  private final TextField facilitiesFilterTextField = new TextField();
  private final ListView<FloorFacility> facilitiesListView = new ListView<>();

  // levels section
  private final VBox levelsVBox = new VBox();
  private final Label levelsHeading = new Label("Levels:");
  private final ListView<FloorLevel> levelsListView = new ListView<>();
  private final CheckBox allLevelsCheckbox = new CheckBox("Show all levels");

  // boolean properties for controlling visibility of sections
  // property to toggle the visibility of the sites titled pane
  private final SimpleBooleanProperty showSitesProperty = new SimpleBooleanProperty();
  // property to handle selection of all sites checkbox
  private final SimpleBooleanProperty allSitesProperty = new SimpleBooleanProperty();
  // property to toggle the visibility of the facilities titled pane
  private final SimpleBooleanProperty showFacilitiesProperty = new SimpleBooleanProperty();
  // property to toggle the visibility of the levels vbox
  private final SimpleBooleanProperty showLevelsProperty = new SimpleBooleanProperty(false);
  // property to toggle the visibility of the all levels option for scenes only
  private final SimpleBooleanProperty sceneViewProperty = new SimpleBooleanProperty();

  // control for zoom functionality
  private final Button zoomButton = new Button("Zoom to");

  // data
  private final ObservableList<FloorSite> sites = FXCollections.observableArrayList();
  private final FilteredList<FloorSite> filteredSites = new FilteredList<>(sites);

  private final ObservableList<FloorFacility> facilities = FXCollections.observableArrayList();
  private final FilteredList<FloorFacility> filteredFacilities = new FilteredList<>(facilities);

  private final ObservableList<FloorLevel> levels = FXCollections.observableArrayList();

  /**
   * Creates an instance of the skin.
   *
   * @param control the {@link FloorFilter} control this skin represents
   * @since 100.14.0
   */
  public FloorFilterSkin(FloorFilter control) {
    super(control);

    skinnable = getSkinnable();

    // set boolean property to true if the geo view in the control is a scene view
    sceneViewProperty.set(control.getGeoView() instanceof SceneView);

    // add a listener on the floor manager property
    // the UI will only display and be configured if a floor manager is loaded successfully in the control
    control.floorManagerProperty().addListener(observable -> setup());

    // calls setup initially incase the floor manager is already set and loaded
    setup();
  }

  /**
   * Configures properties that relate to how the UI should display depending on the floor manager and related data,
   * and triggers the UI to draw if it is the first time a floor manager has been set. If the floor manager changes as
   * a result of the {@link FloorFilter#refresh()} method being called, the data will be re-configured within the existing UI.
   *
   * @since 100.14.0
   */
  private void setup() {
    if (skinnable.getFloorManager() != null) {
      // if the floor filter has not been drawn already, set up the UI elements
      if (floorFilterPane.getChildren().isEmpty()) {
        setupPanes();
        setupZoomButton();
        setupSites();
        setupFacilities();
        setupLevels();
      }

      var controlSites = getSites();
      // if there are no sites, don't show the sites browser
      if (controlSites.isEmpty()) {
        showSitesProperty.set(false);
        // if there is 1 site, set it as the selected site and don't show the sites browser
      } else if (controlSites.size() == 1) {
        // only if the properties are not bound
        if (!skinnable.selectedSiteProperty().isBound() && !skinnable.selectedFacilityProperty().isBound() &&
          !skinnable.selectedLevelProperty().isBound()) {
          skinnable.setSelectedSite(controlSites.get(0));
        }
        showSitesProperty.set(false);
        // if there are multiple sites, show the sites browser and set the data to the sites list
      } else {
        showSitesProperty.set(true);
        sites.setAll(controlSites);
        // sort the sites by name
        sites.sort(Comparator.comparing(FloorSite::getName));
      }

      var controlFacilities = getFacilities();
      // if there are no facilities, don't show the facilities browser
      if (controlFacilities.isEmpty()) {
        showFacilitiesProperty.set(false);
        // if there are multiple facilities, show the facilities browser and set the data to the facilities list.
      } else {
        showFacilitiesProperty.set(true);
        facilities.setAll(controlFacilities);
        // sort the facilities by name
        facilities.sort(Comparator.comparing(FloorFacility::getName));
        // if there is 1 facility, automatically select it
        if (controlFacilities.size() == 1 && !skinnable.selectedSiteProperty().isBound() && !skinnable.selectedFacilityProperty().isBound() &&
          !skinnable.selectedLevelProperty().isBound()) {
          skinnable.setSelectedFacility(controlFacilities.get(0));
        }
      }

      updateUI();
    } else {
      clearUI();
    }
  }

  /**
   * Configures the main content panes: sets their children, sets style classes and sets other properties.
   *
   * @since 100.14.0
   */
  private void setupPanes() {
    // sets up the site browser and adds to the sites titled pane
    sitesVBox.getChildren().addAll(sitesHeading, sitesFilterTextField, sitesListView, allSitesCheckbox);
    sitesHeading.getStyleClass().add("floor-filter-heading");
    sitesTitledPane.setContent(sitesVBox);
    sitesTitledPane.getStyleClass().add("floor-filter-sites");
    // sets titled pane properties to manage behavior
    sitesTitledPane.setExpanded(true);
    sitesTitledPane.setAnimated(false);
    sitesTitledPane.visibleProperty().bind(showSitesProperty);
    sitesTitledPane.managedProperty().bind(sitesTitledPane.visibleProperty());
    sitesTitledPane.setText("Sites");

    // sets up the facility browser and adds to the facilities titled pane
    facilitiesVBox.getChildren().addAll(facilityHeading, facilitiesFilterTextField, facilitiesListView);
    facilityHeading.getStyleClass().add("floor-filter-heading");
    facilitiesTitledPane.setContent(facilitiesVBox);
    facilitiesTitledPane.getStyleClass().add("floor-filter-facilities");
    // set titled pane properties to manage behavior
    facilitiesTitledPane.setAnimated(false);
    facilitiesTitledPane.visibleProperty().bind(showFacilitiesProperty);
    facilitiesTitledPane.managedProperty().bind(facilitiesTitledPane.visibleProperty());
    facilitiesTitledPane.setText("Facilities");
    facilitiesTitledPane.setExpanded(false);

    // sets the sites and facilities titled panes to a vbox
    sitesAndFacilitiesVBox.getChildren().addAll(sitesTitledPane, facilitiesTitledPane);
    sitesAndFacilitiesVBox.getStyleClass().add("floor-filter-sites-facilities");

    // sets up the levels browser
    levelsVBox.getChildren().addAll(levelsHeading, levelsListView, allLevelsCheckbox);
    levelsVBox.getStyleClass().add("floor-filter-levels");
    // only display the levels browser if a selected facility has levels
    levelsVBox.visibleProperty().bind(showLevelsProperty);
    levelsVBox.managedProperty().bind(levelsVBox.visibleProperty());

    // adds all sections and controls to the content pane vbox and sets it to a scroll pane to manage overrun on height
    contentPane.getChildren().addAll(sitesAndFacilitiesVBox, levelsVBox, zoomButton);
    scrollPane.setContent(contentPane);
    // prevent horizontal scrolling on the scroll pane
    scrollPane.setFitToWidth(true);
    // adds the scroll pane to the main vbox and sets that to the skin
    floorFilterPane.getChildren().add(scrollPane);
    floorFilterPane.getStyleClass().add("floor-filter-pane");
    getChildren().add(floorFilterPane);
  }

  /**
   * Configures the zoom button.
   *
   * @since 100.14.0
   */
  private void setupZoomButton() {
    // add style classes
    zoomButton.getStyleClass().add("floor-filter-zoom-button");
    // bind the width of the button to the pane
    zoomButton.prefWidthProperty().bind(contentPane.widthProperty());
    // configure actions based on what data is selected
    zoomButton.setOnAction(e -> {
      var selectedSite = skinnable.getSelectedSite();
      var selectedFacility = skinnable.getSelectedFacility();
      var geoView = skinnable.getGeoView();
      // if there is a selected site but no selected facility, zoom to the selected site
      if (selectedSite != null && selectedFacility == null) {
        geoView.setViewpoint(new Viewpoint(selectedSite.getGeometry().getExtent()));
      } else if (selectedFacility != null) {
        // zoom to the selected facility
        geoView.setViewpoint(new Viewpoint(selectedFacility.getGeometry().getExtent()));
      }
    });
  }

  /**
   * Sets up elements specific to the sites view and data, including the TextField to filter by name, ListView to
   * display the sites, and CheckBox to select all sites.
   *
   * @since 100.14.0
   */
  private void setupSites() {
    // handle changes to selected site
    skinnable.selectedSiteProperty().addListener((observable, oldValue, newValue) -> {
      if (showSitesProperty.get()) {
        // filter the facilities
        if (!allSitesProperty.get()) {
          if (newValue == null) {
            filteredFacilities.setPredicate(facility -> facility.getName().toLowerCase().contains(
              facilitiesFilterTextField.getText().toLowerCase()));
          } else {
            filteredFacilities.setPredicate(facility -> facility.getSite() == newValue && facility.getName()
              .toLowerCase().contains(facilitiesFilterTextField.getText().toLowerCase()));
          }
        }
        // if the site is not already selected in the UI select it
        if (newValue != sitesListView.getSelectionModel().getSelectedItem()) {
          sitesListView.getSelectionModel().select(newValue);
        }
        updateUI();
      }
    });

    // configure the text field that filters sites by name
    sitesFilterTextField.setPromptText("Filter sites by name");
    sitesFilterTextField.textProperty().addListener(obs -> {
      String filter = sitesFilterTextField.getText();
      if (filter == null || filter.length() == 0) {
        filteredSites.setPredicate(site -> true);
      } else {
        filteredSites.setPredicate(site -> site.getName().toLowerCase().contains(filter.toLowerCase()));
      }
    });

    // bind the sites listview interactivity to the all sites checkbox selection state
    sitesListView.disableProperty().bind(allSitesCheckbox.selectedProperty());
    sitesFilterTextField.disableProperty().bind(allSitesCheckbox.selectedProperty());
    sitesListView.setPlaceholder(new Label("No sites found"));

    // set the filtered list on the list view and configure a cell factory to display the site names
    sitesListView.setMinHeight(CELL_SIZE);
    sitesListView.setItems(filteredSites);
    sitesListView.setCellFactory(v -> {
      ListCell<FloorSite> cell = new ListCell<>() {
        @Override
        protected void updateItem(FloorSite site, boolean empty) {
          super.updateItem(site, empty);
          if (empty || site == null) {
            setText(null);
          } else {
            setText(site.getName());
          }
        }
      };
      // if an already selected site is clicked again, still update the viewpoint
      cell.setOnMouseClicked(e -> {
        if (skinnable.getSelectedSite() != null) {
          skinnable.getGeoView().setViewpoint(new Viewpoint(skinnable.getSelectedSite().getGeometry().getExtent()));
        }
        updateUI();
      });
      return cell;
    });

    // if the properties are not bound and site is not already selected, set the site to the control
    sitesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != skinnable.selectedSiteProperty().get() && !skinnable.selectedSiteProperty().isBound()
        && !skinnable.selectedFacilityProperty().isBound() && !skinnable.selectedLevelProperty().isBound()) {
        skinnable.setSelectedSite(newValue);
      }
      updateUI();
    });

    // list view deselection sets site to null unless any of the properties are bound
    sitesListView.addEventFilter(MOUSE_CLICKED, click -> {
      if (click.isControlDown()) {
        if (!skinnable.selectedSiteProperty().isBound() && !skinnable.selectedFacilityProperty().isBound()
          && !skinnable.selectedLevelProperty().isBound()) {
          skinnable.setSelectedSite(null);
          updateUI();
        } else {
          // ignore deselection if property is bound
          click.consume();
        }
      }
    });

    // handle the all sites property
    allSitesProperty.addListener(observable -> handleIsAllSitesPropertyChanged());

    // when all sites checkbox is selected, update the isAllSitesProperty value
    allSitesCheckbox.selectedProperty().addListener((observable, oldValue, newValue) ->
      allSitesProperty.set(allSitesCheckbox.isSelected()));

    // configure listener that controls the height of the list view, or the point at which the list view will become
    // scrollable. This is calculated using the cell size and maximum number of desired rows before scroll. If the data
    // is smaller than the maximum number of rows, then the number of rows will be the size of the data.
    filteredSites.addListener((ListChangeListener<? super FloorSite>) observable ->
      sitesListView.setPrefHeight(
        filteredSites.size() < MAX_NO_OF_ROWS ? filteredSites.size() * CELL_SIZE : MAX_NO_OF_ROWS * CELL_SIZE));
  }

  /**
   * Sets up elements specific to the facilities view and data, including the TextField to filter by name and ListView
   * to display the facilities.
   *
   * @since 100.14.0
   */
  private void setupFacilities() {
    // handle changes to selected facility
    skinnable.selectedFacilityProperty().addListener((observable, oldValue, newValue) -> {
      if (showFacilitiesProperty.get()) {
        // filter the levels
        if (getSkinnable().getSelectedFacility() != null && !getSkinnable().getSelectedFacility().getLevels().isEmpty()) {
          levels.clear();
          var facilityLevels = new ArrayList<>(getSkinnable().getSelectedFacility().getLevels());
          facilityLevels.sort(Comparator.comparing(FloorLevel::getVerticalOrder).reversed());
          levels.setAll(facilityLevels);
          levelsListView.getSelectionModel().select(getSkinnable().getSelectedLevel());
        }
        // if the facility is not already selected in the UI select it
        if (newValue != facilitiesListView.getSelectionModel().getSelectedItem()) {
          facilitiesListView.getSelectionModel().select(newValue);
        }
        updateUI();
      }
    });

    // configure the text field that filters facilities by name
    facilitiesFilterTextField.setPromptText("Filter facilities by name");
    facilitiesFilterTextField.textProperty().addListener(obs -> {
      var selectedSite = skinnable.getSelectedSite();
      String filter = facilitiesFilterTextField.getText();
      if (filter == null || filter.length() == 0) {
        if (selectedSite != null) {
          filteredFacilities.setPredicate(facility -> facility.getSite() == selectedSite);
        } else {
          filteredFacilities.setPredicate(facility -> true);
        }
      } else {
        if (!allSitesProperty.get()) {
          filteredFacilities.setPredicate(facility -> facility.getName().toLowerCase().contains(filter.toLowerCase()) &&
            facility.getSite() == selectedSite);
        } else {
          filteredFacilities.setPredicate(facility -> facility.getName().toLowerCase().contains(filter.toLowerCase()));
        }
      }
    });

    // set the filtered list on the list view and configure a cell factory to display the facility names
    facilitiesListView.setMinHeight(CELL_SIZE);
    facilitiesListView.setItems(filteredFacilities);
    facilitiesListView.setPlaceholder(new Label("No facilities found"));
    facilitiesListView.setCellFactory(v -> {
      ListCell<FloorFacility> cell = new ListCell<>() {
        @Override
        protected void updateItem(FloorFacility facility, boolean empty) {
          super.updateItem(facility, empty);
          if (empty || facility == null) {
            setText(null);
          } else {
            if (allSitesProperty.get()) {
              setText(facility.getName() + " (" + facility.getSite().getName() + ")");
            } else {
              setText(facility.getName());
            }
          }
        }
      };
      // if an already selected facility is clicked again, still update the viewpoint
      cell.setOnMouseClicked(e -> {
        if (skinnable.getSelectedFacility() != null) {
          skinnable.getGeoView().setViewpoint(new Viewpoint(skinnable.getSelectedFacility().getGeometry().getExtent()));
        }
        updateUI();
      });
      return cell;
    });

    // if the properties are not bound and the facility is not already selected, set the facility to the control
    facilitiesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != null && newValue != skinnable.getSelectedFacility() && !skinnable.selectedSiteProperty().isBound()
        && !skinnable.selectedFacilityProperty().isBound() && !skinnable.selectedLevelProperty().isBound()) {
        skinnable.setSelectedFacility(newValue);
      }
      updateUI();
    });

    // list view deselection sets facility to null unless any of the properties are bound
    facilitiesListView.addEventFilter(MOUSE_CLICKED, click -> {
      if (click.isControlDown()) {
        if (!skinnable.selectedSiteProperty().isBound() && !skinnable.selectedFacilityProperty().isBound()
          && !skinnable.selectedLevelProperty().isBound()) {
          skinnable.setSelectedFacility(null);
          updateUI();
        } else {
          // ignore deselection if property is bound
          click.consume();
        }
      }
    });

    // configure listener that controls the height of the list view, or the point at which the list view will become
    // scrollable. This is calculated using the cell size and maximum number of desired rows before scroll. If the data
    // is smaller than the maximum number of rows, then the number of rows will be the size of the data.
    filteredFacilities.addListener((ListChangeListener<? super FloorFacility>) observable ->
      facilitiesListView.setPrefHeight(filteredFacilities.size() < MAX_NO_OF_ROWS ?
        filteredFacilities.size() * CELL_SIZE : MAX_NO_OF_ROWS * CELL_SIZE));
  }

  /**
   * Sets up elements specific to the levels view and data, including the CheckBox to display all levels if the GeoView
   * is a SceneView and the ListView to display the levels.
   *
   * @since 100.14.0
   */
  private void setupLevels() {
    // keep the level list view selection aligned with control selection
    skinnable.selectedLevelProperty().addListener((observable, oldValue, newValue) -> {
      // if the level is not already selected in the UI select it
      if (newValue != levelsListView.getSelectionModel().getSelectedItem()) {
        levelsListView.getSelectionModel().select(newValue);
      }
    });

    // only display the all levels checkbox if the view is a SceneView
    allLevelsCheckbox.visibleProperty().bind(sceneViewProperty);
    allLevelsCheckbox.managedProperty().bind(allLevelsCheckbox.visibleProperty());

    // toggles the visibility of levels based on selection
    allLevelsCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
      if (allLevelsCheckbox.isSelected()) {
        getLevels().forEach(level -> level.setVisible(true));
      } else if (!allLevelsCheckbox.isSelected()) {
        filterLevelsVisibility();
      }
      updateUI();
    });

    // set the filtered list on the list view and configure a cell factory to display the level long names by default
    levelsListView.setMinHeight(CELL_SIZE);
    levelsListView.setItems(levels);
    levelsListView.setCellFactory(v -> new ListCell<>() {
      @Override
      protected void updateItem(FloorLevel level, boolean empty) {
        super.updateItem(level, empty);
        if (empty || level == null) {
          setText(null);
        } else {
          setText(level.getLongName());
        }
      }
    });

    // if the properties are not bound and level is not already selected, set the level to the control
    levelsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != null && newValue != skinnable.getSelectedLevel() && !skinnable.selectedSiteProperty().isBound()
        && !skinnable.selectedFacilityProperty().isBound() && !skinnable.selectedLevelProperty().isBound()) {
        skinnable.setSelectedLevel(newValue);
      }
      updateUI();
    });

    // list view deselection sets level to null unless property is bound
    levelsListView.addEventFilter(MOUSE_CLICKED, click -> {
      if (click.isControlDown()) {
        if (!skinnable.selectedSiteProperty().isBound() && !skinnable.selectedFacilityProperty().isBound()
          && !skinnable.selectedLevelProperty().isBound()) {
          skinnable.setSelectedLevel(null);
          updateUI();
        } else {
          // ignore deselection if property is bound
          click.consume();
        }
      }
    });

    // configure listener that controls the height of the list view, or the point at which the list view will become
    // scrollable. This is calculated using the cell size and maximum number of desired rows before scroll. If the data
    // is smaller than the maximum number of rows, then the number of rows will be the size of the data.
    levels.addListener((ListChangeListener<? super FloorLevel>) observable ->
      levelsListView.setPrefHeight(levels.size() < MAX_NO_OF_ROWS ?
        levels.size() * CELL_SIZE : MAX_NO_OF_ROWS * CELL_SIZE));
  }

  /**
   * Ensures the UI components match the data set in the control. This includes labels being kept up to date.
   *
   * @since 100.14.0
   */
  private void updateUI() {
    var selectedSite = skinnable.getSelectedSite();
    var selectedFacility = skinnable.getSelectedFacility();

    // force site selection before facility selection
    if (showSitesProperty.get() && showFacilitiesProperty.get() && !allSitesProperty.get() && selectedSite == null) {
      facilitiesTitledPane.setDisable(true);
      facilitiesTitledPane.setExpanded(false);
    } else {
      facilitiesTitledPane.setDisable(false);
    }

    if (selectedSite == null) {
      sitesHeading.setText("Select a site");
    } else {
      sitesHeading.setText(selectedSite.getName());
    }

    if (selectedFacility == null) {
      facilityHeading.setText("Select a facility");
      showLevelsProperty.set(false);
      // if the geoview is a scene reset the all levels checkbox when no facility is selected
      if (sceneViewProperty.get()) {
        allLevelsCheckbox.setSelected(false);
      }
    } else {
      showLevelsProperty.set(!selectedFacility.getLevels().isEmpty());
      facilityHeading.setText(selectedFacility.getName());
    }

    // if the geoview is a scene view ensure level visibility is correct
    if (sceneViewProperty.get()) {
      if (allLevelsCheckbox.isSelected()) {
        getLevels().forEach(level -> level.setVisible(true));
        levelsListView.setDisable(true);
      } else {
        filterLevelsVisibility();
        levelsListView.setDisable(false);
      }
    }

    // only enable the zoom button if there is relevant data selected
    zoomButton.setDisable(selectedSite == null && selectedFacility == null);
  }

  /**
   * Handle changes to the value of the isAllSitesProperty. This is toggled by the all sites checkbox option on the
   * sites browser. It filters the list of facilities to show all facilities attached to the floor manager, as opposed
   * to facilities attached to a selected site.
   *
   * @since 100.14.0
   */
  private void handleIsAllSitesPropertyChanged() {
    if (allSitesProperty.get()) {
      // filter the facilities list only by the text field input
      filteredFacilities.setPredicate(facility ->
        facility.getName().toLowerCase().contains(facilitiesFilterTextField.getText().toLowerCase()));
    } else {
      // filter by the selected site as well as text field input
      filteredFacilities.setPredicate(facility -> facility.getSite() == skinnable.getSelectedSite() &&
        facility.getName().toLowerCase().contains(facilitiesFilterTextField.getText().toLowerCase()));
    }
    updateUI();
  }

  /**
   * Sets all floor levels with the same vertical order as the selected level to be visible.
   *
   * @since 100.14.0
   */
  private void filterLevelsVisibility() {
    var selectedLevel = skinnable.getSelectedLevel();
    if (selectedLevel != null) {
      getLevels().forEach(level -> level.setVisible(level.getVerticalOrder() == selectedLevel.getVerticalOrder()));
    }
  }

  /**
   * Gets all the FloorSites associated with the FloorManager.
   *
   * @return an observable list of FloorSites, empty if FloorManager is null
   * @since 100.14.0
   */
  private ObservableList<FloorSite> getSites() {
    var floorManager = skinnable.getFloorManager();
    ObservableList<FloorSite> floorManagerSites = FXCollections.observableArrayList();
    if (floorManager != null) {
      floorManagerSites.setAll(floorManager.getSites());
    } else {
      floorManagerSites.clear();
    }
    return floorManagerSites;
  }

  /**
   * Gets all the FloorFacilities associated with the FloorManager.
   *
   * @return an observable list of FloorFacilities, empty if FloorManager is null
   * @since 100.14.0
   */
  private ObservableList<FloorFacility> getFacilities() {
    var floorManager = skinnable.getFloorManager();
    ObservableList<FloorFacility> floorManagerFacilities = FXCollections.observableArrayList();
    if (floorManager != null) {
      floorManagerFacilities.setAll(floorManager.getFacilities());
    } else {
      floorManagerFacilities.clear();
    }
    return floorManagerFacilities;
  }

  /**
   * Gets all the FloorLevels associated with the FloorManager.
   *
   * @return an observable list of FloorLevels, empty if FloorManager is null
   * @since 100.14.0
   */
  private ObservableList<FloorLevel> getLevels() {
    var floorManager = skinnable.getFloorManager();
    ObservableList<FloorLevel> floorManagerLevels = FXCollections.observableArrayList();
    if (floorManager != null) {
      floorManagerLevels.setAll(floorManager.getLevels());
    } else {
      floorManagerLevels.clear();
    }
    return floorManagerLevels;
  }

  /**
   * Clears all UI Panes.
   *
   * @since 100.14.0
   */
  private void clearUI() {
    getChildren().clear();
    floorFilterPane.getChildren().clear();
    contentPane.getChildren().clear();
    sitesAndFacilitiesVBox.getChildren().clear();
    sitesVBox.getChildren().clear();
    facilitiesVBox.getChildren().clear();
    levelsVBox.getChildren().clear();
  }

  @Override
  protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double
    leftInset) {
    return PREF_WIDTH;
  }

  @Override
  protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double
    leftInset) {
    return computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
  }

  @Override
  protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double
    leftInset) {
    return computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
  }

  @Override
  protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double
    leftInset) {
    return computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
  }

  @Override
  protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double
    leftInset) {
    return computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
  }
}
