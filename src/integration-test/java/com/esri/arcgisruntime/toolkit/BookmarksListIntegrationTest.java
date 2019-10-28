package com.esri.arcgisruntime.toolkit;

import com.esri.arcgisruntime.geometry.CoordinateFormatter;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.*;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SceneView;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.VerticalDirection;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.junit.After;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.testfx.api.FxRobotException;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.concurrent.atomic.AtomicBoolean;

public class BookmarksListIntegrationTest extends ApplicationTest {

    private StackPane stackPane;

    @Override
    public void start(Stage primaryStage) {
        stackPane = new StackPane();
        Scene fxScene = new Scene(stackPane);
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

        sleep(3000);

        // every bookmark's name will be displayed in the view
        map.getBookmarks().forEach(bookmark -> clickOn(bookmark.getName()));
    }

    /**
     * Tests that every bookmark in a scene has its name displayed in the view.
     */
    @Test
    public void item_for_every_bookmark_in_scene() {
        // given a scene view containing a scene with bookmarks
        SceneView sceneView = new SceneView();
        Platform.runLater(() -> stackPane.getChildren().add(sceneView));

        ArcGISScene scene = new ArcGISScene(Basemap.createImagery());
        scene.getBookmarks().add(new Bookmark("Guitar-shaped trees", new Viewpoint(-33.867886, -63.985, 4e4)));
        scene.getBookmarks().add(new Bookmark("Grand Prismatic Spring", new Viewpoint(44.525049, -110.83819, 6e3)));
        sceneView.setArcGISScene(scene);

        // when the bookmarks view is added with the scene view
        BookmarksList bookmarksList = new BookmarksList(sceneView);
        bookmarksList.setMaxSize(100, 100);
        StackPane.setAlignment(bookmarksList, Pos.TOP_RIGHT);
        StackPane.setMargin(bookmarksList, new Insets(10));
        Platform.runLater(() -> stackPane.getChildren().add(bookmarksList));

        sleep(3000);

        // every bookmark's name will be displayed in the view
        scene.getBookmarks().forEach(bookmark -> clickOn(bookmark.getName()));
    }

    /**
     * Tests that adding and removing bookmarks from the map updates the list.
     */
    @Test
    public void adding_and_removing_bookmarks() {
        // given a map view containing a map with bookmarks and a bookmarks list
        MapView mapView = new MapView();
        Platform.runLater(() -> stackPane.getChildren().add(mapView));

        ArcGISMap map = new ArcGISMap(Basemap.createImagery());
        Bookmark bookmarkToKeep = new Bookmark("Guitar-shaped trees", new Viewpoint(-33.867886, -63.985, 4e4));
        Bookmark bookmarkToRemove = new Bookmark("Grand Prismatic Spring", new Viewpoint(44.525049, -110.83819, 6e3));
        map.getBookmarks().add(bookmarkToKeep);
        map.getBookmarks().add(bookmarkToRemove);
        mapView.setMap(map);

        BookmarksList bookmarksList = new BookmarksList(mapView);
        bookmarksList.setMaxSize(100, 100);
        StackPane.setAlignment(bookmarksList, Pos.TOP_RIGHT);
        StackPane.setMargin(bookmarksList, new Insets(10));
        Platform.runLater(() -> stackPane.getChildren().add(bookmarksList));

        sleep(3000);

        // after adding and removing some bookmarks
        Bookmark bookmarkToAdd = new Bookmark("Strange Symbol", new Viewpoint(37.401573, -116.867808, 6e3));
        map.getBookmarks().remove(bookmarkToRemove);
        map.getBookmarks().add(bookmarkToAdd);

        WaitForAsyncUtils.waitForFxEvents();

        // the bookmark to keep and the added one will be listed and the removed one will not
        clickOn(bookmarkToKeep.getName());
        clickOn(bookmarkToAdd.getName());
        Assertions.assertThrows(FxRobotException.class, () -> clickOn(bookmarkToRemove.getName()));
    }

