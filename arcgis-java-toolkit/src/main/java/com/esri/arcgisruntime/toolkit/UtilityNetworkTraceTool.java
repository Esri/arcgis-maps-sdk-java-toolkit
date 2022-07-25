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

package com.esri.arcgisruntime.toolkit;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.LayerContent;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.*;
import com.esri.arcgisruntime.utilitynetworks.*;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.control.Control;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static java.util.stream.Collectors.groupingBy;

public class UtilityNetworkTraceTool extends Control {

    // properties with public accessors
    private final ReadOnlyObjectWrapper<MapView> mapViewProperty = new ReadOnlyObjectWrapper<>() {
        @Override
        public void set(MapView newValue) {
            super.set(Objects.requireNonNull(newValue, "MapView cannot be null"));
        }
    };
    private final ReadOnlyObjectWrapper<UtilityNetwork> selectedUtilityNetworkProperty = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<UtilityNetworkTraceToolCompletedTrace> completedTraceProperty = new ReadOnlyObjectWrapper<>();
    private final SimpleBooleanProperty isAddingStartingPointsProperty = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty autoZoomToResultsProperty = new SimpleBooleanProperty(true);
    private final SimpleObjectProperty<Symbol> startingPointSymbolProperty = new SimpleObjectProperty<>(
            new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CROSS, ColorUtil.colorToArgb(Color.LIMEGREEN), 20));
    private final SimpleObjectProperty<SimpleMarkerSymbol> resultPointSymbolProperty = new SimpleObjectProperty<>(
            new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, ColorUtil.colorToArgb(Color.rgb(0, 0, 255, 0.5)), 20));
    private final SimpleObjectProperty<SimpleLineSymbol> resultLineSymbolProperty = new SimpleObjectProperty<>(
            new SimpleLineSymbol(SimpleLineSymbol.Style.DOT, ColorUtil.colorToArgb(Color.rgb(0, 0, 255, 0.5)), 5));
    private final SimpleObjectProperty<SimpleFillSymbol> resultFillSymbolProperty = new SimpleObjectProperty<>(
            new SimpleFillSymbol(SimpleFillSymbol.Style.FORWARD_DIAGONAL, ColorUtil.colorToArgb(Color.rgb(0, 0, 255, 0.5)),
                    new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, ColorUtil.colorToArgb(Color.rgb(0, 0, 255, 0.5)), 2)));

    // internal properties
    private final SimpleListProperty<UtilityNetwork> utilityNetworksProperty =
            new SimpleListProperty<>(FXCollections.observableArrayList());
    private final SimpleListProperty<UtilityNamedTraceConfiguration> traceConfigurationsProperty =
            new SimpleListProperty<>(FXCollections.observableArrayList());
    private final SimpleObjectProperty<UtilityNamedTraceConfiguration> selectedTraceConfigurationProperty = new SimpleObjectProperty<>();
    private final SimpleListProperty<UtilityNetworkTraceStartingPoint> startingPointsProperty =
            new SimpleListProperty<>(FXCollections.observableArrayList());
    private final SimpleListProperty<UtilityNetworkTraceOperationResult> traceResultsProperty =
            new SimpleListProperty<>(FXCollections.observableArrayList());
    private final SimpleBooleanProperty isMapAndUtilityNetworkLoadingInProgressProperty = new SimpleBooleanProperty(true);
    private final SimpleBooleanProperty insufficientStartingPointsProperty = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty aboveMinimumStartingPointsProperty = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty enableTraceProperty = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty isTraceInProgressProperty = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty isIdentifyInProgressProperty = new SimpleBooleanProperty(false);

    private final GraphicsOverlay startingPointsGraphicsOverlay = new GraphicsOverlay();
    private UtilityNetworkTraceOperationResult traceResultInProgress;

    // listenable futures for asynchronous methods so that they can be cancelled
    private ListenableFuture<List<UtilityNamedTraceConfiguration>> queryNamedTraceConfigurationsFuture;
    private ListenableFuture<List<IdentifyLayerResult>> identifyLayersFuture;
    private ListenableFuture<List<UtilityTraceResult>> traceInProgressFuture;
    private ListenableFuture<List<ArcGISFeature>> fetchFeaturesForElementsFuture;

    private static final String DEFAULT_STYLE_CLASS = "utility-network-view";
    public UtilityNetworkTraceTool(@NamedArg("mapView") MapView mapView) {
        mapViewProperty.set(mapView);
        // add the starting points graphics overlay to the MapView ready to display starting points
        mapView.getGraphicsOverlays().add(startingPointsGraphicsOverlay);
        // configure action for clicks on the MapView
        mapView.setOnMouseClicked(this::onMapViewClicked);

        // listener for changes to the loading state property
        // once loading is complete, selects the first utility network in the list by default
        isMapAndUtilityNetworkLoadingInProgressProperty.addListener(((observable, oldValue, newValue) -> {
            if (!newValue && !utilityNetworksProperty.isEmpty()) {
                // waits for async loading of utility networks before setting
                Platform.runLater(() -> selectedUtilityNetworkProperty.set(utilityNetworksProperty.get(0)));
            }
        }));

        // listener for changes to the selected utility network property
        // resets trace configurations and starting points which are dependent on the selected utility network
        selectedUtilityNetworkProperty.addListener(((observable, oldValue, newValue) -> {
            resetNewTraceConfigurationProperties();
            if (newValue != null) {
                // async operation to query named trace configurations for the newly selected utility network
                queryNamedTraceConfigurationsFuture = selectedUtilityNetworkProperty.get().queryNamedTraceConfigurationsAsync(null);
                try {
                    // added a timeout as there is no UI configured to directly cancel this async operation
                    ObservableList<UtilityNamedTraceConfiguration> traceConfigs =
                            FXCollections.observableArrayList(queryNamedTraceConfigurationsFuture.get(30, TimeUnit.SECONDS));
                    traceConfigurationsProperty.set(traceConfigs);
                    traceConfigurationsProperty.sort(Comparator.comparing(UtilityNamedTraceConfiguration::getName));
                    if (!traceConfigurationsProperty.isEmpty()) {
                        // select the first trace configuration by default
                        selectedTraceConfigurationProperty.set(traceConfigurationsProperty.get(0));
                    }
                } catch (CancellationException cancellationException) {
                    // ignore cancellations of the async query operation
                } catch (Exception e) {
                    // if there is any other Exception while setting up the named trace configurations, display a warning
                    displayLoggerWarning("Could not load Utility Named Trace Configurations.");
                    // ensure data is reset
                    traceConfigurationsProperty.clear();
                    selectedTraceConfigurationProperty.set(null);
                }
            }
        }));

        // listener for changes to the selected trace configuration property
        selectedTraceConfigurationProperty.addListener(((observable, oldValue, newValue) -> applyStartingPointWarnings()));

        // listener for changes to the list of starting points
        startingPointsProperty.addListener((ListChangeListener<UtilityNetworkTraceStartingPoint>) c -> {
            while (c.next()) {
                for (UtilityNetworkTraceStartingPoint removedStartingPoint : c.getRemoved()) {
                    // when a starting point is removed, remove from the graphics overlay
                    startingPointsGraphicsOverlay.getGraphics().remove(removedStartingPoint.getGraphic());
                    applyStartingPointWarnings();
                }
                for (UtilityNetworkTraceStartingPoint addedStartingPoint : c.getAddedSubList()) {
                    // when a starting point is added, add to the graphics overlay
                    startingPointsGraphicsOverlay.getGraphics().add(addedStartingPoint.getGraphic());
                    applyStartingPointWarnings();
                }
            }
        });

        // listener for changes to the list of trace results
        traceResultsProperty.addListener((ListChangeListener<UtilityNetworkTraceOperationResult>) c -> {
            while (c.next()) {
                for (UtilityNetworkTraceOperationResult addedResult : c.getAddedSubList()) {
                    // when a result is added, set it to the completed trace property
                    completedTraceProperty.set(
                            new UtilityNetworkTraceToolCompletedTrace(
                                    addedResult.getRawResults(),
                                    addedResult.getException(),
                                    addedResult.getTraceParameters()));
                }
                for (UtilityNetworkTraceOperationResult removedResult : c.getRemoved()) {
                    // when a result is removed, unselect the features and remove graphics from the MapView's graphics overlay
                    removedResult.selectResultFeatures(false);
                    mapView.getGraphicsOverlays().remove(removedResult.getResultsGraphicsOverlay());
                }
            }
        });

        // listener for the starting points symbol property
        startingPointSymbolProperty.addListener((observable, oldValue, newValue) -> {
            // when the property changes, update the existing starting points' symbology
            startingPointsProperty.get().forEach(startingPoint -> startingPoint.updateSelectionGraphicSymbol(newValue));
        });

        // enable trace is true if there is a trace configuration selected and sufficient starting points
        enableTraceProperty.bind(
                Bindings.and(selectedTraceConfigurationProperty.isNotNull(), insufficientStartingPointsProperty.not()));

        // load and configure the data
        setupUtilityNetworks();

        getStyleClass().add(DEFAULT_STYLE_CLASS);

        setMinHeight(USE_PREF_SIZE);
        setMinWidth(USE_PREF_SIZE);
        setMaxHeight(USE_COMPUTED_SIZE);
        setMaxWidth(USE_PREF_SIZE);
    }

    @Override
    public String getUserAgentStylesheet() {
        return Objects.requireNonNull(this.getClass().getResource("skins/utility-network-trace.css")).toExternalForm();
    }

    public ReadOnlyObjectProperty<MapView> mapViewReadOnlyProperty() { return mapViewProperty.getReadOnlyProperty(); }

    public MapView getMapView() {
        return mapViewProperty.get();
    }

    public ReadOnlyObjectProperty<UtilityNetwork> selectedUtilityNetworkReadOnlyProperty() {
        return selectedUtilityNetworkProperty.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<UtilityNetworkTraceToolCompletedTrace> completedTraceReadOnlyProperty() {
        return completedTraceProperty.getReadOnlyProperty();
    }

    public SimpleBooleanProperty isAddingStartingPointsProperty() { return isAddingStartingPointsProperty; }

    public Boolean getIsAddingStartingPoints() { return isAddingStartingPointsProperty.get(); }

    public void setIsAddingStartingPoints(Boolean isAddingStartingPoints) { isAddingStartingPointsProperty.set(isAddingStartingPoints); }

    public SimpleBooleanProperty autoZoomToResultsProperty() { return autoZoomToResultsProperty; }

    public Boolean getAutoZoomToResults() { return autoZoomToResultsProperty.get(); }

    public void setAutoZoomToResults(Boolean autoZoomToResults) { autoZoomToResultsProperty.set(autoZoomToResults); }

    public SimpleObjectProperty<Symbol> startingPointSymbolProperty() { return startingPointSymbolProperty; }

    public Symbol getStartingPointSymbol() { return startingPointSymbolProperty.get(); }

    public void setStartingPointSymbol(Symbol symbol) { startingPointSymbolProperty.set(symbol); }

    public SimpleObjectProperty<SimpleMarkerSymbol> resultPointSymbolProperty() { return resultPointSymbolProperty; }

    public SimpleMarkerSymbol getResultPointSymbol() { return resultPointSymbolProperty.get(); }

    public void setResultPointSymbol(SimpleMarkerSymbol simpleMarkerSymbol) {
        resultPointSymbolProperty.set(simpleMarkerSymbol); }

    public SimpleObjectProperty<SimpleLineSymbol> resultLineSymbolProperty() { return resultLineSymbolProperty; }

    public SimpleLineSymbol getResultLineSymbol() { return resultLineSymbolProperty.get(); }

    public void setResultLineSymbol(SimpleLineSymbol simpleLineSymbol) { resultLineSymbolProperty.set(simpleLineSymbol); }

    public SimpleObjectProperty<SimpleFillSymbol> resultFillSymbolProperty() { return resultFillSymbolProperty; }

    public SimpleFillSymbol getResultFillSymbol() {return resultFillSymbolProperty.get(); }

    public void setResultFillSymbol(SimpleFillSymbol simpleFillSymbol) { resultFillSymbolProperty.set(simpleFillSymbol); }

    private void setupUtilityNetworks() {
        isMapAndUtilityNetworkLoadingInProgressProperty.set(true);
        var mapView = mapViewProperty.get();
        // check if there is map
        if (mapView.getMap() != null) {
            ArcGISMap map = mapViewProperty.get().getMap();
            map.addDoneLoadingListener(() -> {
                if (map.getLoadStatus() == LoadStatus.LOADED) {
                    // check if the map has utility networks
                    if (!map.getUtilityNetworks().isEmpty()) {
                        ObservableList<UtilityNetwork> utilityNetworksFromMap = FXCollections.observableArrayList();
                        // create completable futures to wait for all utility networks to finish loading before
                        // setting the data
                        CompletableFuture<?>[] futures = new CompletableFuture<?>[map.getUtilityNetworks().size()];
                        for (var utilityNetwork : map.getUtilityNetworks()) {
                            var index = map.getUtilityNetworks().indexOf(utilityNetwork);
                            var completableFuture = new CompletableFuture<>();
                            futures[index] = completableFuture;
                            utilityNetwork.addDoneLoadingListener(() -> {
                                if (utilityNetwork.getLoadStatus() == LoadStatus.LOADED) {
                                    utilityNetworksFromMap.add(utilityNetwork);
                                    // complete the future if the utility network loads successfully
                                    futures[index].complete(null);
                                } else if (utilityNetwork.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
                                    // complete the future with an exception if the utility network fails to load
                                    futures[index].completeExceptionally(utilityNetwork.getLoadError());
                                }
                            });
                            if (utilityNetwork.getLoadStatus() == LoadStatus.NOT_LOADED) {
                                // load the utility network if it is not already loaded
                                utilityNetwork.loadAsync();
                            }
                        }

                        CompletableFuture.allOf(futures).whenCompleteAsync((future, exception) -> {
                            // when all futures complete, set the data to the utility networks property
                            utilityNetworksProperty.set(utilityNetworksFromMap);
                            isMapAndUtilityNetworkLoadingInProgressProperty.set(false);
                        });
                    } else {
                        displayLoggerWarning("There are no Utility Networks associated with the ArcGIS Map attached " +
                                "to the MapView. UtilityNetworkTrace.refresh() can be used to reload.");
                        isMapAndUtilityNetworkLoadingInProgressProperty.set(false);
                    }
                } else if (map.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
                    displayLoggerWarning("The ArcGISMap failed to load.");
                    isMapAndUtilityNetworkLoadingInProgressProperty.set(false);
                }
            });
            if (map.getLoadStatus() != LoadStatus.LOADED) {
                // load the map if it is not already loaded
                map.loadAsync();
            }
        } else {
            displayLoggerWarning("There is no ArcGIS Map attached to the MapView. UtilityNetworkTrace.refresh() can be " +
                    "used to reload.");
            isMapAndUtilityNetworkLoadingInProgressProperty.set(false);
        }
    }

    public void refresh() {
        selectedUtilityNetworkProperty.set(null);
        utilityNetworksProperty.clear();
        resetNewTraceConfigurationProperties();
        resetTraceResults();
        setupUtilityNetworks();
        applyStartingPointWarnings();
    }

    private void resetTraceResults() {
        var mapViewGraphicsOverlay = getMapView().getGraphicsOverlays();
        traceResultsProperty.forEach(result -> {
            result.selectResultFeatures(false);
            mapViewGraphicsOverlay.remove(result.getResultsGraphicsOverlay());
        });
        completedTraceProperty.set(null);
        traceResultsProperty.clear();
    }

    private void resetNewTraceConfigurationProperties() {
        if (traceInProgressFuture != null) {
            traceInProgressFuture.cancel(true);
        }
        traceInProgressFuture = null;
        if (fetchFeaturesForElementsFuture != null) {
            fetchFeaturesForElementsFuture.cancel(true);
        }
        fetchFeaturesForElementsFuture = null;

        isTraceInProgressProperty.set(false);
        isIdentifyInProgressProperty.set(false);

        if (identifyLayersFuture != null) {
            identifyLayersFuture.cancel(true);
        }
        identifyLayersFuture = null;
        if (queryNamedTraceConfigurationsFuture != null) {
            queryNamedTraceConfigurationsFuture.cancel(true);
        }
        queryNamedTraceConfigurationsFuture = null;

        selectedTraceConfigurationProperty.set(null);
        traceConfigurationsProperty.set(FXCollections.observableArrayList());

        startingPointsProperty.clear();
        startingPointsGraphicsOverlay.getGraphics().clear();
        traceResultInProgress = null;
        applyStartingPointWarnings();
    }

    public void addStartingPoint(ArcGISFeature feature) {
        addStartingPoint(feature, null);
    }

    public void addStartingPoint(ArcGISFeature feature, Point mapPoint) {
        if (selectedUtilityNetworkProperty != null) {
            var geometry = feature.getGeometry();
            UtilityElement utilityElement = null;

            try {
                utilityElement = selectedUtilityNetworkProperty.get().createElement(feature);
            } catch (Exception e) {
                // if a feature does not belong to the selected utility network it is ignored
            }

            if (utilityElement != null) {
                // check if the starting point already exists
                var startingPointAlreadyExists = false;
                var utilityElementId = utilityElement.getObjectId();
                for (UtilityNetworkTraceStartingPoint startingPoint : startingPointsProperty) {
                    if (startingPoint.getUtilityElement().getObjectId() == utilityElementId) {
                        startingPointAlreadyExists = true;
                    }
                }

                // only continue if the starting point does not already exist
                if (!startingPointAlreadyExists) {
                    if (utilityElement.getNetworkSource().getSourceType() ==
                            UtilityNetworkSource.Type.EDGE && geometry instanceof Polyline) {
                        // configure edge utility elements
                        Polyline polyline = (Polyline) geometry;
                        if (polyline.hasZ()) {
                            // get the geometry of the identified feature as a polyline, and remove the z component
                            polyline = (Polyline) GeometryEngine.removeZ(polyline);
                        }
                        if (mapPoint != null && mapPoint.getSpatialReference() != polyline.getSpatialReference()) {
                            polyline = (Polyline) GeometryEngine.project(polyline, mapPoint.getSpatialReference());
                        }
                        geometry = polyline;

                        // compute how far the clicked location is along the edge feature
                        if (mapPoint != null) {
                            double fractionAlongEdge = GeometryEngine.fractionAlong(polyline, mapPoint, -1);
                            if (!Double.isNaN(fractionAlongEdge)) {
                                // set the fraction along edge
                                utilityElement.setFractionAlongEdge(fractionAlongEdge);
                            }
                        }
                    } else if (utilityElement.getNetworkSource().getSourceType() ==
                            UtilityNetworkSource.Type.JUNCTION &&
                            utilityElement.getAssetType().getTerminalConfiguration() != null) {
                        // configure junction utility elements
                        var utilityTerminalConfiguration = utilityElement.getAssetType().getTerminalConfiguration();
                        List<UtilityTerminal> terminals = utilityTerminalConfiguration.getTerminals();
                        if (terminals.size() > 1) {
                            utilityElement.setTerminal(utilityElement.getAssetType().getTerminalConfiguration().getTerminals().get(0));
                        }
                    }
                    // create a graphic based on the geometry of the provided feature and set the starting point symbol
                    // this is used to display the starting point on the map
                    var graphic = new Graphic();
                    graphic.setGeometry(geometry);
                    graphic.setSymbol(startingPointSymbolProperty.get());
                    // get the symbol used for the feature on the feature layer
                    // this is used as an indicator in the UI
                    Symbol symbol = null;
                    if (feature.getFeatureTable().getLayer() instanceof FeatureLayer) {
                        var featureLayer = (FeatureLayer) feature.getFeatureTable().getLayer();
                        symbol = featureLayer.getRenderer().getSymbol(feature);
                    }
                    // add the starting point using the configured properties
                    startingPointsProperty.add(
                            new UtilityNetworkTraceStartingPoint(utilityElement, graphic, symbol, geometry.getExtent()));
                }
            }
        }
    }

    private void runTraceAsync(String name) {
        // cancel any previous traces
        cancelTrace();
        // configure completable futures due to requirement of multiple async methods
        CompletableFuture<Void> traceCompletableFuture = new CompletableFuture<>();
        CompletableFuture<Void> fetchFeaturesForElementsCompletableFuture = new CompletableFuture<>();
        CompletableFuture<?>[] futures = new CompletableFuture[]{traceCompletableFuture, fetchFeaturesForElementsCompletableFuture};

        // wait until all async methods have finished before updating data
        CompletableFuture.allOf(futures).whenCompleteAsync((future, exception) -> {
            isTraceInProgressProperty.set(false);
            if (exception == null && traceResultInProgress != null) {
                // if there were no exceptions from either task and the result is not null, configure the data
                traceResultInProgress.setName(name);
                if (traceResultInProgress.getExtent() != null && autoZoomToResultsProperty.get()) {
                    // update the viewpoint if an extent has been set and autoZoomToResults is true
                    mapViewProperty.get().setViewpoint(new Viewpoint(traceResultInProgress.getExtent()));
                }
                // add the result to the list and then reset
                traceResultsProperty.add(traceResultInProgress);
                traceResultInProgress = null;
            }
            // whether successful or not reset the data
            traceInProgressFuture = null;
            fetchFeaturesForElementsFuture = null;
            applyStartingPointWarnings();
        });

        isTraceInProgressProperty.set(true);
        var selectedUtilityNetwork = selectedUtilityNetworkProperty.get();
        try {
            if (selectedUtilityNetwork == null) {
                // if the selected utility network is null, throw an exception and handle the futures
                throw new IllegalArgumentException("No Utility Network Selected.");
            }
        } catch (IllegalArgumentException e) {
            traceCompletableFuture.completeExceptionally(e);
            fetchFeaturesForElementsCompletableFuture.cancel(true);
        }

        if (selectedUtilityNetwork != null) {
            var selectedTraceConfiguration = selectedTraceConfigurationProperty.get();
            List<UtilityElement> utilityElementsForStartingPoints = new ArrayList<>();
            startingPointsProperty.forEach(sp -> utilityElementsForStartingPoints.add(sp.getUtilityElement()));

            System.out.println("here: " + utilityElementsForStartingPoints.get(0).getFractionAlongEdge());

            try {
                // create utility trace parameters from the trace configuration and starting points
                UtilityTraceParameters utilityTraceParameters =
                        new UtilityTraceParameters(selectedTraceConfiguration, utilityElementsForStartingPoints);
                // instantiate the utility network trace operation result that results data will be added to
                traceResultInProgress =
                        new UtilityNetworkTraceOperationResult(utilityTraceParameters);

                // run the trace and get the results
                traceInProgressFuture =
                        selectedUtilityNetwork.traceAsync(utilityTraceParameters);
                traceInProgressFuture.addDoneListener(() -> {
                    try {
                        List<UtilityTraceResult> utilityTraceResults = traceInProgressFuture.get();

                        // set the raw results to the current result
                        traceResultInProgress.getRawResults().addAll(utilityTraceResults);

                        var containsUtilityElementResult = false;

                        // loop through the results
                        for (var utilityTraceResult : utilityTraceResults) {

                            // if there are any warnings, add to the in progress result
                            if (!utilityTraceResult.getWarnings().isEmpty()) {
                                traceResultInProgress.getWarnings().addAll(utilityTraceResult.getWarnings());
                            }

                            if (utilityTraceResult instanceof UtilityElementTraceResult) {
                                // handle utility element results
                                containsUtilityElementResult = true;

                                UtilityElementTraceResult utilityElementTraceResult =
                                        (UtilityElementTraceResult) utilityTraceResult;
                                // add the element trace results to the current result
                                traceResultInProgress.getElementResults().addAll(
                                        FXCollections.observableArrayList(utilityElementTraceResult.getElements()));
                                traceResultInProgress.setElementResultsByAssetGroup(
                                        traceResultInProgress.getElementResults().stream()
                                                .collect(groupingBy(UtilityElement::getAssetGroup)));
                                // fetch the features to be displayed on the map via an async method
                                fetchFeaturesForElementsFuture =
                                        selectedUtilityNetwork.fetchFeaturesForElementsAsync(utilityElementTraceResult.getElements());
                                fetchFeaturesForElementsFuture.addDoneListener(() -> {
                                    try {
                                        var features = fetchFeaturesForElementsFuture.get();
                                        // add the features to the current result and select the results
                                        traceResultInProgress.getFeatures().addAll(FXCollections.observableArrayList(features));
                                        traceResultInProgress.selectResultFeatures(true);
                                        if (autoZoomToResultsProperty.get()) {
                                            // if authZoomToResult is true, update the viewpoint of the MapView
                                            mapViewProperty.get().setViewpoint(new Viewpoint(traceResultInProgress.getExtent()));
                                        }
                                        // if this is successful, complete the future
                                        fetchFeaturesForElementsCompletableFuture.complete(null);
                                    } catch (CancellationException e) {
                                        // if the async fetch method is cancelled, complete the futures
                                        fetchFeaturesForElementsCompletableFuture.completeExceptionally(e);
                                        traceCompletableFuture.complete(null);
                                    } catch (Exception e) {
                                        // if fetch fails due to another reason, set the error to the result and complete
                                        traceResultInProgress.setException(e);
                                        fetchFeaturesForElementsCompletableFuture.completeExceptionally(e);
                                        traceCompletableFuture.cancel(true);
                                    }
                                });
                            } else if (utilityTraceResult instanceof UtilityGeometryTraceResult) {
                                // handle geometry results and add graphics to the graphics overlay
                                var geometryTraceResult = (UtilityGeometryTraceResult) utilityTraceResult;

                                var multipoint = geometryTraceResult.getMultipoint();
                                if (multipoint != null) {
                                    var graphic = new Graphic(multipoint, new SimpleMarkerSymbol(getResultPointSymbol().getStyle(),
                                            getResultPointSymbol().getColor(), getResultPointSymbol().getSize()));
                                    traceResultInProgress.getGraphics().add(graphic);
                                    traceResultInProgress.getResultsGraphicsOverlay().getGraphics().add(graphic);
                                }

                                var polyline = geometryTraceResult.getPolyline();
                                if (polyline != null) {
                                    var graphic = new Graphic(polyline, new SimpleLineSymbol(getResultLineSymbol().getStyle(),
                                            getResultLineSymbol().getColor(), getResultLineSymbol().getWidth()));
                                    traceResultInProgress.getGraphics().add(graphic);
                                    traceResultInProgress.getResultsGraphicsOverlay().getGraphics().add(graphic);
                                }

                                var polygon = geometryTraceResult.getPolygon();
                                if (polygon != null) {
                                    var graphic = new Graphic(polygon, new SimpleFillSymbol(getResultFillSymbol().getStyle(),
                                            getResultFillSymbol().getColor(), getResultFillSymbol().getOutline()));
                                    traceResultInProgress.getGraphics().add(graphic);
                                    traceResultInProgress.getResultsGraphicsOverlay().getGraphics().add(graphic);
                                }
                                // add the graphics to the MapView's graphics overlay
                                getMapView().getGraphicsOverlays().add(traceResultInProgress.getResultsGraphicsOverlay());
                            } else if (utilityTraceResult instanceof UtilityFunctionTraceResult) {
                                // handle function results
                                var functionTraceResult = (UtilityFunctionTraceResult) utilityTraceResult;
                                functionTraceResult.getFunctionOutputs().forEach(functionOutput ->
                                        traceResultInProgress.getFunctionResults().add(functionOutput));
                            }
                        }
                        // if there were no utility element results, complete the feature future
                        if (!containsUtilityElementResult) {
                            fetchFeaturesForElementsCompletableFuture.complete(null);
                        }
                        // if the trace is successful, complete the trace future
                        traceCompletableFuture.complete(null);
                    } catch (CancellationException e) {
                        // if the listenable future was cancelled complete both futures
                        traceCompletableFuture.completeExceptionally(e);
                        fetchFeaturesForElementsCompletableFuture.complete(null);
                    } catch (Exception e) {
                        // if trace fails due to another reason, set the error to the result and complete both futures
                        traceResultInProgress.setException(e);
                        traceCompletableFuture.complete(null);
                        fetchFeaturesForElementsCompletableFuture.complete(null);
                    }
                });
            } catch (CancellationException e) {
                // if the listenable future was cancelled complete both futures
                traceCompletableFuture.completeExceptionally(e);
                fetchFeaturesForElementsCompletableFuture.complete(null);
            } catch (Exception ex) {
                // if trace fails due to another reason, set the error to the result and complete both futures
                traceResultInProgress.setException(ex);
                traceCompletableFuture.complete(null);
                fetchFeaturesForElementsCompletableFuture.complete(null);
            }
        }
    }

    private void onMapViewClicked(MouseEvent e) {
        if (e.getButton() == MouseButton.PRIMARY && e.isStillSincePress()
                && isAddingStartingPointsProperty.get() && selectedUtilityNetworkProperty != null) {
            identifyStartingPoints(e);
        }
    }

    private void identifyStartingPoints(MouseEvent e) {
        // cancel any previous identify tasks
        cancelIdentifyLayers();
        // start new identify
        isAddingStartingPointsProperty.set(false);
        isIdentifyInProgressProperty.set(true);
        // get the clicked map point
        Point2D screenPoint = new Point2D(e.getX(), e.getY());
        Point mapPoint = getMapView().screenToLocation(screenPoint);
        // identify features
        identifyLayersFuture =
                getMapView().identifyLayersAsync(screenPoint, 10, false);
        identifyLayersFuture.addDoneListener(() -> {
            try {
                // get the result of the query and find any identified features
                List<IdentifyLayerResult> identifyLayerResults = identifyLayersFuture.get();
                identifyLayerResults.forEach(identifyLayerResult -> {
                    LayerContent layerContent = identifyLayerResult.getLayerContent();
                    if (layerContent instanceof FeatureLayer && !identifyLayerResult.getElements().isEmpty()) {
                        List<GeoElement> identifiedFeatures = identifyLayerResult.getElements();
                        identifiedFeatures.forEach(identifiedFeature -> {
                            if (identifiedFeature instanceof ArcGISFeature) {
                                // add any identified features as starting points
                                addStartingPoint((ArcGISFeature) identifiedFeature, mapPoint);
                            }
                        });
                    }
                });
            } catch (CancellationException cancellationException) {
                // cancellations are ignored
            } catch (Exception ex) {
                // all other exceptions are ignored
            } finally {
                // reset the data
                identifyLayersFuture = null;
                isIdentifyInProgressProperty.set(false);
            }
        });
    }

    private void applyStartingPointWarnings() {
        if (selectedTraceConfigurationProperty.get() != null) {
            var minimum = selectedTraceConfigurationProperty.get().getMinimumStartingLocations() == UtilityMinimumStartingLocations.MANY ? 2 : 1;
            insufficientStartingPointsProperty.set(startingPointsProperty.size() < minimum);
            aboveMinimumStartingPointsProperty.set(startingPointsProperty.size() > minimum);
        }
    }

    private void cancelTrace() {
        if (traceInProgressFuture != null) {
            traceInProgressFuture.cancel(true);
        }
        if (fetchFeaturesForElementsFuture != null) {
            fetchFeaturesForElementsFuture.cancel(true);
        }
        isTraceInProgressProperty.set(false);
    }

    private void cancelIdentifyLayers() {
        if (identifyLayersFuture != null) {
            identifyLayersFuture.cancel(true);
        }
        isIdentifyInProgressProperty.set(false);
    }

    private void displayLoggerWarning(String message) {
        var logger = Logger.getLogger(UtilityNetworkTraceTool.class.getName());
        logger.warning(message);
    }
}
