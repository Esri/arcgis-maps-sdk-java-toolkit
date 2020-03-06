package com.esri.arcgisruntime.toolkit;

import com.esri.arcgisruntime.data.ArcGISFeatureTable;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.view.MapView;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Automated integration tests for feature template picker.
 */
@ExtendWith(ApplicationExtension.class)
public class FeatureTemplatePickerTest {

    private static final String WILDFIRE_RESPONSE_URL = "https://sampleserver6.arcgisonline" +
        ".com/arcgis/rest/services/Wildfire/FeatureServer/0";

    private StackPane stackPane;
    private final FeatureLayer featureLayer = new FeatureLayer(new ServiceFeatureTable(WILDFIRE_RESPONSE_URL));

    @Start
    private void start(Stage primaryStage) {
        stackPane = new StackPane();

        Scene scene = new Scene(stackPane);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.toFront();
    }

    /**
     * Clean up stage after each test.
     *
     * @throws Exception exception
     */
    @AfterEach
    private void cleanup() throws Exception {
        FxToolkit.cleanupStages();
    }

    /**
     * Tests that the picker shows the names of feature templates in its layers.
     *
     * @param robot robot injected by test extension
     */
    @Test
    void templateNamesVisible(FxRobot robot) {
        // given a feature template picker using a feature layer with 16 feature templates
        Platform.runLater(() -> {
            FeatureTemplatePicker featureTemplatePicker = new FeatureTemplatePicker(featureLayer);
            stackPane.getChildren().add(featureTemplatePicker);
        });

        // when the feature template picker is done rendering
        robot.sleep(3000);
        FeatureTemplatePicker featureTemplatePicker = (FeatureTemplatePicker) stackPane.getChildren().get(0);

        // the feature template picker should have feature template items
        int templates = featureTemplatePicker.getFeatureTemplateGroups().stream()
            .map(FeatureTemplateGroup::getFeatureTemplateItems)
            .mapToInt(Collection::size)
            .sum();
        assertTrue(templates > 0);

        // all feature template names should be visible
        featureTemplatePicker.getFeatureTemplateGroups().forEach(featureTemplateGroup ->
            featureTemplateGroup.getFeatureTemplateItems().forEach(featureTemplateItem -> {
                robot.clickOn(featureTemplateItem.getFeatureTemplate().getName());
            })
        );
    }

    /**
     * Tests that scrollbars are shown when the picker's max size is smaller than the size of its contents.
     *
     * @param robot robot injected by test extension
     */
    @Test
    void scrollable(FxRobot robot) {
        ArcGISFeatureTable featureTable = new ServiceFeatureTable(WILDFIRE_RESPONSE_URL);
        FeatureLayer featureLayer = new FeatureLayer(featureTable);

        Platform.runLater(() -> {
            FeatureTemplatePicker featureTemplatePicker = new FeatureTemplatePicker(featureLayer, featureLayer);
            featureTemplatePicker.setMaxSize(300, 300);
            stackPane.getChildren().add(featureTemplatePicker);
        });

        robot.sleep(3000);

        Object[] scrollBars = robot.lookup(n -> n instanceof ScrollBar).queryAll().toArray();
        assertEquals(2, scrollBars.length);
        assertTrue(((ScrollBar) scrollBars[0]).isVisible());
        assertFalse(((ScrollBar) scrollBars[1]).isVisible());

        FeatureTemplatePicker featureTemplatePicker = (FeatureTemplatePicker) stackPane.getChildren().get(0);
        Platform.runLater(() -> featureTemplatePicker.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE));

        WaitForAsyncUtils.waitForFxEvents();

