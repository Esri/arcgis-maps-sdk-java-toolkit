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
import org.junit.After;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TemplatePickerTest extends ApplicationTest {

    final String WILDFIRE_RESPONSE_URL = "https://sampleserver6.arcgisonline" +
        ".com/arcgis/rest/services/Wildfire/FeatureServer/0";

    private StackPane stackPane;

    @Override
    public void start(Stage primaryStage) {
        stackPane = new StackPane();

        Scene scene = new Scene(stackPane);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.toFront();
    }

    @Test
    public void templatePicker() {
        ArcGISFeatureTable featureTable = new ServiceFeatureTable(WILDFIRE_RESPONSE_URL);
        FeatureLayer featureLayer = new FeatureLayer(featureTable);

        Platform.runLater(() -> {
            FeatureTemplatePicker featureTemplatePicker = new FeatureTemplatePicker(featureLayer);
            stackPane.getChildren().add(featureTemplatePicker);
        });

        sleep(10000);

        FeatureTemplatePicker featureTemplatePicker = (FeatureTemplatePicker) stackPane.getChildren().get(0);
        featureTemplatePicker.getFeatureTemplateGroups().forEach(featureTemplateGroup ->
            featureTemplateGroup.getFeatureTemplateItems().forEach(featureTemplateItem ->
                clickOn(featureTemplateItem.getFeatureTemplate().getName())
            )
        );
        clickOn(featureLayer.getName());
    }

    @After
    public void cleanup() throws Exception {
        FxToolkit.cleanupStages();
    }

    @Test
    public void scrollable() {
        ArcGISFeatureTable featureTable = new ServiceFeatureTable(WILDFIRE_RESPONSE_URL);
        FeatureLayer featureLayer = new FeatureLayer(featureTable);

        Platform.runLater(() -> {
            FeatureTemplatePicker featureTemplatePicker = new FeatureTemplatePicker(featureLayer, featureLayer);
            featureTemplatePicker.setMaxSize(300, 300);
            stackPane.getChildren().add(featureTemplatePicker);
        });

        sleep(10000);

        Object[] scrollBars = lookup(n -> n instanceof ScrollBar).queryAll().toArray();
        assertEquals(2, scrollBars.length);
        assertTrue(((ScrollBar) scrollBars[0]).isVisible());
        assertFalse(((ScrollBar) scrollBars[1]).isVisible());

        FeatureTemplatePicker featureTemplatePicker = (FeatureTemplatePicker) stackPane.getChildren().get(0);
        featureTemplatePicker.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        WaitForAsyncUtils.waitForFxEvents();
        sleep(4000);

        assertFalse(((ScrollBar) scrollBars[0]).isVisible());
        assertFalse(((ScrollBar) scrollBars[1]).isVisible());
    }

    @Test
    public void orientation() {
        ArcGISFeatureTable featureTable = new ServiceFeatureTable(WILDFIRE_RESPONSE_URL);
        FeatureLayer featureLayer = new FeatureLayer(featureTable);

        Platform.runLater(() -> {
            FeatureTemplatePicker featureTemplatePicker = new FeatureTemplatePicker(featureLayer, featureLayer);
            featureTemplatePicker.setMaxSize(300, 300);
            stackPane.getChildren().add(featureTemplatePicker);
        });

        sleep(10000);

        Object[] scrollBars = lookup(n -> n instanceof ScrollBar).queryAll().toArray();
        assertEquals(2, scrollBars.length);

        assertTrue(((ScrollBar) scrollBars[0]).isVisible());
        assertFalse(((ScrollBar) scrollBars[1]).isVisible());

        FeatureTemplatePicker featureTemplatePicker = (FeatureTemplatePicker) stackPane.getChildren().get(0);
        featureTemplatePicker.setOrientation(Orientation.HORIZONTAL);

        WaitForAsyncUtils.waitForFxEvents();

        assertFalse(((ScrollBar) scrollBars[0]).isVisible());
        assertTrue(((ScrollBar) scrollBars[1]).isVisible());

        featureTemplatePicker.setOrientation(Orientation.VERTICAL);

        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(((ScrollBar) scrollBars[0]).isVisible());
        assertFalse(((ScrollBar) scrollBars[1]).isVisible());
    }

    @Test
    public void fromMap() {
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

        sleep(5000);

        FeatureTemplatePicker featureTemplatePicker = (FeatureTemplatePicker) stackPane.getChildren().get(0);
        assertEquals(4, featureTemplatePicker.getFeatureTemplateGroups().size());

        map.getOperationalLayers().forEach(layer -> clickOn(layer.getName()));
    }

    /**
     * Tests wiring between items' toggle group and the selected feature template item property.
     */
    @Test
    public void focusAndSelection() {
        ArcGISFeatureTable featureTable = new ServiceFeatureTable(WILDFIRE_RESPONSE_URL);
        FeatureLayer featureLayer = new FeatureLayer(featureTable);

        Platform.runLater(() -> {
            FeatureTemplatePicker featureTemplatePicker = new FeatureTemplatePicker(featureLayer);
            stackPane.getChildren().add(featureTemplatePicker);
        });

        sleep(3000);

        FeatureTemplatePicker featureTemplatePicker = (FeatureTemplatePicker) stackPane.getChildren().get(0);

        // given a template which is selected programmatically
        Object[] toggleButtons = lookup(n -> n instanceof ToggleButton).queryAll().toArray();
        assertTrue(toggleButtons.length > 1);
        ToggleButton firstButton = (ToggleButton) toggleButtons[0];
        ToggleButton secondButton = (ToggleButton) toggleButtons[1];

        // when the selected template is clicked
        clickOn(firstButton);
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(firstButton.getUserData(), featureTemplatePicker.getSelectedFeatureTemplateItem());

        clickOn(firstButton);
        WaitForAsyncUtils.waitForFxEvents();
        assertNull(featureTemplatePicker.getSelectedFeatureTemplateItem());

        clickOn(firstButton);
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(firstButton.getUserData(), featureTemplatePicker.getSelectedFeatureTemplateItem());

        clickOn(secondButton);
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
    public void symbolSize() {
        ArcGISFeatureTable featureTable = new ServiceFeatureTable(WILDFIRE_RESPONSE_URL);
        FeatureLayer featureLayer = new FeatureLayer(featureTable);

        Platform.runLater(() -> {
            FeatureTemplatePicker featureTemplatePicker = new FeatureTemplatePicker(featureLayer);
            stackPane.getChildren().add(featureTemplatePicker);
        });

        sleep(3000);

        Set<ImageView> imageViews = lookup(n -> n instanceof ImageView).queryAll();

        FeatureTemplatePicker featureTemplatePicker = (FeatureTemplatePicker) stackPane.getChildren().get(0);
        int prevSize = featureTemplatePicker.getSymbolSize();
        int newSize = 100;
        featureTemplatePicker.setSymbolSize(newSize);

        sleep(1000);

        for (ImageView imageView : imageViews) {
            assertEquals(newSize, imageView.getImage().getWidth());
            assertEquals(newSize, imageView.getImage().getHeight());
        }

        featureTemplatePicker.setSymbolSize(prevSize);

        sleep(1000);

        for (ImageView imageView : imageViews) {
            assertEquals(prevSize, imageView.getImage().getWidth());
            assertEquals(prevSize, imageView.getImage().getHeight());
        }
    }
}
