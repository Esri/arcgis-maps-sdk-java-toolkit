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
import com.esri.arcgisruntime.mapping.view.SketchEditor;
import com.esri.arcgisruntime.mapping.view.SketchStyle;
import com.esri.arcgisruntime.symbology.FillSymbol;
import com.esri.arcgisruntime.symbology.LineSymbol;
import com.esri.arcgisruntime.symbology.MarkerSymbol;
import com.esri.arcgisruntime.symbology.MultilayerPointSymbol;
import com.esri.arcgisruntime.symbology.MultilayerPolygonSymbol;
import com.esri.arcgisruntime.symbology.MultilayerPolylineSymbol;
import com.esri.arcgisruntime.symbology.MultilayerSymbol;
import com.esri.arcgisruntime.symbology.Symbol;

public final class SketchEditorTemplateHelper {

  public static SketchCreationMode getSketchCreationMode(FeatureTemplate template, FeatureLayer featureLayer) {
    SketchEditor sketchEditor = new SketchEditor();

    GeometryType geometryType = Objects.requireNonNull(featureLayer).getFeatureTable().getGeometryType();
    FeatureTemplate.DrawingTool drawingTool = Objects.requireNonNull(template).getDrawingTool();

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

  public static SketchStyle getSketchStyle(FeatureTemplate template, FeatureLayer featureLayer) {
    Graphic graphic = new Graphic();
    graphic.getAttributes().putAll(template.getPrototypeAttributes());
    Symbol symbol = featureLayer.getRenderer().getSymbol(graphic);

    SketchStyle sketchStyle = new SketchStyle();

    if (symbol instanceof MultilayerSymbol) {
      if (symbol instanceof MultilayerPointSymbol) {
        sketchStyle.setVertexSymbol((MultilayerPointSymbol) symbol);
        sketchStyle.setFeedbackVertexSymbol((MultilayerPointSymbol) sketchStyle.getVertexSymbol());
        sketchStyle.setSelectedVertexSymbol((MultilayerPointSymbol) sketchStyle.getVertexSymbol());
      }
      if (symbol instanceof MultilayerPolylineSymbol) {
        sketchStyle.setLineSymbol((MultilayerPolylineSymbol) symbol);
        sketchStyle.setFeedbackLineSymbol((MultilayerPolylineSymbol) sketchStyle.getLineSymbol());
      }
      if (symbol instanceof MultilayerPolygonSymbol) {
        sketchStyle.setFillSymbol((MultilayerPolygonSymbol) symbol);
        sketchStyle.setFeedbackFillSymbol((MultilayerPolygonSymbol) sketchStyle.getFillSymbol());
      }
    } else {
      if (symbol instanceof MarkerSymbol) {
        sketchStyle.setVertexSymbol((MarkerSymbol) symbol);
        sketchStyle.setFeedbackVertexSymbol((MarkerSymbol) sketchStyle.getVertexSymbol());
        sketchStyle.setSelectedVertexSymbol((MarkerSymbol) sketchStyle.getVertexSymbol());
      }
      if (symbol instanceof LineSymbol) {
        sketchStyle.setLineSymbol((LineSymbol) symbol);
        sketchStyle.setFeedbackLineSymbol((LineSymbol) sketchStyle.getLineSymbol());
      }
      if (symbol instanceof FillSymbol) {
        sketchStyle.setFillSymbol((FillSymbol) symbol);
        sketchStyle.setFeedbackFillSymbol((FillSymbol) sketchStyle.getFillSymbol());
      }
    }

    return sketchStyle;
  }
}
