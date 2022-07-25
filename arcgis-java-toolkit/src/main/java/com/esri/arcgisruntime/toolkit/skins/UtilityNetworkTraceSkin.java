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

import com.esri.arcgisruntime.mapping.view.Callout;
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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.logging.Logger;

public class UtilityNetworkTraceSkin extends SkinBase<UtilityNetworkTraceTool> {

    private static final double STARTING_POINT_LIST_CELL_HEIGHT = 95.0;
    private static final double PREF_WIDTH = 350.0;

    private final UtilityNetworkTraceTool skinnable = getSkinnable();
    public final MapView controlMapView;
    private final Callout callout;
    private final Label calloutTitle = new Label();
    private final Label calloutDetail = new Label();
    private Node root;

    public SimpleBooleanProperty isMapAndUtilityNetworkLoadingInProgressProperty = new SimpleBooleanProperty();
    public SimpleBooleanProperty insufficientStartingPointsProperty = new SimpleBooleanProperty(false);
    public SimpleBooleanProperty aboveMinimumStartingPointsProperty = new SimpleBooleanProperty(false);
    public SimpleBooleanProperty enableTraceProperty = new SimpleBooleanProperty();
    public SimpleBooleanProperty isIdentifyInProgressProperty = new SimpleBooleanProperty(false);
    public SimpleBooleanProperty isTraceInProgressProperty = new SimpleBooleanProperty(false);
    public SimpleListProperty<UtilityNetwork> utilityNetworksProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    public SimpleListProperty<UtilityNamedTraceConfiguration> traceConfigurationsProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    public SimpleListProperty<UtilityNetworkTraceStartingPoint> startingPointsProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    public SimpleListProperty<UtilityNetworkTraceOperationResult> traceResultsProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    public SimpleObjectProperty<UtilityNetwork> selectedUtilityNetworkProperty = new SimpleObjectProperty<>();
    public SimpleObjectProperty<UtilityNamedTraceConfiguration> selectedTraceConfigurationProperty = new SimpleObjectProperty<>();
    public SimpleStringProperty traceNameProperty = new SimpleStringProperty();

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

    public UtilityNetworkTraceSkin(UtilityNetworkTraceTool control) {
        super(control);
        // configure mapview related settings
        controlMapView = skinnable.getMapView();
        callout = controlMapView.getCallout();
        callout.setStyle("leader-position: bottom; corner-radius:0; margin:10;");
        var calloutCustomView = new VBox(5);
        calloutTitle.getStyleClass().add("arcgis-toolkit-java-h2");
        calloutDetail.getStyleClass().add("arcgis-toolkit-java-h3");
        calloutCustomView.getChildren().addAll(calloutTitle, calloutDetail);
        callout.setCustomView(calloutCustomView);
        callout.setOnMouseClicked(e -> callout.dismiss());

        // load the FXML
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("utility_network_trace.fxml"));
        fxmlLoader.setControllerFactory(param -> this);
        try {
            root = fxmlLoader.load();
            // configure the UI if the FXML is loaded successfully
            configureUI();
        }  catch (Exception e) {
            // if the FXML file fails to load, log a warning
            Logger.getLogger(UtilityNetworkTraceSkin.class.getName()).warning(
                    "Failed to load the FXML file. UtilityNetworkTraceSkin will not be displayed.");
        }
    }

    public Button getRunTraceButton() { return runTraceButton; }

    public Button getCancelTraceInProgressButton() { return cancelTraceInProgressButton; }

    public Button getCancelIdentifyStartingPointsButton() { return cancelIdentifyStartingPointsButton; }

    public Button getClearResultsButton() { return clearResultsButton; }

    public double getStartingPointListCellHeight() { return STARTING_POINT_LIST_CELL_HEIGHT; }

    private void configureUI() {
        // handle progress indicator for initial load
        utilityNetworkLoadingProgressIndicator.visibleProperty().bind(isMapAndUtilityNetworkLoadingInProgressProperty);
        // Utility Network selection setup
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

        // Trace Configuration selection setup
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

        // Starting Point selection setup

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

        // link the callout content to the starting point currently selected in the list view
        startingPointsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                calloutTitle.setText(newValue.getUtilityElement().getNetworkSource().getName());
                calloutDetail.setText(newValue.getUtilityElement().getAssetGroup().getName());
                callout.showCalloutAt(newValue.getGraphic().getGeometry().getExtent().getCenter());
            } else {
                calloutTitle.setText("");
                calloutDetail.setText("");
                callout.dismiss();
            }
        });

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

        // only show warning if there are insufficient starting points
        insufficientStartingPointsWarningHBox.visibleProperty().bind(insufficientStartingPointsProperty);

        // only show warning if there are above the minimum starting points
        aboveMinStartingPointsWarningHBox.visibleProperty().bind(aboveMinimumStartingPointsProperty);

        // bind the trace name to the value in the text field
        traceNameProperty.bind(traceNameTextField.textProperty());

        // only enable the trace button if trace is enabled
        runTraceButton.disableProperty().bind(enableTraceProperty.not());

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

        // configure the clear results button and only display when there are trace results
        clearResultsButton.setOnAction(e -> traceResultsProperty.clear());
        clearResultsButton.visibleProperty().bind(Bindings.isNotEmpty(traceResultsProperty));

        getChildren().add(root);
    }

    private Tab findTabForResult(UtilityNetworkTraceOperationResult result) {
        return resultsTabPane.getTabs().stream()
                .filter(c -> ((UtilityNetworkTraceOperationResultView) c).getResult() == result).findFirst().orElse(null);
    }

    public String getDefaultTraceName() {
        return selectedTraceConfigurationProperty.get().getName() + " " + (traceResultsProperty.size() + 1);
    }

    @FXML
    private void handleAddStartingPointButtonClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.isStillSincePress()) {
            skinnable.isAddingStartingPointsProperty().set(true);
        }
    }

    @FXML
    private void handleClearStartingPointsButtonClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.isStillSincePress()) {
            startingPointsProperty.clear();
        }
    }

    @FXML
    private void handleCancelAddStartingPointsButtonClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.isStillSincePress()) {
            skinnable.isAddingStartingPointsProperty().set(false);
            if (traceResultsProperty.isEmpty()) {
                tabPane.getSelectionModel().select(newTraceTab);
            }
        }
    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double
            leftInset) {
        return PREF_WIDTH;
    }

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
