/*
 * Copyright 2022 Esri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.esri.arcgisruntime.toolkit.skins;

import java.util.logging.Logger;

import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.toolkit.UtilityNetworkTraceOperationResult;
import com.esri.arcgisruntime.toolkit.UtilityNetworkTraceStartingPoint;
import com.esri.arcgisruntime.toolkit.UtilityNetworkTraceTool;
import com.esri.arcgisruntime.utilitynetworks.UtilityNamedTraceConfiguration;
import com.esri.arcgisruntime.utilitynetworks.UtilityNetwork;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * A skin for displaying a {@link UtilityNetworkTraceTool}.
 *
 * <p>
 * The UI is only displayed if there is are Utility Networks loaded in the data model.
 *
 * <p>
 * The skin includes a TabPane with one tab for configuring a new trace and another Tab for displaying results.
 *
 * <p>
 * The new trace Tab includes a custom {@link UtilityNetworkTraceStartingPointView} for displaying the different
 * properties of starting points in the UI.
 *
 * <p>
 * The result Tab itself contains a TabPane of the results made up of custom Tabs defined in
 * {@link UtilityNetworkTraceOperationResultView}.
 *
 * <p>
 *  Generic styles can be overwritten using the default style class defined in the Control. In addition a number of
 *  custom styles are defined within utility-network-trace.css and can be overwritten.
 *  E.g. to change the icons used for the starting point and results zoom / delete buttons, assign a new SVG string to:
 *  -utility-network-view-zoom-icon-svg
 *  -utility-network-view-trash-icon-svg
 *
 *  Or, the color of the existing icon could be updated using:
 *  .utility-network-view .arcgis-toolkit-java-trash-icon {
 *      -fx-background-color: black;
 *  }
 *  .utility-network-view .arcgis-toolkit-java-zoom-icon {
 *      -fx-background-color: black;
 *  }
 *
 * @since 100.15.0
 */
public class UtilityNetworkTraceSkin extends SkinBase<UtilityNetworkTraceTool> {

  private static final double STARTING_POINT_LIST_CELL_HEIGHT = 95.0;
  // default width/height
  // overwrite using setPrefWidth or setPrefHeight
  private static final double PREF_WIDTH = 350.0;
  private static final double PREF_HEIGHT = 700.0;

  private final UtilityNetworkTraceTool skinnable = getSkinnable();
  public final MapView controlMapView;
  private Node root;

  public final SimpleBooleanProperty isMapAndUtilityNetworkLoadingInProgressProperty = new SimpleBooleanProperty();
  public final SimpleBooleanProperty insufficientStartingPointsProperty = new SimpleBooleanProperty(false);
  public final SimpleBooleanProperty aboveMinimumStartingPointsProperty = new SimpleBooleanProperty(false);
  public final SimpleBooleanProperty enableTraceProperty = new SimpleBooleanProperty();
  public final SimpleBooleanProperty isIdentifyInProgressProperty = new SimpleBooleanProperty(false);
  public final SimpleBooleanProperty isTraceInProgressProperty = new SimpleBooleanProperty(false);
  public final SimpleListProperty<UtilityNetwork> utilityNetworksProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
  public final SimpleListProperty<UtilityNamedTraceConfiguration> traceConfigurationsProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
  public final SimpleListProperty<UtilityNetworkTraceStartingPoint> startingPointsProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
  public final SimpleListProperty<UtilityNetworkTraceOperationResult> traceResultsProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
  public final SimpleObjectProperty<UtilityNetwork> selectedUtilityNetworkProperty = new SimpleObjectProperty<>();
  public final SimpleObjectProperty<UtilityNamedTraceConfiguration> selectedTraceConfigurationProperty = new SimpleObjectProperty<>();
  public final SimpleStringProperty traceNameProperty = new SimpleStringProperty();

  @FXML ProgressIndicator utilityNetworkLoadingProgressIndicator;
  // displays if no utility networks are found
  @FXML VBox utilityNetworksNotFoundVBox;

