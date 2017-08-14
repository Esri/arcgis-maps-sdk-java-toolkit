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

package com.esri.arcgisruntime.toolkit;

import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.mapping.view.ViewpointChangedListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * A compass control which shows the direction of north for the view. Clicking on the compass will return the view to
 * north. The compass can be set to fade out when its heading is north and fade in when the heading changes. The size of
 * the compass is the smaller of its width and height values.
 */
public final class Compass extends Control {

  private static final double SIZE = 100.0;

  private GeoView view;

  private final SimpleDoubleProperty headingProperty = new SimpleDoubleProperty(0.0);
  private final SimpleBooleanProperty autoHideProperty = new SimpleBooleanProperty(true);

  // property to hold the action to be executed when the compass is clicked
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

  // handler for clicking on the compass - resets to north
  private final EventHandler<ActionEvent> compassClickedAction = e -> {
    if (view != null) {
      if (view instanceof MapView) {
        ((MapView) view).setViewpointRotationAsync(0.0);
      } else if (view instanceof SceneView) {
        SceneView sceneView = (SceneView) view;
        Camera camera = sceneView.getCurrentViewpointCamera();
        camera = new Camera(camera.getLocation(), 0.0, camera.getPitch(), camera.getRoll());
        sceneView.setViewpointAsync(new Viewpoint(camera.getLocation(), 1, camera), 0.25f);
      }
    }
  };

  // handler for heading changes
  private final ViewpointChangedListener viewpointChangedListener = v -> {
    if (view != null) {
      if (view instanceof MapView) {
        headingProperty.set(((MapView) view).getMapRotation());
      } else if (view instanceof SceneView) {
        headingProperty.set(((SceneView) view).getCurrentViewpointCamera().getHeading());
      }
    }
  };

  /**
   * Creates an instance of a compass control. The compass control will show the direction of north when a non-null
   * {@link GeoView} has been set using {@link #setGeoView(GeoView)}.
   */
  public Compass() {
    this(null);
  }

  /**
   * Creates an instance of a compass control. The compass control will show the direction of north when a non-null
   * {@link GeoView} has been set.
   *
   * @param geoView the GeoView to link with this compass
   */
  public Compass(GeoView geoView) {
    setPrefHeight(SIZE);
    setPrefWidth(SIZE);
    setMaxHeight(USE_PREF_SIZE);
    setMaxWidth(USE_PREF_SIZE);
    setGeoView(geoView);
  }

  @Override
  protected Skin<?> createDefaultSkin() {
    return new com.esri.arcgisruntime.toolkit.skins.CompassSkin(this);
  }

  /**
   * Sets the {@link GeoView} which this compass is representing.
   *
   * @param geoView the GeoView
   */
  public void setGeoView(GeoView geoView) {
    if (view != null) {
      view.removeViewpointChangedListener(viewpointChangedListener);
    }
    view = geoView;
    if (view != null) {
      view.addViewpointChangedListener(viewpointChangedListener);
      setOnAction(compassClickedAction);
    } else {
      headingProperty.set(0.0);
      setOnAction(null);
    }
  }

  /**
   * A property containing the current compass heading in degrees.
   *
   * @return the compass heading property
   */
  public SimpleDoubleProperty headingProperty() {
    return headingProperty;
  }

  /**
   * Returns the compass heading in degrees.
   *
   * @return the compass heading
   */
  public double getHeading() {
    return headingProperty.get();
  }

  /**
   * Sets the compass heading in degrees. If {@link #setGeoView(GeoView)} has been called with a non-null argument then
   * that view will rotate to match the heading set.
   *
   * @param heading the compass heading
   */
  public void setHeading(double heading) {
    headingProperty.set(heading);
    if (view != null) {
      if (view instanceof MapView) {
        ((MapView) view).setViewpointRotationAsync(heading);
      } else if (view instanceof SceneView) {
        SceneView sceneView = (SceneView) view;
        Camera camera = sceneView.getCurrentViewpointCamera();
        camera = new Camera(camera.getLocation(), 0.0, camera.getPitch(), camera.getRoll());
        sceneView.setViewpointAsync(new Viewpoint(camera.getLocation(), 1, camera), 0.25f);
      }
    }
  }

  /**
   * A property controlling if the compass automatically hides when the view is oriented to north.
   *
   * @return the auto hide property
   */
  public SimpleBooleanProperty autoHideProperty() {
    return autoHideProperty;
  }

  /**
   * Returns true if the compass automatically hides when its heading is north.
   *
   * @return true if enabled, false otherwise
   */
  public boolean isAutoHide() {
    return autoHideProperty.get();
  }

  /**
   * Enables or disables automatically hiding the compass when its heading is north.
   *
   * @param autoHide true to enable, false to disable
   */
  public void setAutoHide(boolean autoHide) {
    autoHideProperty.set(autoHide);
  }

  /**
   * A property to hold the action to execute when the compass is clicked or tapped.
   *
   * @return the action property
   */
  private ObjectProperty<EventHandler<ActionEvent>> onActionProperty() {
    return onAction;
  }

  /**
   * Sets the action to execute when the compass is clicked or tapped.
   *
   * @param value the action
   */
  private void setOnAction(EventHandler<ActionEvent> value) {
    onActionProperty().set(value);
  }

  /**
   * Returns the action that is set to execute when the compass is clicked or tapped.
   *
   * @return the action
   */
  private EventHandler<ActionEvent> getOnAction() {
    return onActionProperty().get();
  }
}
