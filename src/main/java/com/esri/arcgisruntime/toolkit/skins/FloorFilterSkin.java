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

import java.util.Comparator;

import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.floor.FloorFacility;
import com.esri.arcgisruntime.mapping.floor.FloorLevel;
import com.esri.arcgisruntime.mapping.floor.FloorSite;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.toolkit.FloorFilter;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
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

/**
 * <p>A skin for displaying a {@link FloorFilter}.</p>
 *
 * <p>The skin is only configured and displayed if there is a valid FloorManager loaded in the data model.</p>
 *
 * <p>FloorSite and FloorFacility selection occurs within titled panes containing list views of the sites and facilities
 * attached to the floor manager. FloorLevels are displayed in a list view when a facility is selected.</p>
 *
 * <p>If there is only 1 site on the floor manager, it is automatically selected and the sites browser will not be
 * displayed. If there is only 1 facility on the floor manager, it is automatically selected and the facilities
 * browser is still visible.</p>
 *
 * <p>Styles can be customized using the CSS classes laid out below:</p>
 *
 * <p>Customize the main wrapper including the border:</p>
 * <pre>
 * .floor-filter-wrapper {
 *   -fx-background-color: white;
 *   -fx-border-color: grey;
 *   -fx-border-width: 2;
 * }
 * </pre>
 *
 * <p>Sites:</p>
 * <ul>
 * <li>.floor-filter-sites: styles the titled pane for the sites.</li>
 * <li>.floor-filter-sites VBox: styles the sites VBox wrapper.</li>
 * <li>.floor-filter-sites ListView: styles the sites ListView.</li>
 * </ul>
 * <p>Facilities:</p>
 * <ul>
 * <li>.floor-filter-facilities: styles the titled pane for the facilities.</li>
 * <li>.floor-filter-facilities VBox: styles the facilities VBox wrapper.</li>
 * <li>.floor-filter-facilities ListView: styles the facilities ListView.</li>
 * </ul>
 * <p>Levels:</p>
 * <ul>
 * <li>.floor-filter-levels: styles the levels VBox.</li>
 * <li>.floor-filter-sites ListView: styles the levels ListView.</li>
 * </ul>
 * <p>Other:</p>
 * <ul>
 * <li>floor-filter-zoom-button: styles the zoom button.</li>
 * <li>floor-filter-collapse-button: styles the collapse button.</li>
 * </ul>
 *
 * @since 100.14.0
 */
public class FloorFilterSkin  extends SkinBase<FloorFilter> {

  private static final double PREF_WIDTH = 200.0;
  private static final double COLLAPSED_WIDTH = 100.0;

  // used to manage list view content
  private static final double MAX_NO_OF_ROWS = 5.0;
  private static final double CELL_SIZE = 26.0;

  // main wrapper and scroll pane to manage content if height overruns
  private final VBox floorFilterWrapper = new VBox();
  private final ScrollPane scrollPane = new ScrollPane();
  private final VBox contentPane = new VBox();

  // sites and facilities section
  private final VBox sitesAndFacilitiesVBox = new VBox();

  private final TitledPane sitesTitledPane = new TitledPane();
  private final VBox sitesVBox = new VBox();
  private final Label sitesHeading = new Label("Select a site");
  private final TextField sitesFilterTextField = new TextField();
  private final ListView<FloorSite> sitesListView = new ListView<>();
  private final CheckBox allSitesCheckbox = new CheckBox("Select all sites");

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
  private final SimpleBooleanProperty isShowSitesProperty = new SimpleBooleanProperty();
  // property to handle selection of all sites checkbox
  private final SimpleBooleanProperty isAllSitesProperty = new SimpleBooleanProperty();
  // property to toggle the visibility of the facilities titled pane
  private final SimpleBooleanProperty isShowFacilitiesProperty = new SimpleBooleanProperty();
  // property to toggle the visibility of the levels vbox
  private final SimpleBooleanProperty facilityHasLevelsProperty = new SimpleBooleanProperty(false);
  // property to toggle the collapsed view
  private final SimpleBooleanProperty isCollapsedProperty = new SimpleBooleanProperty();
  // property to toggle the visibility of the all levels option for scenes only
  private final SimpleBooleanProperty isSceneViewProperty = new SimpleBooleanProperty();

