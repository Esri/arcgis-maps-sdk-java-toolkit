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

  public void setView(MapView mapView) {
    view = mapView;
    if (view != null) {
      view.addMapRotationChangedListener(r -> headingProperty.set(view.getMapRotation()));
      setOnAction(e -> view.setViewpointRotationAsync(0.0));
    }
  }

  public SimpleDoubleProperty headingProperty() {
    return headingProperty;
  }

  public double getHeading() {
    return headingProperty.get();
  }

  public void setHeading(double heading) {
    headingProperty.set(heading);
    if (view != null) {
      view.setViewpointRotationAsync(heading);
    }
  }

  public SimpleBooleanProperty autoHideProperty() {
    return autoHideProperty;
  }

  public boolean isAutoHide() {
    return autoHideProperty.get();
  }

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
