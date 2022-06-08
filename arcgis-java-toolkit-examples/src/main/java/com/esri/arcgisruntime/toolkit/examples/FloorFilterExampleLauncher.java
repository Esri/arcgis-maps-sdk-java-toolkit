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

package com.esri.arcgisruntime.toolkit.examples;

import com.esri.arcgisruntime.toolkit.utils.ExampleUtils;

/**
 * A Launcher class for the {@link FloorFilterExample}.
 *
 * @since 100.15.0
 */
public class FloorFilterExampleLauncher {
    public static void main(String[] args) {
        // configure the API Key
        // authentication with an API key or named user is required to access basemaps and other location services
        ExampleUtils.configureAPIKeyForRunningStandAloneExample();
        // run the app
        FloorFilterExample.main(args);
    }
}
