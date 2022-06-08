/*
 COPYRIGHT 1995-2022 ESRI

 TRADE SECRETS: ESRI PROPRIETARY AND CONFIDENTIAL
 Unpublished material - all rights reserved under the
 Copyright Laws of the United States.

 For additional information, contact:
 Environmental Systems Research Institute, Inc.
 Attn: Contracts Dept
 380 New York Street
 Redlands, California, USA 92373

 email: contracts@esri.com
 */

package com.esri.arcgisruntime.toolkit.model;

import com.esri.arcgisruntime.mapping.view.GeoView;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Defines methods that are required for all Examples that are displayed within an
 * {@link com.esri.arcgisruntime.toolkit.controller.ExampleView}.
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
