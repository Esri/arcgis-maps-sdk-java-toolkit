package com.esri.arcgisruntime.toolkit;

import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Test;
import org.testfx.api.FxRobotException;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TemplatePickerTest extends ApplicationTest {

    private TemplatePicker templatePicker;
    private FeatureLayer featureLayer;
    private Scene scene;

    @Override
    public void start(Stage primaryStage) {
        final String WILDFIRE_RESPONSE_URL = "https://sampleserver6.arcgisonline" +
                ".com/arcgis/rest/services/Wildfire/FeatureServer/0";
        FeatureTable featureTable = new ServiceFeatureTable(WILDFIRE_RESPONSE_URL);
        featureLayer = new FeatureLayer(featureTable);

        ArcGISMap map = new ArcGISMap(Basemap.createImagery());
        map.getOperationalLayers().add(featureLayer);
        MapView mapView = new MapView();
        mapView.setPrefSize(500, 500);
        mapView.setMap(map);

        templatePicker = new TemplatePicker();
        templatePicker.featureLayerListProperty().add(featureLayer);

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(mapView);
        borderPane.setLeft(templatePicker);

        scene = new Scene(borderPane);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.toFront();
    }

    @After
    public void cleanup() throws Exception {
        FxToolkit.cleanupStages();
    }

    /**
     * Tests that the title rendered for a template is updated when the layer's name changes.
     */
    @Test
    public void testTemplateTitleUpdatesWhenLayerNameChanges() {
        sleep(5000);

        // given a template picker rendering a feature layer's name as the template's title
        clickOn(featureLayer.getName()); // should not fail since name will be displayed and can be clicked

        // when the feature layer title is changed
        featureLayer.setName("Test");

        // then the title in the template picker will be updated
        clickOn("Test");
    }

    /**
     * Tests that clicking on a selected cell does not trigger a selection changed event.
     */
    @Test
    public void testSubsequentClicksDoNotTriggerSelectionChanges() {
        sleep(3000);

        // given a template which is selected
        Set<TilePane> tilePanes = lookup(n -> n instanceof TilePane).queryAll();
        assertEquals(1, tilePanes.size());
        TilePane tilePane = tilePanes.iterator().next();
        clickOn(tilePane.getChildren().get(0));

        // when the selected template is clicked again
        templatePicker.selectedTemplateProperty().addListener(o -> fail("Should not be triggered"));
        clickOn(tilePane.getChildren().get(0));

        // then the selection property changed listener should not be fired again
        sleep(1000);
    }

    /**
     * Tests that clicking on a cell selects it.
     */
    @Test
    public void testClickingOnCellSelectsIt() {
        sleep(3000);

        // given a template which is selected programmatically
        Set<TilePane> tilePanes = lookup(n -> n instanceof TilePane).queryAll();
        assertEquals(1, tilePanes.size());
        TilePane tilePane = tilePanes.iterator().next();

        // when the selected template is clicked
        clickOn(tilePane.getChildren().get(0));

        // then the selection property changed listener should not be fired again
        sleep(1000);

        assertNotNull(templatePicker.selectedTemplateProperty().get());
        assertEquals(featureLayer.getName(), templatePicker.selectedTemplateProperty().get().getFeatureLayer().getName());
    }

    /**
     * Tests that separators between templates can be shown and hidden.
     */
    @Test
    public void testSeparatorsVisibility() {
        sleep(2000);

        // given a template picker with separators showing
        templatePicker.showSeparatorsProperty().set(true);
        clickOn(n -> n instanceof Separator); // should not fail

        // when showing separators is set to false
        templatePicker.showSeparatorsProperty().set(false);

        sleep(1000);

        // then no separators will be shown
        assertThrows(FxRobotException.class, () -> clickOn(n -> n instanceof Separator), "No separators should be " +
                "clickable.");
    }

    /**
     * Tests that template names can be hidden.
     */
    @Test
    public void testTemplateNamesVisibility() {
        sleep(3000);

        // given a template picker with template names showing
        clickOn(featureLayer.getName()); // should not fail

        // when showing template names is set to false
        Platform.runLater(() -> templatePicker.showTemplateNamesProperty().set(false));

        WaitForAsyncUtils.waitForFxEvents();

        // then no name is shown
        assertThrows(FxRobotException.class, () -> clickOn(featureLayer.getName()), "Name should not be clickable.");
    }

    /**
     * Tests that the template swatch sizes update when the symbolHeight and symbolWidth properties are changed.
     */
    @Test
    public void testSymbolSize() {
        sleep(3000);

        int height = 10;
        int width = 500;

        Platform.runLater(() -> {
            templatePicker.symbolHeightProperty().set(height);
            templatePicker.symbolWidthProperty().set(width);
        });

        sleep(1000);

        Set<ImageView> imageViews = lookup(n -> n instanceof ImageView).queryAll();
        imageViews.forEach(imageView -> {
            assertEquals(height, imageView.getImage().getHeight());
            assertEquals(width, imageView.getImage().getWidth());
        });
    }

    @Test
    public void testOverrideCss() {
        scene.getStylesheets().add(getClass().getResource("/test.css").toExternalForm());
        sleep(3000);

        Set<TilePane> tilePanes = lookup(n -> n instanceof TilePane).queryAll();
        clickOn(tilePanes.iterator().next().getChildren().get(0));

        sleep(5000);
    }
}
