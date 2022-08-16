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

import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.symbology.Symbol;
import com.esri.arcgisruntime.utilitynetworks.UtilityElement;
import com.esri.arcgisruntime.utilitynetworks.UtilityNetworkSource;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * A model for a starting point used for running a utility network trace by a {@link UtilityNetworkTraceTool}.
 *
 * @since 100.15.0
 */
public class UtilityNetworkTraceStartingPoint {

  private final SimpleDoubleProperty fractionAlongEdgeProperty = new SimpleDoubleProperty();

  private Boolean hasMultipleTerminals = false;
  private Boolean hasFractionAlongEdge = false;
  private final Envelope extent;
  private final Graphic graphic;
  private final Symbol featureSymbol;
  private final UtilityElement utilityElement;

  /**
   * Creates a UtilityNetworkTraceStartingPoint.
   *
   * @param utilityElement the utility element the starting point represents
   * @param graphic the graphic used to display the on the MapView
   * @param featureSymbol the symbol of the feature itself
   * @param extent the extent
   * @since 100.15.0
   */
  protected UtilityNetworkTraceStartingPoint(
    UtilityElement utilityElement, Graphic graphic, Symbol featureSymbol, Envelope extent) {
    this.utilityElement = utilityElement;
    this.graphic = graphic;
    this.featureSymbol = featureSymbol;
    this.extent = extent;
    // Determine whether the starting point has multiple terminals.
    // Can be used to display terminal picker in UI.
    if (utilityElement.getAssetType().getTerminalConfiguration() != null &&
      utilityElement.getAssetType().getTerminalConfiguration().getTerminals().size() > 1) {
      hasMultipleTerminals = true;
    }
    // determine whether the starting point requires fraction along edge properties
    if (utilityElement.getNetworkSource().getSourceType() == UtilityNetworkSource.Type.EDGE &&
      graphic != null && graphic.getGeometry() instanceof Polyline) {
      hasFractionAlongEdge = true;
      fractionAlongEdgeProperty.set(utilityElement.getFractionAlongEdge());
      var polyline = (Polyline) graphic.getGeometry();
      graphic.setGeometry(
        GeometryEngine.createPointAlong(polyline, GeometryEngine.length(polyline) * fractionAlongEdgeProperty.get()));
      // Add a listener to the fraction along edge property to update the geometry of the graphic to reposition
      // along the line at the new location, and update the fraction along edge value on the utility element.
      fractionAlongEdgeProperty.addListener((observable, oldValue, newValue) -> {
        graphic.setGeometry(
          GeometryEngine.createPointAlong(polyline, GeometryEngine.length(polyline) * newValue.doubleValue()));
        utilityElement.setFractionAlongEdge((Double) newValue);
      });
    }
  }

  /**
   * Returns the fraction along edge property.
   *
   * @return the fraction along edge property. Null if the starting point is not an edge feature
   * @since 100.15.0
   */
  public SimpleDoubleProperty fractionAlongEdgeProperty() {
    return fractionAlongEdgeProperty;
  }

  /**
   * Returns the fraction along edge value.
   *
   * @return the fraction along edge value. Null if the starting point is not an edge feature
   * @since 100.15.0
   */
  public Double getFractionAlongEdge() {
    return fractionAlongEdgeProperty.get();
  }

  /**
   * Sets the fraction along edge value.
   *
   * @param fractionAlongEdge the fraction along edge value.
   * @since 100.15.0
   */
  public void setFractionAlongEdge(Double fractionAlongEdge) {
    fractionAlongEdgeProperty.set(fractionAlongEdge);
  }

  /**
   * Returns a boolean value which determines whether the starting point has a fraction along edge.
   *
   * @return the hasFractionAlongEdge value. True if it does, false otherwise
   * @since 100.15.0
   */
  public Boolean getHasFractionAlongEdge() {
    return hasFractionAlongEdge;
  }

  /**
   * Returns a boolean value which determines whether the starting point has multiple terminals.
   *
   * @return the hasMultipleTerminals value. True if there is more than 1 terminal, false otherwise
   * @since 100.15.0
   */
  public Boolean getHasMultipleTerminals() {
    return hasMultipleTerminals;
  }

  /**
   * Returns an Envelope of the extent of the starting point.
   *
   * @return the extent
   * @since 100.15.0
   */
  public Envelope getExtent() {
    return extent;
  }

  /**
   * Returns the graphic associated with the starting point.
   *
   * @return the graphic
   * @since 100.15.0
   */
  public Graphic getGraphic() {
    return graphic;
  }

  /**
   * Updates the symbol attached to the starting point's graphic.
   *
   * @param symbol te symbol to set to the graphic
   * @since 100.15.0
   */
  public void updateSelectionGraphicSymbol(Symbol symbol) {
    graphic.setSymbol(symbol);
  }

  /**
   * Returns the feature symbol for the starting point.
   *
   * @return the symbol
   * @since 100.15.0
   */
  public Symbol getFeatureSymbol() {
    return featureSymbol;
  }

  /**
   * Returns the UtilityElement associated with the starting point.
   *
   * @return the utility element
   * @since 100.15.0
   */
  public UtilityElement getUtilityElement() {
    return utilityElement;
  }
}
