package com.sitepark.extractor.values;

public record SrgbColor(double red, double green, double blue) {
  public SrgbColor {
    if (isOutOfRange(red) || isOutOfRange(green) || isOutOfRange(blue)) {
      throw new IllegalArgumentException(
          "srgb values have to range from 0-1, given (" + red + ", " + green + ", " + blue + ")");
    }
  }

  private static boolean isOutOfRange(double value) {
    return value < 0.0 || value > 1.0;
  }

  public static SrgbColor ofRgbColor(int red, int green, int blue) {
    return new RgbColor(red, green, blue).toSrgbColor();
  }

  public RgbColor toRgbColor() {
    return new RgbColor(
        (int) Math.round(this.red * (double) 255.0F),
        (int) Math.round(this.green * (double) 255.0F),
        (int) Math.round(this.blue * (double) 255.0F));
  }
}