  // controls for zoom and collapse functionality
  private final Button zoomButton = new Button("Zoom to");
  private final Button collapseButton = new Button();

  // data
  private final SimpleObjectProperty<FloorSite> selectedSiteProperty = new SimpleObjectProperty<>();
  private final SimpleObjectProperty<FloorFacility> selectedFacilityProperty = new SimpleObjectProperty<>();

  private final ObservableList<FloorSite> sites = FXCollections.observableArrayList();
  private final FilteredList<FloorSite> filteredSites = new FilteredList<>(sites, s -> true);

  private final ObservableList<FloorFacility> facilities = FXCollections.observableArrayList();
  private final FilteredList<FloorFacility> filteredFacilities = new FilteredList<>(facilities, s -> true);

  private final ObservableList<FloorLevel> levels = FXCollections.observableArrayList();
  private final FilteredList<FloorLevel> filteredLevels = new FilteredList<>(levels, s -> true);

  /**
   * Creates an instance of the skin.
   *
   * @param control the {@link FloorFilter} control this skin represents
   * @since 100.14.0
   */
  public FloorFilterSkin(FloorFilter control) {
    super(control);

    // add a listener on the floor manager property
    // the UI will only display and be configured if a floor manager is loaded successfully in the control
    control.floorManagerProperty().addListener(observable -> handleFloorManagerChanged());

    // set boolean property to true if the geo view in the control is a scene view
    isSceneViewProperty.set(getSkinnable().getGeoView() instanceof SceneView);

    // add listeners to selected site/facility/level properties
    control.selectedSiteProperty().addListener(observable -> handleSelectedSiteChanged());
    control.selectedFacilityProperty().addListener(observable -> handleSelectedFacilityChanged());
    control.selectedLevelProperty().addListener(observable -> handleSelectedLevelChanged());
    selectedSiteProperty.bind(control.selectedSiteProperty());
    selectedFacilityProperty.bind(control.selectedFacilityProperty());

    // handle the all sites property
    isAllSitesProperty.addListener(observable -> handleIsAllSitesPropertyChanged());
    // handle the expansion of the titled panes
    sitesTitledPane.expandedProperty().addListener(observable -> handleTitledPaneExpanded(sitesTitledPane));
    facilitiesTitledPane.expandedProperty().addListener(observable -> handleTitledPaneExpanded(facilitiesTitledPane));
    // handle the collapsed view
    isCollapsedProperty.addListener(observable -> handleIsCollapsedPropertyChanged());
  }

  /**
   * Handles changes to the floor manager property value. This configures properties that relate to how the UI should
   * display depending on the data, and triggers the UI to draw if it is the first time a floor manager has been set.
   * If the floor manager changes as a result of the control.refresh() method being called, the data will be configured
   * within the existing UI.
   *
   * @since 100.14.0
   */
  private void handleFloorManagerChanged() {
    if (getSkinnable().getFloorManager() != null) {
      // if the floor filter has not been drawn already, set up the UI elements
      if (floorFilterWrapper.getChildren().isEmpty()) {
        setupUI();
      }

      // if there are no sites, don't show the sites browser
      if (getSkinnable().getSites().isEmpty()) {
        isShowSitesProperty.set(false);
      } else if (getSkinnable().getSites().size() == 1) {
        // if there is 1 site, set it as the selected site and don't show the sites browser
        getSkinnable().setSelectedSite(getSkinnable().getSites().get(0));
        isShowSitesProperty.set(false);
      } else {
        // if there are multiple sites, show the sites browser and set the data to the sites list
        isShowSitesProperty.set(true);
        sites.setAll(getSkinnable().getSites());
        // sort the sites by name
        sites.sort(Comparator.comparing(FloorSite::getName));
      }

      // if there are no facilities, don't show the facilities browser
      if (getSkinnable().getFacilities().isEmpty()) {
        isShowFacilitiesProperty.set(false);
      } else if (getSkinnable().getFacilities().size() == 1) {
        // if there is 1 facility, set it as the selected facility and show the facilities browser
        isShowFacilitiesProperty.set(true);
        facilities.setAll(getSkinnable().getFacilities());
        getSkinnable().setSelectedFacility(getSkinnable().getFacilities().get(0));
      } else {
        // if there are multiple facilities, show the facilities browser and set the data to the facilities list.
        isShowFacilitiesProperty.set(true);
        facilities.setAll(getSkinnable().getFacilities());
        // sort the facilities by name
        facilities.sort(Comparator.comparing(FloorFacility::getName));
        // initiate the facilities list with no filtering applied
        filteredFacilities.setPredicate(facility -> true);
      }

      // set the data to the levels list and sort the levels by vertical order reversed (bottom floor to top floor)
      levels.setAll(getSkinnable().getLevels());
      levels.sort(Comparator.comparing(FloorLevel::getVerticalOrder).reversed());
    }
  }

