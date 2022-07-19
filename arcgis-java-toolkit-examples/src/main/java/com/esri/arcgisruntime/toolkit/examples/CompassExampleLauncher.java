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

package com.esri.arcgisruntime.toolkit.examples;

import com.esri.arcgisruntime.toolkit.utils.ExampleUtils;

/**
 * A Launcher class for the {@link CompassExample}.
 *
 * @since 100.15.0
 */
public class CompassExampleLauncher {
    public static void main(String[] args) {
        // configure the API Key
        // authentication with an API key or named user is required to access basemaps and other location services
        ExampleUtils.configureAPIKeyForRunningStandAloneExample();
        // run the app
        CompassExample.main(args);
    }
}
