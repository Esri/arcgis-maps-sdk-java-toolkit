package com.esri.arcgisruntime.toolkit;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.testfx.framework.junit.ApplicationTest;

public class TableOfContentsTest extends ApplicationTest  {

  /**
   * Tests that a null GeoView argument throws NullPointerException.
   */
  @Test
  public void constructorGeoViewNotNull() {
    Assertions.assertThrows(NullPointerException.class, () -> new TableOfContents(null));
  }
}
