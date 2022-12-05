# arcgis-java-toolkit

## Introduction

The ArcGIS Maps SDK for Java Toolkit contains controls and utilities to simplify your app development. The toolkit is provided as an open source resource (licensed under the Apache License Version 2.0), so you can feel free to download or clone the code and customize to meet your requirements.

## Features

The latest version of the ArcGIS Maps SDK for Java Toolkit features the following JavaFX components:

- Compass: Shows the current viewpoint heading. Can be clicked to reorient the view to north.
- Feature Template Picker: Shows feature templates for a collection of feature layers.
- Floor Filter: Shows sites and facilities, and enables toggling the visibility of levels on floor aware maps and scenes.
- Overview Map: Indicates the viewpoint of the main map/scene view.
- Scalebar: Shows a ruler with units proportional to the map's current scale.
- Utility Network Trace Tool: Use named trace configurations defined in a web map to perform connected trace operations and compare results.

## Requirements

The toolkit requires the ArcGIS Maps SDK for Java. Refer to the 'Instructions' section below if you are using Gradle.
See [the developer guide](https://developers.arcgis.com/java/install-and-set-up/) for complete instructions and
getting setup with the SDK.

The following table shows the minimum version of the SDK compatible with the toolkit:

| SDK Version | Toolkit Version |
|-------------|-----------------|
| 100.2.1     | 100.2.1         |
| 100.14.0    | 100.14.0        |
| 100.15.0    | 100.15.0        |

### API Key requirements

Some of the toolkit components utilize ArcGIS Platform services which require an API key. Refer to the 'Access services' section of the 
[Get Started guide](https://developers.arcgis.com/java/get-started/#3-access-services-and-content-with-an-api-key) 
for more information. Help with how to set your API key can be found in the 
[Developer Guide tutorials](https://developers.arcgis.com/java/maps-2d/tutorials/display-a-map/#set-your-api-key)
and [Java Samples Repository](https://github.com/Esri/arcgis-runtime-samples-java). If a toolkit component requires an API
key, this will be indicated within the JavaDoc for the component.

## Instructions

The toolkit library jar is hosted on https://esri.jfrog.io/artifactory/arcgis.

To add the dependency to your project using Gradle:
```groovy
plugins {
    id 'application'
    id 'org.openjfx.javafxplugin' version '0.0.8'
}

// Replace with version number of ArcGIS Maps SDK for Java you are using in your app, such as:
// arcgisVersion = '100.15.0'. See table below for SDK Versions that support the toolkit.
ext {
  arcgisVersion = '100.15.0'
}

javafx {
    version = "11.0.2"
    modules = [ 'javafx.controls', 'javafx.fxml' ]
}

compileJava.options.encoding = 'UTF-8'

// Toolkit and ArcGIS Maps SDK for Java repository
repositories {
    jcenter()
    maven {
        url 'https://esri.jfrog.io/artifactory/arcgis'
    }
}

configurations {
    natives
}

dependencies {
    implementation "com.esri.arcgisruntime:arcgis-java:$arcgisVersion"
    natives "com.esri.arcgisruntime:arcgis-java-jnilibs:$arcgisVersion"
    natives "com.esri.arcgisruntime:arcgis-java-resources:$arcgisVersion"
    implementation 'com.esri.arcgisruntime:arcgis-java-toolkit:100.15.0'
}
```

The toolkit is open source (licensed under the Apache License Version 2.0), so you are also free to clone or download this repository, customize to meet your requirements, and then build and deploy using Gradle.

## Issues

Find a bug or want to request a new feature?  Please let us know by submitting an issue.

## Contributing

Esri welcomes contributions from anyone and everyone. Please see our [guidelines for contributing](https://github.com/esri/contributing).

## Licensing
Copyright 2018-2022 Esri

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

A copy of the license is available in the repository's [LICENSE.txt](LICENSE.txt) file.