        assertFalse(((ScrollBar) scrollBars[0]).isVisible());
        assertFalse(((ScrollBar) scrollBars[1]).isVisible());
    }

    /**
     * Tests that the appropriate scrollbars are shown based on the picker's orientation.
     *
     * @param robot robot injected by test extension
     */
    @Test
    void orientation(FxRobot robot) {
        ArcGISFeatureTable featureTable = new ServiceFeatureTable(WILDFIRE_RESPONSE_URL);
        FeatureLayer featureLayer = new FeatureLayer(featureTable);

        Platform.runLater(() -> {
            FeatureTemplatePicker featureTemplatePicker = new FeatureTemplatePicker(featureLayer, featureLayer);
            featureTemplatePicker.setMaxSize(300, 300);
            stackPane.getChildren().add(featureTemplatePicker);
        });

        robot.sleep(3000);

        Object[] scrollBars = robot.lookup(n -> n instanceof ScrollBar).queryAll().toArray();
        assertEquals(2, scrollBars.length);

        assertTrue(((ScrollBar) scrollBars[0]).isVisible());
        assertFalse(((ScrollBar) scrollBars[1]).isVisible());

        FeatureTemplatePicker featureTemplatePicker = (FeatureTemplatePicker) stackPane.getChildren().get(0);
        Platform.runLater(() -> featureTemplatePicker.setOrientation(Orientation.HORIZONTAL));

        WaitForAsyncUtils.waitForFxEvents();

        assertFalse(((ScrollBar) scrollBars[0]).isVisible());
        assertTrue(((ScrollBar) scrollBars[1]).isVisible());

        Platform.runLater(() -> featureTemplatePicker.setOrientation(Orientation.VERTICAL));

        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(((ScrollBar) scrollBars[0]).isVisible());
        assertFalse(((ScrollBar) scrollBars[1]).isVisible());
    }

    /**
     * Tests that the picker shows the names of its layer. Also tests the workflow of adding layers from a web map.
     *
     * @param robot robot injected by test extension
     */
    @Test
    void layerNamesVisible(FxRobot robot) {
        MapView mapView = new MapView();
        ArcGISMap map = new ArcGISMap("https://runtime.maps.arcgis.com/home/webmap/viewer.html?webmap=05792de90e1d4eff81fdbde8c5eb4063");
        mapView.setMap(map);

        Platform.runLater(() -> {
            FeatureTemplatePicker featureTemplatePicker = new FeatureTemplatePicker();
            featureTemplatePicker.setMaxWidth(500);
            stackPane.getChildren().add(featureTemplatePicker);

            map.addDoneLoadingListener(() -> {
                if (map.getLoadStatus() == LoadStatus.LOADED) {
                    map.getOperationalLayers().forEach(layer -> {
                        if (layer instanceof FeatureLayer) {
                            featureTemplatePicker.getFeatureLayers().add((FeatureLayer) layer);
                        }
                    });
                }
            });
        });

        robot.sleep(5000);

        FeatureTemplatePicker featureTemplatePicker = (FeatureTemplatePicker) stackPane.getChildren().get(0);
        assertEquals(4, featureTemplatePicker.getFeatureTemplateGroups().size());

        map.getOperationalLayers().forEach(layer -> robot.clickOn(layer.getName()));
    }

    /**
     * Tests wiring between items' toggle group and the selected feature template item property.
     */
    @Test
    void focusAndSelection(FxRobot robot) {
        ArcGISFeatureTable featureTable = new ServiceFeatureTable(WILDFIRE_RESPONSE_URL);
        FeatureLayer featureLayer = new FeatureLayer(featureTable);

        Platform.runLater(() -> {
            FeatureTemplatePicker featureTemplatePicker = new FeatureTemplatePicker(featureLayer);
            stackPane.getChildren().add(featureTemplatePicker);
        });

        robot.sleep(3000);

        FeatureTemplatePicker featureTemplatePicker = (FeatureTemplatePicker) stackPane.getChildren().get(0);

        // given a template which is selected programmatically
        Object[] toggleButtons = robot.lookup(n -> n instanceof ToggleButton).queryAll().toArray();
        assertTrue(toggleButtons.length > 1);
        ToggleButton firstButton = (ToggleButton) toggleButtons[0];
        ToggleButton secondButton = (ToggleButton) toggleButtons[1];

        // when the selected template is clicked
        robot.clickOn(firstButton);
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(firstButton.getUserData(), featureTemplatePicker.getSelectedFeatureTemplateItem());

        robot.clickOn(firstButton);
        WaitForAsyncUtils.waitForFxEvents();
        assertNull(featureTemplatePicker.getSelectedFeatureTemplateItem());

        robot.clickOn(firstButton);
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(firstButton.getUserData(), featureTemplatePicker.getSelectedFeatureTemplateItem());

        robot.clickOn(secondButton);
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(secondButton.getUserData(), featureTemplatePicker.getSelectedFeatureTemplateItem());

        secondButton.setSelected(false);
        WaitForAsyncUtils.waitForFxEvents();
        assertNull(featureTemplatePicker.getSelectedFeatureTemplateItem());

        featureTemplatePicker.setSelectedFeatureTemplateItem((FeatureTemplateItem) firstButton.getUserData());
        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(firstButton.isSelected());

        featureTemplatePicker.setSelectedFeatureTemplateItem(null);
        WaitForAsyncUtils.waitForFxEvents();
        assertNull(firstButton.getToggleGroup().getSelectedToggle());

        // focus
        Platform.runLater(firstButton::requestFocus);
        WaitForAsyncUtils.waitForFxEvents();
        assertNull(featureTemplatePicker.getSelectedFeatureTemplateItem());

        Platform.runLater(secondButton::requestFocus);
        WaitForAsyncUtils.waitForFxEvents();
        assertNull(featureTemplatePicker.getSelectedFeatureTemplateItem());
    }

    /**
     * Tests that the template swatch sizes update when the symbolHeight and symbolWidth properties are changed.
     */
    @Test
    void symbolSize(FxRobot robot) {
        ArcGISFeatureTable featureTable = new ServiceFeatureTable(WILDFIRE_RESPONSE_URL);
        FeatureLayer featureLayer = new FeatureLayer(featureTable);

        Platform.runLater(() -> {
            FeatureTemplatePicker featureTemplatePicker = new FeatureTemplatePicker(featureLayer);
            stackPane.getChildren().add(featureTemplatePicker);
        });

        robot.sleep(3000);

        Set<ImageView> imageViews = robot.lookup(n -> n instanceof ImageView).queryAll();

        FeatureTemplatePicker featureTemplatePicker = (FeatureTemplatePicker) stackPane.getChildren().get(0);
        int prevSize = featureTemplatePicker.getSymbolSize();
        int newSize = 100;
        featureTemplatePicker.setSymbolSize(newSize);

        robot.sleep(1000);

        for (ImageView imageView : imageViews) {
            assertEquals(newSize, imageView.getImage().getWidth());
            assertEquals(newSize, imageView.getImage().getHeight());
        }

        featureTemplatePicker.setSymbolSize(prevSize);

        robot.sleep(1000);

        for (ImageView imageView : imageViews) {
            assertEquals(prevSize, imageView.getImage().getWidth());
            assertEquals(prevSize, imageView.getImage().getHeight());
        }
    }
}
