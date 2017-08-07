/*
 COPYRIGHT 1995-2017 ESRI

 TRADE SECRETS: ESRI PROPRIETARY AND CONFIDENTIAL
 Unpublished material - all rights reserved under the
 Copyright Laws of the United States.

 For additional information, contact:
 Environmental Systems Research Institute, Inc.
 Attn: Contracts Dept
 380 New York Street
 Redlands, California, USA 92373

 email: contracts@esri.com
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
