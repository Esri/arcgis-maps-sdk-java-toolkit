package com.esri.arcgisruntime.toolkit;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Bookmark;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

public class BookmarksViewTest extends ApplicationTest {

    private Scene fxScene;
    private StackPane stackPane;

    @Override
    public void start(Stage primaryStage) {
        stackPane = new StackPane();
        fxScene = new Scene(stackPane);
        primaryStage.setWidth(500);
        primaryStage.setHeight(500);
        primaryStage.setScene(fxScene);
        primaryStage.show();
        primaryStage.toFront();
    }

    @After
    public void cleanup() throws Exception {
        FxToolkit.cleanupStages();
    }

    /**
     * Tests that every bookmark in a map has its name displayed in the view.
     */
    @Test
    public void item_for_every_bookmark_in_map() {
        // given a map view containing a map with bookmarks
        MapView mapView = new MapView();
        Platform.runLater(() -> stackPane.getChildren().add(mapView));

        ArcGISMap map = new ArcGISMap(Basemap.createImagery());
        map.getBookmarks().add(new Bookmark("Guitar-shaped trees", new Viewpoint(-33.867886, -63.985, 4e4)));
        map.getBookmarks().add(new Bookmark("Grand Prismatic Spring", new Viewpoint(44.525049, -110.83819, 6e3)));
        mapView.setMap(map);

        // when the bookmarks view is added with the map view
        BookmarksView bookmarksView = new BookmarksView(mapView);
        bookmarksView.setMaxSize(100, 100);
        StackPane.setAlignment(bookmarksView, Pos.TOP_RIGHT);
        StackPane.setMargin(bookmarksView, new Insets(10));
        Platform.runLater(() -> stackPane.getChildren().add(bookmarksView));

        sleep(2000);

        // every bookmark's name will be displayed in the view
        map.getBookmarks().forEach(bookmark -> clickOn(bookmark.getName()));
    }
}
