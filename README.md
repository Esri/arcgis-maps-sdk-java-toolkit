# arcgis-runtime-toolkit-java

## Features

The latest version of the ArcGIS Runtime Toolkit for Java features the following JavaFX components:

- Compass: Shows the current viewpoint heading. Can be clicked to reorient the view to north.
- Overview Map: Indicates the viewpoint of the main map/scene view.
- Scalebar: Shows a ruler with units proportional to the map's current scale.

### BookmarkListView

The `BookmarkListView` control allows you to display a list view of bookmarks which is kept synchronized with a
 `GeoView`'s map/scene's bookmarks. When a bookmark is selected from the list, it's viewpoint is set on the `GeoView
 `. When the user navigates away from the viewpoint, the item in the list is deselected.
 
#### Basic Usage
 
 ```java
// unbound list of bookmarks
BookmarksView bookmarksView = new BookmarksView(map.getBookmarks());

// list bound to map
BookmarksView bookmarksView = new BookmarksView(ListenableListUtils.toObservableList(map.getBookmarks()));

// switch binding to another map
bookmarksView.setBookmarks(ListenableListUtils.toObservableList(map2.getBookmarks()));
```

#### Customization

The control's default `BookmarkListViewSkin` applies a cell factory which displays the name of the bookmark. The cell
 factory can be overridden on the skin with `bookmarkListViewSkin#setCellFactory(Callback<ListView<Bookmark>, ListCell
 <Bookmark>>)`. The skin can then be replaced with the custom skin with `bookmarksView.setSkin(customSkin)`.

## Instructions

The toolkit library jar is hosted on https://bintray.com/esri/arcgis.

To add the dependency to your project using Gradle:
```groovy
apply plugin: 'application'

// Runtime SDK dependency
apply plugin: 'com.esri.arcgisruntime.java'
buildscript {
    repositories {
        maven {
            url 'https://esri.bintray.com/arcgis'
        }
    }
    dependencies {
        classpath 'com.esri.arcgisruntime:gradle-arcgis-java-plugin:1.0.0'
    }
}
arcgis.version = '100.2.1'

// Toolkit dependency
repositories {
  maven {
      url 'https://esri.bintray.com/arcgis'
  }
}

dependencies {
  compile 'com.esri.arcgisruntime:arcgis-java-toolkit:100.2.1'
}
```

## Requirements

The toolkit requires the ArcGIS Runtime SDK for Java. Refer to the Instructions section above if you are using Gradle.
See [the guide](https://developers.arcgis.com/java/latest/guide/install-the-sdk.htm) for complete instructions and
other options for installing the SDK.

The following table shows which versions of the SDK are compatible with the toolkit:

|  SDK Version  |  Toolkit Version  |
| --- | --- |
| 100.2.1 | 100.2.1 |

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
