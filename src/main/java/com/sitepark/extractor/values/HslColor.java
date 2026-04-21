package com.sitepark.extractor.values;

public record HslColor(double hue, double lightness, double saturation) {
  public HslColor(double hue, double lightness, double saturation) {
    if (!(hue < (double) 0.0F) && !(hue > (double) 360.0F)) {
      if (!(lightness < (double) 0.0F) && !(lightness > (double) 100.0F)) {
        if (!(saturation < (double) 0.0F) && !(saturation > (double) 100.0F)) {
          this.hue = hue;
          this.lightness = lightness;
          this.saturation = saturation;
        } else {
          throw new IllegalArgumentException(
              "hsl saturation has to range from 0-100, given (" + saturation + ")");
        }
      } else {
        throw new IllegalArgumentException(
            "hsl lightness has to range from 0-100, given (" + lightness + ")");
      }
    } else {
      throw new IllegalArgumentException("hsl hue has to range from 0-360, given (" + hue + ")");
    }
  }
}