  /**
   * Handles changes to the selected site property value. This configures properties that relate to how the UI should
   * display depending on the data.
   *
   * @since 100.14.0
   */
  private void handleSelectedSiteChanged() {

    System.out.println("control selected site: " + getSkinnable().getSelectedSite());
    System.out.println("view selected site: " + selectedSiteProperty.get());

    // ensure the selected site is also selected on the list view
    sitesListView.getSelectionModel().select(selectedSiteProperty.get());

    if (selectedSiteProperty.get() != null) {
      // update heading text to show which site is selected
      sitesHeading.setText(selectedSiteProperty.get().getName());

      // filter the facilities by the selected site, assuming the "all sites" checkbox isn't selected
      if (!isAllSitesProperty.get()) {
        filteredFacilities.setPredicate(facility -> facility.getSite() == selectedSiteProperty.get());
      }
    } else {
      // reset site heading if selected site is null
      sitesHeading.setText("Select a site");
    }
  }

  /**
   * Handles changes to the selected facility property value. This configures properties that relate to how the UI
   * should display depending on the data.
   *
   * @since 100.14.0
   */
  private void handleSelectedFacilityChanged() {

    System.out.println("control selected facility: " + getSkinnable().getSelectedFacility());
    System.out.println("view selected facility: " + selectedFacilityProperty.get());

    // ensure the selected facility is also selected on the list view
    facilitiesListView.getSelectionModel().select(selectedFacilityProperty.get());

    if (selectedFacilityProperty.get() != null) {
      // update heading text to show which facility is selected
      facilityHeading.setText(selectedFacilityProperty.get().getName());

      if (selectedFacilityProperty.get().getLevels().isEmpty()) {
        // if the facility doesn't have levels, don't show the list of levels
        facilityHasLevelsProperty.set(false);
      } else {
        // if the facility has levels, show the list of levels and filter by facility
        facilityHasLevelsProperty.set(true);
        filteredLevels.setPredicate(level -> level.getFacility().equals(selectedFacilityProperty.get()));
      }
    } else {
      // ensure levels not shown if facility is null
      facilityHasLevelsProperty.set(false);
      // reset facility heading if facility is null
      facilityHeading.setText("Select a facility");
    }
  }

  /**
   * Handles changes to the selected level property value. This configures properties that relate to how the UI should
   * display depending on the data.
   *
   * @since 100.14.0
   */
  private void handleSelectedLevelChanged() {
    // ensure the selected level is also selected on the list view
    System.out.println("control selected level: " + getSkinnable().getSelectedLevel());
    levelsListView.getSelectionModel().select(getSkinnable().getSelectedLevel());
  }

  /**
   * Sets up each section of the UI.
   *
   * @since 100.14.0
   */
  private void setupUI() {
    setupWrappers();
    setupButtons();
    setupSites();
    setupFacilities();
    setupLevels();
  }

