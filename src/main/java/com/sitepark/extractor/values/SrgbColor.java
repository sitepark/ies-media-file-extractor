package com.sitepark.extractor.values;

public record SrgbColor(double red, double green, double blue) {
  public SrgbColor(double red, double green, double blue) {
    if (!(red < (double) 0.0F)
        && !(red > (double) 1.0F)
        && !(green < (double) 0.0F)
        && !(green > (double) 1.0F)
        && !(blue < (double) 0.0F)
        && !(blue > (double) 1.0F)) {
      this.red = red;
      this.green = green;
      this.blue = blue;
    } else {
      throw new IllegalArgumentException(
          "srgb values have to range from 0-1, given (" + red + ", " + green + ", " + blue + ")");
    }
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