  @FXML TabPane tabPane;
  // tab containing configuration options for running a trace
  @FXML Tab newTraceTab;
  @FXML BorderPane newTraceBorderPane;
  // utility networks
  @FXML VBox utilityNetworkSelectionVBox;
  @FXML ComboBox<UtilityNetwork> utilityNetworkSelectionComboBox;
  // named trace configuration
  @FXML VBox traceConfigsNotFoundVBox;
  @FXML ComboBox<UtilityNamedTraceConfiguration> traceConfigComboBox;
  @FXML VBox traceConfigVBox;
  // starting points
  @FXML VBox startingPointsVBox;
  @FXML ListView<UtilityNetworkTraceStartingPoint> startingPointsListView;
  @FXML Label startingPointsPlaceholder;
  @FXML Button addStartingPointButton;
  @FXML Button cancelAddStartingPointsButton;
  @FXML Button clearStartingPointsButton;
  @FXML VBox addStartingPointProgressVBox;
  @FXML Button cancelIdentifyStartingPointsButton;
  @FXML TextField traceNameTextField;
  // warnings
  @FXML HBox aboveMinStartingPointsWarningHBox;
  @FXML HBox insufficientStartingPointsWarningHBox;
  // run trace
  @FXML Button runTraceButton;
  @FXML VBox traceInProgressVBox;
  @FXML ProgressBar traceInProgressBar;
  @FXML Label traceInProgressLabel;
  @FXML Button cancelTraceInProgressButton;
  // results
  @FXML VBox noResultsFoundVBox;
  @FXML Tab resultsTab;
  @FXML VBox resultsVBox;
  @FXML TabPane resultsTabPane;
  @FXML Button clearResultsButton;

  /**
   * Constructor for all SkinBase instances.
   *
   * @param control The control for which this Skin should attach to.
   * @since 100.15.0
   */
  public UtilityNetworkTraceSkin(UtilityNetworkTraceTool control) {
    super(control);
    // configure mapview related settings
    controlMapView = skinnable.getMapView();

    // load the FXML
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(this.getClass().getResource("utility_network_trace.fxml"));
    fxmlLoader.setControllerFactory(param -> this);
    try {
      root = fxmlLoader.load();
      // configure the UI if the FXML is loaded successfully
      configureUI();
    }  catch (Exception e) {
      // if the FXML file fails to load, log a warning
      Logger.getLogger(UtilityNetworkTraceSkin.class.getName()).warning(
        "Failed to load the FXML file. UtilityNetworkTraceSkin will not be displayed.\n" + e);
    }
  }

  /**
   * Takes an event handler for run trace events and sets to the runTraceButton.
   *
   * @param eventHandler the event handler for run trace events
   * @since 100.15.0
   */
  public void setRunTraceEventHandler(EventHandler<ActionEvent> eventHandler) {
    if (runTraceButton != null) {
      runTraceButton.setOnAction(eventHandler);
    }
  }

  /**
   * Takes an event handler for cancel trace events and sets to the cancelTraceInProgressButton.
   *
   * @param eventHandler the event handler for cancel trace events
   * @since 100.15.0
   */
  public void setCancelTraceEventHandler(EventHandler<ActionEvent> eventHandler) {
    if (cancelTraceInProgressButton != null) {
      cancelTraceInProgressButton.setOnAction(eventHandler);
    }
  }

  /**
   * Takes an event handler for cancel identify starting point events and sets to the
   * cancelIdentifyStartingPointsButton.
   *
   * @param eventHandler the event handler for cancel identify starting point events
   * @since 100.15.0
   */
  public void setCancelIdentifyStartingPointsEventHandler(EventHandler<ActionEvent> eventHandler) {
    if (cancelIdentifyStartingPointsButton != null) {
      cancelIdentifyStartingPointsButton.setOnAction(eventHandler);
    }
  }

  /**
   * Takes an event handler for clear results events and sets to the clearResultsButton.
   *
   * @param eventHandler the event handler for clear results events
   * @since 100.15.0
   */
  public void setClearResultsEventHandler(EventHandler<ActionEvent> eventHandler) {
    if (clearResultsButton != null) {
      clearResultsButton.setOnAction(eventHandler);
    }
  }
  /**
   * Returns the value of the variable used to define the height of the cells used in the starting points ListView.
   *
   * @return the height
   * @since 100.15.0
   */
  public double getStartingPointListCellHeight() {
    return STARTING_POINT_LIST_CELL_HEIGHT;
  }