  /**
   * Configures the main content panes: sets their children, sets style classes and sets other properties.
   *
   * @since 100.14.0
   */
  private void setupWrappers() {
    // sets up the site browser and adds to the sites titled pane
    sitesVBox.getChildren().addAll(sitesHeading, sitesFilterTextField, sitesListView, allSitesCheckbox);
    sitesTitledPane.setContent(sitesVBox);
    sitesTitledPane.getStyleClass().add("floor-filter-sites");
    // sets titled pane properties to manager behavior
    sitesTitledPane.setExpanded(true);
    sitesTitledPane.setAnimated(false);
    sitesTitledPane.visibleProperty().bind(isShowSitesProperty);
    sitesTitledPane.managedProperty().bind(isShowSitesProperty);
    sitesTitledPane.setText("Sites");

    // sets up the facility browser and adds to the facilities titled pane
    facilitiesVBox.getChildren().addAll(facilityHeading, facilitiesFilterTextField, facilitiesListView);
    facilitiesTitledPane.setContent(facilitiesVBox);
    facilitiesTitledPane.getStyleClass().add("floor-filter-facilities");
    // set titled pane properties to manage behavior
    facilitiesTitledPane.setAnimated(false);
    facilitiesTitledPane.visibleProperty().bind(isShowFacilitiesProperty);
    facilitiesTitledPane.managedProperty().bind(isShowFacilitiesProperty);
    facilitiesTitledPane.setText("Facilities");
    facilitiesTitledPane.setExpanded(false);

    // sets the sites and facilities titled panes to a vbox wrapper
    sitesAndFacilitiesVBox.getChildren().addAll(sitesTitledPane, facilitiesTitledPane);
    sitesAndFacilitiesVBox.getStyleClass().add("floor-filter-sites-facilities");

    // sets up the levels browser
    levelsVBox.getChildren().addAll(levelsHeading, levelsListView, allLevelsCheckbox);
    levelsVBox.getStyleClass().add("floor-filter-levels");
    levelsVBox.prefWidthProperty().bind(contentPane.prefWidthProperty());
    levelsListView.prefWidthProperty().bind(levelsVBox.widthProperty());
    // only display the levels browser if a selected facility has levels
    levelsVBox.visibleProperty().bind(facilityHasLevelsProperty);
    levelsVBox.managedProperty().bind(levelsVBox.visibleProperty());

    // adds all sections and controls to the content pane vbox and sets it to a scroll pane to manage height
    contentPane.getChildren().addAll(sitesAndFacilitiesVBox, levelsVBox, zoomButton, collapseButton);
    scrollPane.setContent(contentPane);
    // prevent horizontal scrolling on the scroll pane
    scrollPane.setFitToWidth(true);
    // bind the maximum height of the scroll pane to the top of the attribution
    scrollPane.maxHeightProperty().bind(getSkinnable().getGeoView().attributionTopProperty());

    // adds the scroll pane to the main wrapper and sets that to the skin
    floorFilterWrapper.getChildren().add(scrollPane);
    floorFilterWrapper.getStyleClass().add("floor-filter-wrapper");
    getChildren().add(floorFilterWrapper);
  }

  /**
   * Configures the buttons and their actions.
   *
   * @since 100.14.0
   */
  private void setupButtons() {
    // add style classes
    zoomButton.getStyleClass().add("floor-filter-zoom-button");
    collapseButton.getStyleClass().add("floor-filter-collapse-button");

    // bind the width of the buttons to the pane
    zoomButton.prefWidthProperty().bind(contentPane.widthProperty());
    collapseButton.prefWidthProperty().bind(contentPane.widthProperty());
    zoomButton.disableProperty().bind(selectedSiteProperty.isNull());

    // configure actions based on what data is selected
    zoomButton.setOnAction(e -> {
      if (selectedSiteProperty.get() != null && selectedFacilityProperty.get() == null) {
        // if there is a selected site but no selected facility, zoom to the selected site
        getSkinnable().getGeoView().setViewpoint(new Viewpoint(selectedSiteProperty.get().getGeometry().getExtent()));
      } else if (selectedFacilityProperty.get() != null) {
        // if there is a selected facility, zoom to the selected facility
        getSkinnable().getGeoView().setViewpoint(new Viewpoint(selectedFacilityProperty.get().getGeometry().getExtent()));
      }
    });

    // configure the actions of the collapse button and update the call to action text
    collapseButton.setOnAction(e -> isCollapsedProperty.set(!isCollapsedProperty.get()));
    collapseButton.textProperty().bind(Bindings
      .when(isCollapsedProperty)
      .then("Expand")
      .otherwise("Collapse")
    );
  }

