package com.sitepark.extractor.values;

import java.io.Serializable;

public record RgbColor(int red, int green, int blue) implements Serializable {
  public RgbColor(int red, int green, int blue) {
    if (red >= 0 && red <= 255 && green >= 0 && green <= 255 && blue >= 0 && blue <= 255) {
      this.red = red;
      this.green = green;
      this.blue = blue;
    } else {
      throw new IllegalArgumentException("rgb values have to range from 0-255");
    }
  }

  public static RgbColor ofSrgbColor(double red, double green, double blue) {
    return new SrgbColor(red, green, blue).toRgbColor();
  }

  public SrgbColor toSrgbColor() {
    return new SrgbColor(this.red / 255.0, this.green / 255.0, this.blue / 255.0);
  }
}
