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

package toolkit;

import com.esri.arcgisruntime.mapping.view.MapView;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import toolkit.skin.CompassSkin;

/**
 * A compass control which shows the direction of north for the view. Clicking on the compass will return the view to
 * north. The compass can be set to make itself invisible when its heading is north. The size of the compass is the
 * smaller of its width and height values.
 */
public final class Compass extends Control {

  private static final double SIZE = 100.0;

  private MapView view;

  private final SimpleDoubleProperty headingProperty = new SimpleDoubleProperty(0.0);
  private final SimpleBooleanProperty autoHideProperty = new SimpleBooleanProperty(true);

  private final ObjectProperty<EventHandler<ActionEvent>> onAction = new ObjectPropertyBase<EventHandler<ActionEvent>>() {
    @Override
    protected void invalidated() {
      setEventHandler(ActionEvent.ACTION, get());
    }

    @Override
    public Object getBean() {
      return Compass.this;
    }

    @Override
    public String getName() {
      return "onAction";
    }
  };

  /**
   * Creates an instance of a compass control. The compass control will show the direction of north when a view has been
   * see using {@link #setView(MapView)}.
   */
  public Compass() {
    setPrefHeight(SIZE);
    setPrefWidth(SIZE);
    setMaxHeight(USE_PREF_SIZE);
    setMaxWidth(USE_PREF_SIZE);
  }

  @Override
  protected Skin<?> createDefaultSkin() {
    return new CompassSkin(this);
  }

  /**
   * Sets the {@link MapView} which this compass is representing.
   * @param mapView the map view
   */
  public void setView(MapView mapView) {
    view = mapView;
    if (view != null) {
      view.addMapRotationChangedListener(r -> headingProperty.set(view.getMapRotation()));
      setOnAction(e -> view.setViewpointRotationAsync(0.0));
    }
  }

  /**
   * A property containing the current compass heading in degrees.
   * @return the compass heading property
   */
  public SimpleDoubleProperty headingProperty() {
    return headingProperty;
  }

  /**
   * Returns the compass heading in degrees.
   * @return the compass heading
   */
  public double getHeading() {
    return headingProperty.get();
  }

  /**
   * Sets the compass heading in degrees. If {@link #setView(MapView)} has been called with a non-null argument then
   * that view will rotate to match the heading set.
   * @param heading the compass heading
   */
  public void setHeading(double heading) {
    headingProperty.set(heading);
    if (view != null) {
      view.setViewpointRotationAsync(heading);
    }
  }

  /**
   * A property controlling if the compass automatically hides when the view is oriented to north.
   * @return the auto hide property
   */
  public SimpleBooleanProperty autoHideProperty() {
    return autoHideProperty;
  }

  /**
   * Returns true if the compass automatically hides when its heading is north.
   * @return true if enabled, false otherwise
   */
  public boolean isAutoHide() {
    return autoHideProperty.get();
  }

  /**
   * Enables or disables automatically hiding the compass when its heading is north.
   * @param autoHide true to enable, false to disable
   */
  public void setAutoHide(boolean autoHide) {
    autoHideProperty.set(autoHide);
  }

  private ObjectProperty<EventHandler<ActionEvent>> onActionProperty() {
    return onAction;
  }

  private void setOnAction(EventHandler<ActionEvent> value) {
    onActionProperty().set(value);
  }

  private EventHandler<ActionEvent> getOnAction() {
    return onActionProperty().get();
  }
}
