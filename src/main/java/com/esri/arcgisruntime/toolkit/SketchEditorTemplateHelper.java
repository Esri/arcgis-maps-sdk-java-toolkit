/*
 * Copyright 2019 Esri
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

import java.util.Objects;

import com.esri.arcgisruntime.data.FeatureTemplate;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.SketchCreationMode;
import com.esri.arcgisruntime.mapping.view.SketchStyle;
import com.esri.arcgisruntime.symbology.FillSymbol;
import com.esri.arcgisruntime.symbology.LineSymbol;
import com.esri.arcgisruntime.symbology.MarkerSymbol;
import com.esri.arcgisruntime.symbology.MultilayerPointSymbol;
import com.esri.arcgisruntime.symbology.MultilayerPolygonSymbol;
import com.esri.arcgisruntime.symbology.MultilayerPolylineSymbol;
import com.esri.arcgisruntime.symbology.MultilayerSymbol;
import com.esri.arcgisruntime.symbology.Symbol;

/**
 * A helper class to create a SketchStyle and a SketchCreationMode for a FeatureTemplate.
 *
 * @since 100.5.0
 */
public final class SketchEditorTemplateHelper {

  /**
   * Creates a sketch creation mode that best matches the template.
   *
   * @param featureTemplate the feature template
   * @param featureLayer the feature layer the template will be used with
   * @return the sketch creation mode
   * @throws NullPointerException if featureTemplate is null
   * @throws NullPointerException if featureLayer is null
   * @since 100.5.0
   */
  public static SketchCreationMode createsSketchCreationMode(FeatureTemplate featureTemplate, FeatureLayer featureLayer) {
    GeometryType geometryType = Objects.requireNonNull(featureLayer, "featureLayer").getFeatureTable().getGeometryType();
    FeatureTemplate.DrawingTool drawingTool = Objects.requireNonNull(featureTemplate, "featureTemplate").getDrawingTool();

    // creation mode
    SketchCreationMode creationMode;
    switch (drawingTool) {
      case FREEHAND:
        switch (geometryType) {
          case POLYGON:
            creationMode = SketchCreationMode.FREEHAND_POLYGON;
            break;
          case POLYLINE:
            creationMode = SketchCreationMode.FREEHAND_LINE;
            break;
          default:
            throw new RuntimeException(geometryType.name() + " is not supported");
        }
        break;
      case LINE:
        creationMode = SketchCreationMode.POLYLINE;
        break;
      case POINT:
        switch (geometryType) {
          case POINT:
            creationMode = SketchCreationMode.POINT;
            break;
          case MULTIPOINT:
            creationMode = SketchCreationMode.MULTIPOINT;
            break;
          default:
            throw new RuntimeException(geometryType.name() + " is not supported");
        }
        break;
      case POLYGON:
        creationMode = SketchCreationMode.POLYGON;
        break;
      default:
        throw new RuntimeException(drawingTool.name() + " is not supported");
    }

    return creationMode;
  }

  /**
   * Creates a sketch style suitable for the feature template and feature layer.
   *
   * @param featureTemplate the feature template
   * @param featureLayer the feature layer the template will be used with
   * @return the sketch style
   * @throws NullPointerException if featureTemplate is null
   * @throws NullPointerException if featureLayer is null
   * @since 100.5.0
   */
  public static SketchStyle createSketchStyle(FeatureTemplate featureTemplate, FeatureLayer featureLayer) {
    Graphic graphic = new Graphic();
    graphic.getAttributes().putAll(Objects.requireNonNull(featureTemplate, "featureTemplate").getPrototypeAttributes());
    Symbol symbol = Objects.requireNonNull(featureLayer, "featureLayer").getRenderer().getSymbol(graphic);

    SketchStyle sketchStyle = new SketchStyle();

    if (symbol instanceof MultilayerSymbol) {
      if (symbol instanceof MultilayerPointSymbol) {
        sketchStyle.setMultilayerVertexSymbol((MultilayerPointSymbol) symbol);
        sketchStyle.setMultilayerFeedbackVertexSymbol(sketchStyle.getMultilayerVertexSymbol());
        sketchStyle.setMultilayerSelectedVertexSymbol(sketchStyle.getMultilayerVertexSymbol());
      }
      if (symbol instanceof MultilayerPolylineSymbol) {
        sketchStyle.setMultilayerLineSymbol((MultilayerPolylineSymbol) symbol);
        sketchStyle.setMultilayerFeedbackLineSymbol(sketchStyle.getMultilayerLineSymbol());
      }
      if (symbol instanceof MultilayerPolygonSymbol) {
        sketchStyle.setMultilayerFillSymbol((MultilayerPolygonSymbol) symbol);
        sketchStyle.setMultilayerFeedbackFillSymbol(sketchStyle.getMultilayerFillSymbol());
      }
    } else {
      if (symbol instanceof MarkerSymbol) {
        sketchStyle.setVertexSymbol((MarkerSymbol) symbol);
        sketchStyle.setFeedbackVertexSymbol(sketchStyle.getVertexSymbol());
        sketchStyle.setSelectedVertexSymbol(sketchStyle.getVertexSymbol());
      }
      if (symbol instanceof LineSymbol) {
        sketchStyle.setLineSymbol((LineSymbol) symbol);
        sketchStyle.setFeedbackLineSymbol(sketchStyle.getLineSymbol());
      }
      if (symbol instanceof FillSymbol) {
        sketchStyle.setFillSymbol((FillSymbol) symbol);
        sketchStyle.setFeedbackFillSymbol(sketchStyle.getFillSymbol());
      }
    }

    return sketchStyle;
  }
}
