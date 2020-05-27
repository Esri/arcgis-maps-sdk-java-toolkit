package com.esri.arcgisruntime.toolkit.util;

/**
 * Utilities for JavaFX platform.
 */
public class PlatformUtils {
  private static boolean platformStarted = false;

  /**
   * Checks if the platform started flag was set.
   * @return if the platform started flag was set.
   */
  public static boolean isPlatformStarted() {
    return platformStarted;
  }

  /**
   * Sets the platform started flag to true.
   */
  public static void setPlatformStarted() {
    PlatformUtils.platformStarted = true;
  }
}
