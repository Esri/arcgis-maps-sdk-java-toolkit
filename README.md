# arcgis-runtime-toolkit-java

## Features

The latest version of the ArcGIS Runtime Toolkit for Java features the following JavaFX components:

- Compass: Shows the current viewpoint heading. Can be clicked to reorient the view to north.
- Overview Map: Indicates the viewpoint of the main map/scene view.
- Scalebar: Shows a ruler with units proportional to the map's current scale.
- Template Picker: Shows feature templates for a collection of feature layers.

## Instructions

The toolkit library jar is hosted on https://bintray.com/esri/arcgis.

To add the dependency to your project using Gradle:
```groovy
ext {
  arcgisVersion = "100.7.0"
}

repositories {
  maven {
      url 'https://esri.bintray.com/arcgis'
  }
}

dependencies {
  // toolkit
  compile "com.esri.arcgisruntime:arcgis-java-toolkit:$arcgisVersion"
  // api
  compile "com.esri.arcgisruntime:arcgis-java:$arcgisVersion" 
  // native libraries
  natives "com.esri.arcgisruntime:arcgis-java-jnilibs:$arcgisVersion"
  natives "com.esri.arcgisruntime:arcgis-java-resources:$arcgisVersion"
}

task copyNatives(type: Copy) {
  description = "Copies the arcgis native libraries into USER_HOME/.arcgis for development."
  group = "build"
  configurations.natives.asFileTree.each {
    from(zipTree(it))
  }
  into "${System.properties.getProperty("user.home")}/.arcgis/$arcgisVersion"
}
```

## Requirements

The toolkit requires the ArcGIS Runtime SDK for Java. Refer to the Instructions section above if you are using Gradle.
See [the guide](https://developers.arcgis.com/java/latest/guide/install-the-sdk.htm) for complete instructions and
other options for installing the SDK.

The following table shows the minimum version of the SDK compatible with the toolkit:

|  SDK Version  |  Toolkit Version  |
| --- | --- |
| 100.2.1 | 100.2.1 |
| 100.7.0 | 100.7.0 |

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
