# arcgis-runtime-toolkit-java

## Features

The latest version of the ArcGIS Runtime Toolkit for Java features the following JavaFX components:

- Compass: Shows the current viewpoint heading. Can be clicked to reorient the view to north.
- Overview Map: Indicates the viewpoint of the main map/scene view.
- Scalebar: Shows a ruler with units proportional to the map's current scale.

## Instructions

The toolkit library jar is hosted on https://esri.jfrog.io/artifactory/arcgis.

To add the dependency to your project using Gradle:
```groovy
plugins {
    id 'application'
    id 'org.openjfx.javafxplugin' version '0.0.8'
}

// Replace with version number of ArcGIS SDK you are using in your app, such as:
// arcgisVersion = '100.11.0'. See table below for SDK Versions that support the toolkit.
ext {
  arcgisVersion = '100.2.1'
}

javafx {
    version = "11.0.2"
    modules = [ 'javafx.controls' ]
}

compileJava.options.encoding = 'UTF-8'

// Toolkit and Runtime SDK repository
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
    implementation 'com.esri.arcgisruntime:arcgis-java-toolkit:100.2.1'
}
```

## Requirements

The toolkit requires the ArcGIS Runtime SDK for Java. Refer to the Instructions section above if you are using Gradle.
See [the guide](https://developers.arcgis.com/java/install-and-set-up/) for complete instructions and
other options for installing the SDK.

The following table shows which versions of the SDK are compatible with the toolkit:

|  SDK Version  |  Toolkit Version  |
| --- | --- |
| 100.2.1 or later | 100.2.1 |

## Resources

* [ArcGIS Runtime SDK for Java](https://developers.arcgis.com/java/)
* [ArcGIS Blog](http://blogs.esri.com/esri/arcgis/)
* [twitter@esri](http://twitter.com/esri)

## Issues

Find a bug or want to request a new feature?  Please let us know by submitting an issue.

## Contributing

Esri welcomes contributions from anyone and everyone. Please see our [guidelines for contributing](https://github.com/esri/contributing).

## Licensing
Copyright 2018 Esri

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
