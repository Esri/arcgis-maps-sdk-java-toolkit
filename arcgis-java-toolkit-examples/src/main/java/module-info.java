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
module com.esri.arcgisruntime.toolkit.examples.main {
  // require ArcGISRuntime module
  requires com.esri.arcgisruntime;
  // require ArcGISRuntime toolkit module
  requires com.esri.arcgisruntime.toolkit;
  // require JavaFX modules
  requires javafx.graphics;
  requires javafx.controls;
  requires javafx.web;
  requires javafx.base;
  requires javafx.fxml;
  requires javafx.media;
  requires java.logging;

  exports com.esri.arcgisruntime.toolkit.examples;
  exports com.esri.arcgisruntime.toolkit.examples.examples;
  exports com.esri.arcgisruntime.toolkit.examples.controller;
  exports com.esri.arcgisruntime.toolkit.examples.utils;
  // allow FXML module access to Example FXML files
  opens com.esri.arcgisruntime.toolkit.examples to javafx.fxml;
  opens com.esri.arcgisruntime.toolkit.examples.controller to javafx.fxml;
}
