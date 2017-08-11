/*
 * Copyright 2017 Esri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package toolkit.skin;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.VLineTo;
import javafx.util.Duration;
import toolkit.Compass;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Implements a skin for the {@link Compass} control.
 */
public final class CompassSkin extends SkinBase<Compass> {

  private boolean invalid = true;
  private final StackPane compassStackPane = new StackPane();

  // duration used for fading the compass
  private static final long TIMER_DURATION = 500;

  // property that will be true when the compass is hidden
  private final SimpleBooleanProperty hiddenProperty = new SimpleBooleanProperty(true);

  /**
   * Creates an instance of the skin.
   *
   * @param control the {@link Compass} control this skin represents
   */
  public CompassSkin(Compass control) {
    super(control);

    control.widthProperty().addListener(observable -> invalid = true);
    control.heightProperty().addListener(observable -> invalid = true);

    // bind to the heading but also subtract rotation of the control to ensure north stays pointing up
    compassStackPane.rotateProperty().bind(control.headingProperty().negate().subtract(control.rotateProperty()));

    // hide the compass when the heading is close to north if the auto hide property is enabled
    hiddenProperty.bind(control.headingProperty().isEqualTo(0.0, 0.25).and(control.autoHideProperty()));
    hiddenProperty.addListener(observable -> {
      // use a timer to wait and see if the compass has stayed at north before fading it
      Timer timer = new Timer();
      timer.schedule(new TimerTask() {
        @Override
        public void run() {
          Platform.runLater(() -> {
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(TIMER_DURATION), compassStackPane);
            if (hiddenProperty.get()) {
              fadeTransition.setToValue(0.0);
            } else {
              fadeTransition.setToValue(1.0);
            }
            fadeTransition.play();
          });
        }
      }, TIMER_DURATION);
    });

    // initial opacity based on the auto-hide property
    compassStackPane.setOpacity(control.autoHideProperty().get() == true ? 0.0 : 1.0);

    getChildren().add(compassStackPane);
  }

  @Override
  protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
    if (invalid) {
      update(contentWidth, contentHeight);
      invalid = false;
    }
    getChildren().forEach(c -> layoutInArea(c, contentX, contentY, contentWidth, contentHeight, -1, HPos.CENTER, VPos.CENTER));
  }

  /**
   * Updates the visual representation of the compass e.g. when the size is changed.
   *
   * @param width the width of the control
   * @param height the height of the control
   */
  private void update(double width, double height) {
    compassStackPane.getChildren().clear();

    double radius = Math.min(height, width) / 2.0;
    double triangleHeight = radius * (3.0 / 5.0);
    double triangleWidth = triangleHeight / 3.0;

    PathElement northEastTrianglePath[] =
      {new MoveTo(0.0, 0.0), new VLineTo(triangleHeight), new HLineTo(triangleWidth), new ClosePath()};
    PathElement northWestTrianglePath[] =
      {new MoveTo(0.0, 0.0), new VLineTo(triangleHeight), new HLineTo(-triangleWidth), new ClosePath()};

    Path northEastTriangle = new Path(northEastTrianglePath);
    northEastTriangle.setStroke(null);
    northEastTriangle.setFill(Color.rgb(0x88, 0x00, 0x00));
    northEastTriangle.setTranslateY(-triangleHeight / 2.0);
    northEastTriangle.setTranslateX(triangleWidth / 2.0);

    Path southWestTriangle = new Path(northEastTrianglePath);
    southWestTriangle.setStroke(null);
    southWestTriangle.setFill(Color.rgb(0xA9, 0xA9, 0xA9));
    southWestTriangle.setTranslateY(triangleHeight / 2.0);
    southWestTriangle.setTranslateX(-triangleWidth / 2.0);
    southWestTriangle.setRotate(180.0);

    Path northWestTriangle = new Path(northWestTrianglePath);
    northWestTriangle.setStroke(null);
    northWestTriangle.setFill(Color.rgb(0xFF, 0x00, 0x00));
    northWestTriangle.setTranslateY(-triangleHeight / 2.0);
    northWestTriangle.setTranslateX(-triangleWidth / 2.0);

    Path southEastTriangle = new Path(northWestTrianglePath);
    southEastTriangle.setStroke(null);
    southEastTriangle.setFill(Color.rgb(0x80, 0x80, 0x80));
    southEastTriangle.setTranslateY(triangleHeight / 2.0);
    southEastTriangle.setTranslateX(triangleWidth / 2.0);
    southEastTriangle.setRotate(180.0);

    Circle pivot = new Circle();
    pivot.setRadius(triangleWidth / 3.0);
    pivot.setFill(Color.rgb(0xFF, 0xA5, 0x00));

    Circle circle = new Circle();
    circle.setRadius(radius);
    circle.setFill(Color.rgb(0xE1, 0xF1, 0xF5, 0.25));
    circle.setStroke(Color.rgb(0x80, 0x80, 0x80));
    circle.setStrokeWidth(0.1 * radius);

    compassStackPane.getChildren().addAll(circle, northEastTriangle, northWestTriangle,
      southEastTriangle, southWestTriangle, pivot);

    // fire action event if any of the compass elements are clicked
    compassStackPane.getChildren().forEach(c -> c.setOnMouseClicked(e -> getSkinnable().fireEvent(new ActionEvent())));
  }
}
