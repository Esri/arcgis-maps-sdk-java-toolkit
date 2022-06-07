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

public interface Example {

    VBox getSettings();

    String getExampleName();

    List<Tab> getTabs();

    String getDescription();

    List<GeoView> getGeoViews();
}