  /**
   * Sets up elements specific to the sites view and data, including the TextField to filter by name, ListView to
   * display the sites, and CheckBox to select all sites.
   *
   * @since 100.14.0
   */
  private void setupSites() {
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

    // when a site is selected on the list view, set it to the control
    sitesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      getSkinnable().setSelectedSite(newValue);
      // toggle to the facilities view
      sitesTitledPane.setExpanded(false);
      facilitiesTitledPane.setExpanded(true);
    });

    // bind the sites listview interactivity to whether the all sites checkbox is selected
    sitesListView.disableProperty().bind(allSitesCheckbox.selectedProperty());
    sitesFilterTextField.disableProperty().bind(allSitesCheckbox.selectedProperty());

    // set the filtered list on the list view and configure a cell factory to display the site names
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
      // if an already selected site is clicked again, still continue to navigate to the facility view and update the
      // viewpoint
      cell.setOnMouseClicked(e -> {
        sitesTitledPane.setExpanded(false);
        facilitiesTitledPane.setExpanded(true);
        if (selectedSiteProperty.get() != null) {
          getSkinnable().getGeoView().setViewpoint(new Viewpoint(selectedSiteProperty.get().getGeometry().getExtent()));
        }
      });
      return cell;
    });

    // when all sites checkbox is selected, update the isAllSitesProperty value
    allSitesCheckbox.selectedProperty().addListener((observable, oldValue, newValue) ->
      isAllSitesProperty.set(allSitesCheckbox.isSelected()));

    // configure listener that controls the height of the list view, or the point at which the list view will become
    // scrollable. This is calculated using the cell size and maximum number of desired rows before scroll. If the data
    // is smaller than the maximum number of rows, then the number of rows will be the size of the data.
    filteredSites.addListener((ListChangeListener<? super FloorSite>) observable ->
      sitesListView.setPrefHeight(filteredSites.size() < MAX_NO_OF_ROWS ? filteredSites.size() * CELL_SIZE : MAX_NO_OF_ROWS * CELL_SIZE));
  }

  /**
   * Sets up elements specific to the facilities view and data, including the TextField to filter by name and ListView
   * to display the facilities.
   *
   * @since 100.14.0
   */
  private void setupFacilities() {
    // configure the text field that filters facilities by name
    facilitiesFilterTextField.setPromptText("Filter facilities by name");
    facilitiesFilterTextField.textProperty().addListener(obs -> {
      String filter = facilitiesFilterTextField.getText();
      if (filter == null || filter.length() == 0) {
        filteredFacilities.setPredicate(facility -> true);
      } else {
        if (!isAllSitesProperty.get()) {
          filteredFacilities.setPredicate(facility -> facility.getName().toLowerCase().contains(filter.toLowerCase()) &&
            facility.getSite() == selectedSiteProperty.get());
        } else {
          filteredFacilities.setPredicate(facility -> facility.getName().toLowerCase().contains(filter.toLowerCase()));
        }
      }
    });

    // set the filtered list on the list view and configure a cell factory to display the facility names
    facilitiesListView.setItems(filteredFacilities);
    facilitiesListView.setCellFactory(v -> {
      ListCell<FloorFacility> cell = new ListCell<>() {
        @Override
        protected void updateItem(FloorFacility facility, boolean empty) {
          super.updateItem(facility, empty);
          if (empty || facility == null) {
            setText(null);
          } else {
            setText(facility.getName());
          }
        }
      };
      // if an already selected facility is clicked again, still update the viewpoint
      cell.setOnMouseClicked(e -> {
        if (selectedFacilityProperty.get() != null) {
          getSkinnable().getGeoView().setViewpoint(new Viewpoint(selectedFacilityProperty.get().getGeometry().getExtent()));
        }
      });
      return cell;
    });

    // when a facility is selected on the list view, set it to the control
    facilitiesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
      getSkinnable().setSelectedFacility(newValue));

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
    // only display the all levels checkbox if the view is a SceneView
    allLevelsCheckbox.visibleProperty().bind(isSceneViewProperty);
    allLevelsCheckbox.managedProperty().bind(allLevelsCheckbox.visibleProperty());

    // toggles the visibility of levels based on selection
    allLevelsCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
      if (allLevelsCheckbox.isSelected()) {
        getSkinnable().setAllLevelsVisible();
      } else if (!allLevelsCheckbox.isSelected()) {
        getSkinnable().filterLevelsVisibility();
      }
    });

    // set the filtered list on the list view and configure a cell factory to display the level long names by default
    levelsListView.setItems(filteredLevels);
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

    // when a level is selected on the list view, set it to the control
    levelsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
      getSkinnable().setSelectedLevel(newValue));

    // configure listener that controls the height of the list view, or the point at which the list view will become
    // scrollable. This is calculated using the cell size and maximum number of desired rows before scroll. If the data
    // is smaller than the maximum number of rows, then the number of rows will be the size of the data.
    filteredLevels.addListener((ListChangeListener<? super FloorLevel>) observable ->
      levelsListView.setPrefHeight(filteredLevels.size() < MAX_NO_OF_ROWS ?
        filteredLevels.size() * CELL_SIZE : MAX_NO_OF_ROWS * CELL_SIZE));
  }

  /**
   * A method to handle changes to the value of the isCollapsedProperty.
   * This toggles the UI to show the shortened version of the floor level name and reduces the width of the
   * FloorFilter.
   *
   * @since 100.14.0
   */
  private void handleIsCollapsedPropertyChanged() {
    if (isCollapsedProperty.get()) {
      // if collapsed is true, set the short name as the text on the levels list view
      levelsListView.setCellFactory(v -> new ListCell<>() {
        @Override
        protected void updateItem(FloorLevel level, boolean empty) {
          super.updateItem(level, empty);
          if (empty || level == null) {
            setText(null);
          } else {
            setText(level.getShortName());
          }
        }
      });
      // close the titled panes
      sitesTitledPane.setExpanded(false);
      facilitiesTitledPane.setExpanded(false);
    } else {
      // if collapsed is false, set the long name as the text on the levels list view
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
    }
  }

  /**
   * A method to handle changes to the value of the isAllSitesProperty.
   * This is toggled by the all sites checkbox option on the sites browser. It filters the list of facilities to show
   * all facilities attached to the floor manager, as opposed to facilities attached to a selected site.
   *
   * @since 100.14.0
   */
  private void handleIsAllSitesPropertyChanged() {
    if (isAllSitesProperty.get()) {
      // if all sites is true, filter the facilities list only by the text field input
      filteredFacilities.setPredicate(facility ->
        facility.getName().toLowerCase().contains(facilitiesFilterTextField.getText().toLowerCase()));
      // switch to displaying the facilities view for the user
      sitesTitledPane.setExpanded(false);
      facilitiesTitledPane.setExpanded(true);
    } else {
      // if all sites is false, filter the list by the text field input, and the selected site if there is one
      if (selectedSiteProperty.get() != null) {
        filteredFacilities.setPredicate(facility -> facility.getSite() == selectedSiteProperty.get() &&
          facility.getName().toLowerCase().contains(facilitiesFilterTextField.getText().toLowerCase()));
      } else {
        filteredFacilities.setPredicate(facility ->
          facility.getName().toLowerCase().contains(facilitiesFilterTextField.getText().toLowerCase()));
      }
    }
  }

  /**
   * Method to ensure the view is not collapsed when a titled pane is expanded. A full width UI provides a better
   * experience for browsing the titled panes.
   *
   * @since 100.14.0
   */
  private void handleTitledPaneExpanded(TitledPane titledPane) {
    if (titledPane.isExpanded() && isCollapsedProperty.get()) {
      isCollapsedProperty.set(false);
    }
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

  @Override
  protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double
    leftInset) {
    if (!isCollapsedProperty.get()) {
      return PREF_WIDTH;
    } else {
      return COLLAPSED_WIDTH;
    }
  }
}
