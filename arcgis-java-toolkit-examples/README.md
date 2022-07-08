# arcgis-java-toolkit-examples

## Introduction

This project provides an Example App to view demo implementations of the ArcGIS Runtime API for Java Toolkit components.

## Features

- A main Example App run via Gradle for browsing through a demo of each of the components.
- Additional optional settings for viewing and testing the capabilities of the components.
- Individual Launcher Classes for each component to enable more efficient development and/or a more focused approach to viewing individual tools.

## Instructions

The Example App requires the `arcgis-java-toolkit` project to run. The whole repository `arcgis-runtime-toolkit-java` must be cloned before following the below steps, ensuring the project directories for `arcgis-java-toolkit-examples` and `arcgis-java-toolkit` are next to each other. This enables straightforward testing of any amendments made to the toolkit components as you work.

### IntelliJ IDEA

1. Open Intellij IDEA and select File > Open....
2. Select the `arcgis-runtime-toolkit-java` folder that you cloned, which contains both the `arcgis-java-toolkit` and `arcgis-java-toolkit-examples` projects.
3. Click OK.
4. Select File > Project Structure... and ensure that the Project SDK and language level are set to use Java 11.
5. Store your API key in the gradle.properties file located in the /.gradle folder within your home directory. The API key will be set as a Java system property when the sample is run. `apiKey = yourApiKey`
6. Open the Gradle view with View > Tool Windows > Gradle.
7. In the Gradle view, double-click the run task under Tasks > application to run the main Example App.
Note: if you encounter the error `Could not get unknown property 'apiKey' for task ':run' of type org.gradle.api.tasks.JavaExec` you may have to set the Gradle user home in the IntelliJ Gradle settings to the /.gradle folder in your home directory, or may not have added your API key to your gradle.properties file.
8. An alternative to steps 6 and 7, is to run an individual demo by right-clicking on a "Launcher" class, e.g. `com.esri.arcgisruntime.toolkit.examples.CompassExampleLauncher`, and selecting "Run CompassExampleLauncher.main()".

### API Key requirements

Some of the toolkit components utilize ArcGIS Platform services which require an API key. Refer to the 'Access services' section of the
[Get Started guide](https://developers.arcgis.com/java/get-started/#3-access-services-and-content-with-an-api-key)
for more information. Help with how to set your API key can be found in the
[Developer Guide tutorials](https://developers.arcgis.com/java/maps-2d/tutorials/display-a-map/#set-your-api-key)
and [Java Samples Repository](https://github.com/Esri/arcgis-runtime-samples-java). If a toolkit component requires an API
key, this will be indicated within the JavaDoc for the component.

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
