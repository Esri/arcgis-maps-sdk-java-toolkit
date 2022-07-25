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

import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.symbology.ColorUtil;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.utilitynetworks.*;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UtilityNetworkTraceOperationResult {

    private final SimpleObjectProperty<Color> visualizationColorProperty = new SimpleObjectProperty<>(Color.BLUE);
    private final SimpleBooleanProperty isSelectedProperty = new SimpleBooleanProperty(false);

    private Exception exception = null;
    private final GraphicsOverlay resultsGraphicsOverlay = new GraphicsOverlay();
    private final List<ArcGISFeature> features = new ArrayList<>();
    private final List<Graphic> graphics = new ArrayList<>();
    private final List<String> warnings = new ArrayList<>();
    private final List<UtilityElement> elementResults = new ArrayList<>();
    private final List<UtilityTraceFunctionOutput> functionResults = new ArrayList<>();
    private final List<UtilityTraceResult> rawResults = new ArrayList<>();
    private final UtilityTraceParameters traceParameters;
    private Map<UtilityAssetGroup, List<UtilityElement>> elementResultsByAssetGroup = new HashMap<>();
    private String name = "";

    protected UtilityNetworkTraceOperationResult(UtilityTraceParameters traceParameters) {
        this.traceParameters = traceParameters;
        // add a listener to update the visualization color
        visualizationColorProperty.addListener(((observable, oldValue, newValue) -> updateVisualizationColor(newValue)));
        // add a listener to select or unselect features
        isSelectedProperty.addListener(((observable, oldValue, newValue) -> selectResultFeatures(newValue)));
    }

    public SimpleObjectProperty<Color> visualizationColorProperty() {
        return visualizationColorProperty;
    }

    public SimpleBooleanProperty isSelectedProperty() { return isSelectedProperty; }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public GraphicsOverlay getResultsGraphicsOverlay() {
        return resultsGraphicsOverlay;
    }

    public List<ArcGISFeature> getFeatures() { return features; }

    public List<Graphic> getGraphics() {
        return graphics;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public List<UtilityElement> getElementResults() {
        return elementResults;
    }

    public Map<UtilityAssetGroup, List<UtilityElement>> getElementResultsByAssetGroup() {
        return elementResultsByAssetGroup;
    }

    public void setElementResultsByAssetGroup(Map<UtilityAssetGroup, List<UtilityElement>> elementResultsByAssetGroup) {
        this.elementResultsByAssetGroup = elementResultsByAssetGroup;
    }

    public List<UtilityTraceFunctionOutput> getFunctionResults() { return functionResults; }

    public List<UtilityTraceResult> getRawResults() { return rawResults; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public UtilityTraceParameters getTraceParameters() {
        return traceParameters;
    }

    public void selectResultFeatures(Boolean isSelectFeatures) {
        Map<Layer, List<ArcGISFeature>> groups =
                features.stream().collect(Collectors.groupingBy(feature -> feature.getFeatureTable().getLayer()));

        if (isSelectFeatures) {
            isSelectedProperty.set(true);
            for (Map.Entry<Layer, List<ArcGISFeature>> group : groups.entrySet()) {
                if (group.getKey() instanceof FeatureLayer) {
                    ((FeatureLayer) group.getKey()).selectFeatures(FXCollections.observableArrayList(group.getValue()));
                }
            }
        } else {
            isSelectedProperty.set(false);
            for (Map.Entry<Layer, List<ArcGISFeature>> group : groups.entrySet()) {
                if (group.getKey() instanceof FeatureLayer) {
                    ((FeatureLayer) group.getKey()).unselectFeatures(FXCollections.observableArrayList(group.getValue()));
                }
            }
        }
    }

    public Envelope getExtent() {
        Envelope graphicsExtent = null;
        Envelope featuresExtent = null;

        if (!resultsGraphicsOverlay.getGraphics().isEmpty()) {
            // if there are graphics, get the extent of the graphics overlay
            graphicsExtent = resultsGraphicsOverlay.getExtent();
        }
        if (!features.isEmpty()) {
            // if there are features, combine their geometries
            featuresExtent = GeometryEngine.combineExtents(features.stream().map(Feature::getGeometry).collect(Collectors.toList()));
        }

        if (featuresExtent != null && graphicsExtent != null) {
            // if there are features and graphics, combine their extents
            return GeometryEngine.combineExtents(graphicsExtent, featuresExtent);
        } else if (featuresExtent != null) {
            return featuresExtent;
        } else return graphicsExtent;
    }

    public Boolean hasAnyResults() {
        return !graphics.isEmpty() || !elementResults.isEmpty() || !functionResults.isEmpty();
    }

    private void updateVisualizationColor(Color color) {
        graphics.forEach(graphic -> {
            var symbol = graphic.getSymbol();
            if (symbol instanceof SimpleMarkerSymbol) {
                var marker = (SimpleMarkerSymbol) symbol;
                marker.setColor(ColorUtil.colorToArgb(color));
            } else if (symbol instanceof SimpleLineSymbol) {
                var marker = (SimpleLineSymbol) symbol;
                marker.setColor(ColorUtil.colorToArgb(color));
            } else if (symbol instanceof SimpleFillSymbol) {
                var fillMarker = (SimpleFillSymbol) symbol;
                fillMarker.setColor(ColorUtil.colorToArgb(color));
                if (fillMarker.getOutline() instanceof SimpleLineSymbol) {
                    var outline = (SimpleLineSymbol) fillMarker.getOutline();
                    outline.setColor(ColorUtil.colorToArgb(color));
                }
            }
        });
    }
}
