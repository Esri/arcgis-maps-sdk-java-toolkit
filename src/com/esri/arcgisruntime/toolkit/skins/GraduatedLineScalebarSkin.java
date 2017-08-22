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

package com.esri.arcgisruntime.toolkit.skins;

import com.esri.arcgisruntime.toolkit.Scalebar;

public final class GraduatedLineScalebarSkin extends ScalebarSkin {

  public GraduatedLineScalebarSkin(Scalebar scalebar) {
    super(scalebar);
  }

  @Override
  protected void update(double width, double height) {
  }

  @Override
  protected void recalculate() {

  }

  @Override
  protected double calculateMaximumScalebarWidth() {
    return 0;
  }
}
