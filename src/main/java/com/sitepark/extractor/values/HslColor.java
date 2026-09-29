package com.sitepark.extractor.values;

public record HslColor(double hue, double lightness, double saturation) {
  public HslColor {
    if (isOutOfRange(hue, 360.0)) {
      throw new IllegalArgumentException("hsl hue has to range from 0-360, given (" + hue + ")");
    }
    if (isOutOfRange(lightness, 100.0)) {
      throw new IllegalArgumentException(
          "hsl lightness has to range from 0-100, given (" + lightness + ")");
    }
    if (isOutOfRange(saturation, 100.0)) {
      throw new IllegalArgumentException(
          "hsl saturation has to range from 0-100, given (" + saturation + ")");
    }
  }

  private static boolean isOutOfRange(double value, double max) {
    return value < 0.0 || value > max;
  }
}