    /**
     * Tests that clicking a bookmark updates the geo view's viewpoint.
     */
    @Test
    public void clicking_item_switches_viewpoint() {
        // given a bookmarks list with a bookmark
        MapView mapView = new MapView();
        Platform.runLater(() -> stackPane.getChildren().add(mapView));

        ArcGISMap map = new ArcGISMap(Basemap.createImagery());
        Bookmark bookmark = new Bookmark("Guitar-shaped trees", new Viewpoint(-33.867886, -63.985, 4e4));
        map.getBookmarks().add(bookmark);
        mapView.setMap(map);

        BookmarksList bookmarksView = new BookmarksList(mapView);
        bookmarksView.setMaxSize(100, 100);
        StackPane.setAlignment(bookmarksView, Pos.TOP_RIGHT);
        StackPane.setMargin(bookmarksView, new Insets(10));
        Platform.runLater(() -> stackPane.getChildren().add(bookmarksView));

        sleep(1000);

        AtomicBoolean eventFired = new AtomicBoolean(false);
        mapView.addViewpointChangedListener(viewpointChangedEvent -> {
            eventFired.set(true);
        });

        // when the bookmark is selected
        clickOn(bookmark.getName());

        sleep(2000);

        // the geo view's viewpoint will be changed and the new viewpoint will equal that of the bookmark
        Assertions.assertTrue(eventFired.get());
        Assertions.assertTrue(bookmark.getViewpoint().getTargetGeometry().equals(
                GeometryEngine.project(mapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE).getTargetGeometry(),
                        bookmark.getViewpoint().getTargetGeometry().getSpatialReference()), 0.01));
    }

    /**
     * Tests that one can return to the previously selected bookmark viewpoint after panning/zooming.
     */
    @Test
    public void can_return_to_bookmark_after_move() {
        // given a bookmarks list with a bookmark
        MapView mapView = new MapView();
        Platform.runLater(() -> stackPane.getChildren().add(mapView));

        ArcGISMap map = new ArcGISMap(Basemap.createImagery());
        Bookmark bookmark = new Bookmark("Guitar-shaped trees", new Viewpoint(-33.867886, -63.985, 4e4));
        map.getBookmarks().add(bookmark);
        mapView.setMap(map);

        BookmarksList bookmarksView = new BookmarksList(mapView);
        bookmarksView.setMaxSize(100, 100);
        StackPane.setAlignment(bookmarksView, Pos.TOP_RIGHT);
        StackPane.setMargin(bookmarksView, new Insets(10));
        Platform.runLater(() -> stackPane.getChildren().add(bookmarksView));

        sleep(3000);

        // when the bookmark is selected, moved away from, and selected again
        clickOn(bookmark.getName())
                .moveTo(mapView)
                .scroll(2, VerticalDirection.DOWN)
                .drag(mapView, MouseButton.PRIMARY)
                .moveTo(new Point2D(0, 0))
                .release(MouseButton.PRIMARY)
                .clickOn(bookmark.getName());

        sleep(4000);

        // the geo view's viewpoint will be the same as the bookmark's viewpoint
        Assertions.assertTrue(bookmark.getViewpoint().getTargetGeometry().equals(
                GeometryEngine.project(mapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE).getTargetGeometry(),
                        bookmark.getViewpoint().getTargetGeometry().getSpatialReference()), 0.01));
    }

    /**
     * Tests that a user can override the default cell factory to show an icon and custom text.
     */
    @Test
    public void custom_cell_factory() {
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

        bookmarksList.setCellFactory(new Callback<>() {
            private Image bookmarkIcon = new Image(getClass().getResourceAsStream("/bookmark-outline.png"), 12, 12, true, true);

            @Override
            public ListCell<Bookmark> call(ListView<Bookmark> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Bookmark item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty ? null : formatBookmarkNameWithViewpointCoordinate(item));
                        setGraphic(empty ? null : new ImageView(bookmarkIcon));
                    }
                };
            }
        });

        sleep(3000);

        // every bookmark's name will be displayed in the view with an image and custom name
        Assertions.assertEquals(4, lookup(n -> n instanceof ImageView).queryAll().size(), "Two image views from " +
                "mapView and 2 from the bookmarks list");
        map.getBookmarks().forEach(bookmark -> clickOn(formatBookmarkNameWithViewpointCoordinate(bookmark)));
    }

    /**
     * Creates a string to display the bookmark's name and viewpoint coordinate.
     *
     * @param bookmark bookmark to display
     * @return string representation of bookmark, e.g. "Guitar-shaped trees (33.87S 063.98W)"
     */
    private String formatBookmarkNameWithViewpointCoordinate(Bookmark bookmark) {
        Point point = (Point) bookmark.getViewpoint().getTargetGeometry();
        String coordinateString = CoordinateFormatter.toLatitudeLongitude(point,
                CoordinateFormatter.LatitudeLongitudeFormat.DECIMAL_DEGREES, 2);
        return bookmark.getName() + " (" + coordinateString + ")";
    }
}
