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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.symbology.ColorUtil;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.utilitynetworks.UtilityAssetGroup;
import com.esri.arcgisruntime.utilitynetworks.UtilityElement;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraceFunctionOutput;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraceParameters;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraceResult;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.paint.Color;

/**
 * A model for the results of a utility network trace run by a {@link UtilityNetworkTraceTool}.
 *
 * @since 100.15.0
 */
public class UtilityNetworkTraceOperationResult {

  private final SimpleObjectProperty<Color> visualizationColorProperty = new SimpleObjectProperty<>(Color.BLUE);
  private final SimpleBooleanProperty isSelectedProperty = new SimpleBooleanProperty(false);

  private Exception exception = null;
  private final GraphicsOverlay resultsGraphicsOverlay = new GraphicsOverlay();
  private final List<ArcGISFeature> features = new ArrayList<>();
  private final List<String> warnings = new ArrayList<>();
  private final List<UtilityElement> elementResults = new ArrayList<>();
  private final List<UtilityTraceFunctionOutput> functionResults = new ArrayList<>();
  private final List<UtilityTraceResult> rawResults = new ArrayList<>();
  private final UtilityTraceParameters traceParameters;
  private Map<UtilityAssetGroup, List<UtilityElement>> elementResultsByAssetGroup = new HashMap<>();
  private String name = "";

  /**
   * Creates a UtilityNetworkTraceOperationResult.
   *
   * @param traceParameters the parameters used to run the trace
   * @since 100.15.0
   */
  protected UtilityNetworkTraceOperationResult(UtilityTraceParameters traceParameters) {
    this.traceParameters = traceParameters;
    // add a listener to update the visualization color
    visualizationColorProperty.addListener(((observable, oldValue, newValue) -> updateVisualizationColor(newValue)));
    // add a listener to select or unselect features
    isSelectedProperty.addListener(((observable, oldValue, newValue) -> selectResultFeatures(newValue)));
  }

  /**
   * Returns a property that contains the color to use for visualizing the results on an ArcGISMap. This helps to
   * differentiate results when there are multiple results on the same map.
   *
   * @return the visualization color property
   * @since 100.15.0
   */
  public SimpleObjectProperty<Color> visualizationColorProperty() {
    return visualizationColorProperty;
  }

  /**
   * Returns a property that contains the value of isSelected.
   *
   * @return true if the graphics are selected, false otherwise
   * @since 100.15.0
   */
  public SimpleBooleanProperty isSelectedProperty() { return isSelectedProperty; }

  /**
   * Returns the exception associated with the trace result.
   *
   * @return the exception. Null if the trace was successful.
   * @since 100.15.0
   */
  public Exception getException() {
    return exception;
  }

  /**
   * Sets an exception to the result.
   *
   * @param exception the exception associated with the trace
   * @since 100.15.0
   */
  public void setException(Exception exception) {
    this.exception = exception;
  }

  /**
   * Returns the graphics overlay containing the graphics for this result. The list of graphics can be accessed from
   * the Graphics Overlay.
   *
   * @return the graphics overlay
   * @since 100.15.0
   */
  public GraphicsOverlay getResultsGraphicsOverlay() {
    return resultsGraphicsOverlay;
  }

  /**
   * Returns the list of features associated with the result.
   *
   * @return the list of features
   * @since 100.15.0
   */
  public List<ArcGISFeature> getFeatures() { return features; }

  /**
   * Returns any warnings that are generated during a trace.
   *
   * @return the warnings
   * @since 100.15.0
   */
  public List<String> getWarnings() {
    return warnings;
  }

  /**
   * Returns the element results, UtilityElement, associated with the trace.
   *
   * @return the list of element results
   * @since 100.15.0
   */
  public List<UtilityElement> getElementResults() {
    return elementResults;
  }

  /**
   * Returns a map of the utility elements organised by UtilityAssetGroup.
   *
   * @return a map of utility elements organised by asset group
   * @since 100.15.0
   */
  public Map<UtilityAssetGroup, List<UtilityElement>> getElementResultsByAssetGroup() {
    return elementResultsByAssetGroup;
  }

  /**
   * Sets the provided Map of utility elements organised by UtilityAssetGroup to the result.
   *
   * @param elementResultsByAssetGroup the Map of utility element results
   * @since 100.15.0
   */
  public void setElementResultsByAssetGroup(Map<UtilityAssetGroup, List<UtilityElement>> elementResultsByAssetGroup) {
    this.elementResultsByAssetGroup = elementResultsByAssetGroup;
  }

  /**
   * Returns the list of function results, UtilityTraceFunctionOutput, associated with the trace.
   *
   * @return the list of function results
   * @since 100.15.0
   */
  public List<UtilityTraceFunctionOutput> getFunctionResults() { return functionResults; }

  /**
   * Returns the list of raw results, UtilityTraceResult, generated by a trace that form the basis for the
   * UtilityNetworkTraceOperationResult.
   *
   * @return the list of results
   * @since 100.15.0
   */
  public List<UtilityTraceResult> getRawResults() { return rawResults; }

  /**
   * Returns the name given to the trace result.
   *
   * @return the result name
   * @since 100.15.0
   */
  public String getName() { return name; }

  /**
   * Sets the name given to the trace result.
   *
   * @param name the name to set
   * @since 100.15.0
   */
  public void setName(String name) { this.name = name; }

  /**
   * Returns the UtilityTraceParameters associated with the trace result.
   *
   * @return the utility trace parameters
   * @since 100.15.0
   */
  public UtilityTraceParameters getTraceParameters() {
    return traceParameters;
  }

  /**
   * Selects or unselects the selection of any features relating to the provided trace result.
   *
   * @param isSelectFeatures true if the features should be selected, false to unselect
   * @since 100.15.0
   */
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

  /**
   * Returns the extent of the graphics and/or features associated with the trace result.
   *
   * @return an Envelope of the extent
   * @since 100.15.0
   */
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

  /**
   * Validates whether the result contains any graphic, element or function results.
   *
   * @return true if there are graphic or element or function results, false otherwise
   * @since 100.15.0
   */
  public boolean hasAnyResults() {
    return !resultsGraphicsOverlay.getGraphics().isEmpty() || !elementResults.isEmpty() || !functionResults.isEmpty();
  }

  /**
   * Validates whether the result failed with an Exception.
   *
   * @return true if there is an Exception, false otherwise
   * @since 100.15.0
   */
  public boolean hasException() {
    return exception != null;
  }

  /**
   * Updates the symbology of all graphics associated with the trace result to the provided color.
   *
   * @param color the new color
   * @since 100.15.0
   */
  private void updateVisualizationColor(Color color) {
    resultsGraphicsOverlay.getGraphics().forEach(graphic -> {
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
