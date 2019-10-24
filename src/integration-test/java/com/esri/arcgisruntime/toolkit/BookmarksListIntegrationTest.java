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
import org.junit.jupiter.api.Assertions;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.concurrent.atomic.AtomicBoolean;

public class BookmarksListIntegrationTest extends ApplicationTest {

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
        BookmarksList bookmarksList = new BookmarksList(mapView);
        bookmarksList.setMaxSize(100, 100);
        StackPane.setAlignment(bookmarksList, Pos.TOP_RIGHT);
        StackPane.setMargin(bookmarksList, new Insets(10));
        Platform.runLater(() -> stackPane.getChildren().add(bookmarksList));

        sleep(10000);

        // every bookmark's name will be displayed in the view
        map.getBookmarks().forEach(bookmark -> clickOn(bookmark.getName()));
    }

    @Test
    public void clicking_item_switches_viewpoint() {
        // given a map view containing a map with bookmarks
        MapView mapView = new MapView();
        Platform.runLater(() -> stackPane.getChildren().add(mapView));

        ArcGISMap map = new ArcGISMap(Basemap.createImagery());
        Bookmark bookmark = new Bookmark("Guitar-shaped trees", new Viewpoint(-33.867886, -63.985, 4e4));
        map.getBookmarks().add(bookmark);
        mapView.setMap(map);

        // when the bookmarks view is added with the map view
        BookmarksList bookmarksView = new BookmarksList(mapView);
        bookmarksView.setMaxSize(100, 100);
        StackPane.setAlignment(bookmarksView, Pos.TOP_RIGHT);
        StackPane.setMargin(bookmarksView, new Insets(10));
        Platform.runLater(() -> stackPane.getChildren().add(bookmarksView));

        AtomicBoolean eventFired = new AtomicBoolean(false);
        mapView.addViewpointChangedListener(viewpointChangedEvent -> {
            eventFired.set(true);
        });

        // every bookmark's name will be displayed in the view
        clickOn(bookmark.getName());

        WaitForAsyncUtils.waitForFxEvents();

        Assertions.assertTrue(eventFired.get());

        sleep(2000);

        Assertions.assertEquals(bookmark.getViewpoint(), mapView.getCurrentViewpoint(Viewpoint.Type.BOUNDING_GEOMETRY));
    }
}