  /**
   * Configures the UI by setting up relationships between UI controls and the properties and data that determine
   * what is displayed and what interactions occur.
   *
   * @since 100.15.0
   */
  private void configureUI() {
    // handle progress indicator for initial load
    utilityNetworkLoadingProgressIndicator.visibleProperty().bind(isMapAndUtilityNetworkLoadingInProgressProperty);
    utilityNetworkSelectionSetup();
    traceConfigurationSelectionSetup();
    startingPointSelectionSetup();
    warningsSetup();
    traceControlsSetup();
    resultsSetup();
    getChildren().add(root);
  }

  /**
   * Configures the UI, listeners and properties relating to the selection of a UtilityNetwork.
   *
   * @since 100.15.0
   */
  private void utilityNetworkSelectionSetup() {
    // if there are no utility networks, display an error message and don't display the tabpane
    utilityNetworksNotFoundVBox.visibleProperty().bind(Bindings.and(utilityNetworksProperty.emptyProperty(), isMapAndUtilityNetworkLoadingInProgressProperty.not()));
    // if there are utility networks, display the tabpane and don't display the error message
    tabPane.visibleProperty().bind(utilityNetworksProperty.emptyProperty().not());

    // utility network combobox displays the utility networks
    utilityNetworkSelectionComboBox.itemsProperty().bind(utilityNetworksProperty);
    utilityNetworkSelectionComboBox.setConverter(new UtilityNetworkStringConverter());

    // only display the utility network UI if there is more than 1 utility network to choose from,
    // or if one is not pre-selected
    utilityNetworkSelectionVBox.visibleProperty().bind(
      Bindings.or(utilityNetworksProperty.sizeProperty().greaterThan(1),
        Bindings.and(utilityNetworksProperty.sizeProperty().isEqualTo(1),
          selectedUtilityNetworkProperty.isNull())));

    // keep the selected utility network property up to date
    utilityNetworkSelectionComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != selectedUtilityNetworkProperty.get()) {
        selectedUtilityNetworkProperty.set(newValue);
      }
    });

    // keep the combobox in sync with the data
    selectedUtilityNetworkProperty.addListener(((observable, oldValue, newValue) -> {
      if (newValue != utilityNetworkSelectionComboBox.getSelectionModel().getSelectedItem()) {
        utilityNetworkSelectionComboBox.getSelectionModel().select(newValue);
      }
    }));
  }

  /**
   * Configures the UI, listeners and properties relating to the selection of a UtilityNamedTraceConfiguration.
   *
   * @since 100.15.0
   */
  private void traceConfigurationSelectionSetup() {
    // only display the trace configuration settings when there is a utility network selected
    traceConfigVBox.visibleProperty().bind(selectedUtilityNetworkProperty.isNotNull());

    // only display the combobox if there are available trace configurations, otherwise show warning
    traceConfigComboBox.visibleProperty().bind(traceConfigurationsProperty.emptyProperty().not());
    traceConfigsNotFoundVBox.visibleProperty().bind(traceConfigurationsProperty.emptyProperty());
    startingPointsVBox.visibleProperty().bind(traceConfigurationsProperty.emptyProperty().not());

    // trace config combobox displays the trace configs
    traceConfigComboBox.itemsProperty().bind(traceConfigurationsProperty);
    traceConfigComboBox.setConverter(new UtilityNamedTraceConfigurationStringConverter());

    // keep the selected trace configuration property up to date
    traceConfigComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != selectedTraceConfigurationProperty.get()) {
        selectedTraceConfigurationProperty.set(newValue);
      }
    });

    // keep the combobox in sync with the data
    selectedTraceConfigurationProperty.addListener(((observable, oldValue, newValue) -> {
      if (newValue != traceConfigComboBox.getSelectionModel().getSelectedItem()) {
        traceConfigComboBox.getSelectionModel().select(newValue);
      }
    }));
  }

  /**
   * Configures the UI, listeners and properties relating to the selection of starting points.
   *
   * @since 100.15.0
   */
  private void startingPointSelectionSetup() {
    // only display the starting points listview and remove button if there are any
    startingPointsListView.visibleProperty().bind(Bindings.isNotEmpty(startingPointsProperty));
    clearStartingPointsButton.visibleProperty().bind(Bindings.isNotEmpty(startingPointsProperty));
    // configure the list view
    startingPointsListView.setCellFactory(listView -> new StartingPointListCell(this));
    startingPointsListView.setItems(startingPointsProperty);
    // set the pref height to the number of cells multiplied by the defined cell height
    startingPointsListView.prefHeightProperty().bind(Bindings.size(startingPointsProperty).multiply(STARTING_POINT_LIST_CELL_HEIGHT));

    // hide add starting points button if selection is in progress
    addStartingPointButton.visibleProperty().bind(skinnable.isAddingStartingPointsProperty().not());
    // only show the cancel button if selection is in process
    cancelAddStartingPointsButton.visibleProperty().bind(skinnable.isAddingStartingPointsProperty());
    // display the starting points placeholder label if starting points are being added
    startingPointsPlaceholder.visibleProperty().bind(skinnable.isAddingStartingPointsProperty());

    // configure the UI when an identify is in progress
    isIdentifyInProgressProperty.addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        addStartingPointProgressVBox.setVisible(true);
        newTraceBorderPane.setVisible(false);
      } else {
        addStartingPointProgressVBox.setVisible(false);
        newTraceBorderPane.setVisible(true);
      }
    });
  }

  /**
   * Configures the UI relating to the display of warnings.
   *
   * @since 100.15.0
   */
  private void warningsSetup() {
    // only show warning if there are insufficient starting points
    insufficientStartingPointsWarningHBox.visibleProperty().bind(insufficientStartingPointsProperty);
    // only show warning if there are above the minimum starting points
    aboveMinStartingPointsWarningHBox.visibleProperty().bind(aboveMinimumStartingPointsProperty);
  }

  /**
   * Configures the UI, listeners and properties relating to the running of a trace.
   *
   * @since 100.15.0
   */
  private void traceControlsSetup() {
    // bind the trace name to the value in the text field
    traceNameProperty.bind(traceNameTextField.textProperty());

    // only enable the trace button if trace is enabled
    runTraceButton.disableProperty().bind(enableTraceProperty.not());

    // configure the UI for when a trace is running
    isTraceInProgressProperty.addListener(((observable, oldValue, newValue) -> {
      if (newValue) {
        tabPane.getSelectionModel().select(resultsTab);
        newTraceTab.setDisable(true);
        resultsVBox.setVisible(false);
        traceInProgressVBox.setVisible(true);
      } else {
        resultsVBox.setVisible(true);
        traceInProgressVBox.setVisible(false);
        newTraceTab.setDisable(false);
      }
    }));
  }

  /**
   * Configures the UI, listeners and properties relating to the display of results.
   *
   * @since 100.15.0
   */
  private void resultsSetup() {
    // only show the results tab when there are results and/or a result is in progress
    noResultsFoundVBox.visibleProperty().bind(Bindings.and(isTraceInProgressProperty.not(), traceResultsProperty.emptyProperty()));

    // keep trace results in sync with data
    traceResultsProperty.addListener((ListChangeListener<UtilityNetworkTraceOperationResult>) c -> {
      while (c.next()) {
        if (traceResultsProperty.isEmpty()) {
          tabPane.getSelectionModel().select(newTraceTab);
        }
        for (UtilityNetworkTraceOperationResult removedResult : c.getRemoved()) {
          resultsTabPane.getTabs().remove(findTabForResult(removedResult));
        }
        for (UtilityNetworkTraceOperationResult addedResult : c.getAddedSubList()) {
          Platform.runLater(() -> {
            // waits for result to finish process via async operation
            var tab = new UtilityNetworkTraceOperationResultView(this, addedResult);
            resultsTabPane.getTabs().add(tab);
            resultsTabPane.getSelectionModel().select(tab);
          });
        }
      }
    });

    // configure the clear results button and only display when there are trace results
    clearResultsButton.setOnAction(e -> traceResultsProperty.clear());
    clearResultsButton.visibleProperty().bind(Bindings.isNotEmpty(traceResultsProperty));
  }

  /**
   * Returns the Tab that is displaying the data for the provided result.
   *
   * @return the Tab. Null if no Tab is found.
   * @since 100.15.0
   */
  private Tab findTabForResult(UtilityNetworkTraceOperationResult result) {
    return resultsTabPane.getTabs()
      .stream()
      .map(UtilityNetworkTraceOperationResultView.class::cast)
      .filter(t -> t.getResult() == result).findFirst().orElse(null);
  }

  /**
   * Creates and returns the default trace name used when a name is not defined in the text field.
   *
   * @return the default trace name
   * @since 100.15.0
   */
  public String getDefaultTraceName() {
    return selectedTraceConfigurationProperty.get().getName() + " " + (traceResultsProperty.size() + 1);
  }

  /**
   * Handles actions on the addStartingPointsButton.
   *
   * @since 100.15.0
   */
  @FXML
  private void handleAddStartingPointButton() {
    skinnable.isAddingStartingPointsProperty().set(true);
  }

  /**
   * Handles actions on the clearStartingPointsButton.
   *
   * @since 100.15.0
   */
  @FXML
  private void handleClearStartingPointsButton() {
    startingPointsProperty.clear();
  }

  /**
   * Handles actions on the cancelAddStartingPointsButton.
   *
   * @since 100.15.0
   */
  @FXML
  private void handleCancelAddStartingPointsButton() {
    skinnable.isAddingStartingPointsProperty().set(false);
    if (traceResultsProperty.isEmpty()) {
      tabPane.getSelectionModel().select(newTraceTab);
    }
  }

  @Override
  protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double
    leftInset) {
    return PREF_WIDTH;
  }

  @Override
  protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double
    leftInset) {
    return PREF_HEIGHT;
  }

  @Override
  public void dispose() {
    // remove event handler from MapView when the skin is disposed
    getSkinnable().removeDefaultMapViewEventHandler();
  }

  /**
   * Defines a custom String Converter for the UtilityNetwork selection combobox.
   *
   * @since 100.15.0
   */
  private static class UtilityNetworkStringConverter extends StringConverter<UtilityNetwork> {
    @Override
    public String toString(UtilityNetwork utilityNetwork) {
      return utilityNetwork != null ? utilityNetwork.getName() : "";
    }

    @Override
    public UtilityNetwork fromString(String string) {
      return null;
    }
  }

  /**
   * Defines a custom String Converter for the UtilityNamedTraceConfiguration selection combobox.
   *
   * @since 100.15.0
   */
  private static class UtilityNamedTraceConfigurationStringConverter extends StringConverter<UtilityNamedTraceConfiguration> {
    @Override
    public String toString(UtilityNamedTraceConfiguration traceConfig) {
      return traceConfig != null ? traceConfig.getName() : "";
    }

    @Override
    public UtilityNamedTraceConfiguration fromString(String string) {
      return null;
    }
  }

  /**
   * Defines a custom ListCell for the starting points ListView, which contains a custom BorderPane defined in
   * {@link UtilityNetworkTraceStartingPointView}.
   *
   * @since 100.15.0
   */
  private static class StartingPointListCell extends ListCell<UtilityNetworkTraceStartingPoint> {

    private final UtilityNetworkTraceSkin skin;

    public StartingPointListCell(UtilityNetworkTraceSkin skin) {
      this.skin = skin;
      this.getStyleClass().add("utility-network-starting-point-list-cell");
    }
    @Override
    public void updateItem(UtilityNetworkTraceStartingPoint startingPoint, boolean empty) {
      super.updateItem(startingPoint, empty);
      if (startingPoint != null && !empty) {
        setText(null);
        setGraphic(new UtilityNetworkTraceStartingPointView(skin, startingPoint));
      } else {
        setText(null);
        setGraphic(null);
      }
    }
  }
}
