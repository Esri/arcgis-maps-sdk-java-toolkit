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

package com.esri.arcgisruntime.toolkit.examples.model;

import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.toolkit.examples.controller.ExampleView;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Defines methods that are required for all Examples that are displayed within an
 * {@link ExampleView}.
 *
 * @since 100.15.0
 */
public interface Example {

    /**
     * Returns the name of the Toolkit Component used in the Example. E.g. Compass.
     *
     * @return a String of the name
     * @since 100.15.0
     */
    String getName();

    /**
     * Returns a description of the Toolkit Component used in the Example.
     *
     * @return a String of the description
     * @since 100.15.0
     */
    String getDescription();

    /**
     * Returns a list of Tabs used to display the views required for the Example.
     *
     * @return a list of Tabs
     * @since 100.15.0
     */
    List<Tab> getTabs();

    /**
     * Returns a VBox containing any settings configured for the Example.
     *
     * @return a VBox containing any required settings
     * @since 100.15.0
     */
    VBox getSettings();

    /**
     * Returns any GeoViews used for the Example. This list may contain one or both of a MapView and a SceneView, or
     * in some cases may not contain either if the Toolkit Component does not require a GeoView.
     *
     * @return a list of GeoViews
     * @since 100.15.0
     */
    List<GeoView> getGeoViews();
}
