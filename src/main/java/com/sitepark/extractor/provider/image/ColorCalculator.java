package com.sitepark.extractor.provider.image;

import com.sitepark.extractor.values.HslColor;
import com.sitepark.extractor.values.RgbColor;
import com.sitepark.extractor.values.SrgbColor;

public class ColorCalculator {

  public static HslColor toHsl(RgbColor rgb) {
    return toHsl(rgb.toSrgbColor());
  }

  public static HslColor toHsl(SrgbColor srgb) {
    double min = Math.min(srgb.red(), Math.min(srgb.green(), srgb.blue()));
    double max = Math.max(srgb.red(), Math.max(srgb.green(), srgb.blue()));
    double hue = getHue(srgb, max, min);

    double lightness = (max + min) / (double) 2.0F;
    double saturation;
    if (max == min) {
      saturation = 0.0F;
    } else if (lightness <= (double) 0.5F) {
      saturation = (max - min) / (max + min);
    } else {
      saturation = (max - min) / ((double) 2.0F - max - min);
    }

    return new HslColor(hue, saturation * (double) 100.0F, lightness * (double) 100.0F);
  }

  private static double getHue(SrgbColor srgb, double max, double min) {
    double hue;
    if (max == min) {
      hue = 0.0F;
    } else if (max == srgb.red()) {
      hue =
          ((double) 60.0F * (srgb.green() - srgb.blue()) / (max - min) + (double) 360.0F)
              % (double) 360.0F;
    } else if (max == srgb.green()) {
      hue = (double) 60.0F * (srgb.blue() - srgb.red()) / (max - min) + (double) 120.0F;
    } else if (max == srgb.blue()) {
      hue = (double) 60.0F * (srgb.red() - srgb.green()) / (max - min) + (double) 240.0F;
    } else {
      hue = 0.0F;
    }
    return hue;
  }

  public static RgbColor toRgb(HslColor hsl) {
    return toSrgb(hsl).toRgbColor();
  }

  public static SrgbColor toSrgb(HslColor hsl) {
    double hue = hsl.hue() / (double) 360.0F;
    double saturation = hsl.saturation() / (double) 100.0F;
    double lightness = hsl.lightness() / (double) 100.0F;
    double r;
    double g;
    double b;
    if (saturation > (double) 0.0F) {
      hue = hue < (double) 1.0F ? hue * (double) 6.0F : (double) 0.0F;
      double q =
          lightness
              + saturation * (lightness > (double) 0.5F ? (double) 1.0F - lightness : lightness);
      double p = (double) 2.0F * lightness - q;
      r = normalize(q, p, hue < (double) 4.0F ? hue + (double) 2.0F : hue - (double) 4.0F);
      g = normalize(q, p, hue);
      b = normalize(q, p, hue < (double) 2.0F ? hue + (double) 4.0F : hue - (double) 2.0F);
    } else {
      r = lightness;
      g = lightness;
      b = lightness;
    }

    return new SrgbColor(r, g, b);
  }

  private static double normalize(double q, double p, double color) {
    if (color < (double) 1.0F) {
      return p + (q - p) * color;
    } else if (color < (double) 3.0F) {
      return q;
    } else {
      return color < (double) 4.0F ? p + (q - p) * ((double) 4.0F - color) : p;
    }
  }

  public static double createComparisonValue(
      double saturation,
      double targetSaturation,
      double luma,
      double targetLuma,
      long population,
      long highestPopulation) {
    return weightedMean(
        invertDiff(saturation, targetSaturation),
        3f,
        invertDiff(luma, targetLuma),
        6.5f,
        population / (double) highestPopulation,
        0.5f);
  }

  public static double weightedMean(double... values) {
    double sum = 0f;
    double sumWeight = 0f;
    for (int i = 0; i < values.length; i += 2) {
      double value = values[i];
      double weight = values[i + 1];
      sum += (value * weight);
      sumWeight += weight;
    }
    return sum / sumWeight;
  }

  /**
   * Returns a value in the range 0-1. 1 is returned when {@code value} equals the {@code
   * targetValue} and then decreases as the absolute difference between {@code value} and {@code
   * targetValue} increases.
   *
   * @param value the item's value
   * @param targetValue the value that we desire
   */
  public static double invertDiff(double value, double targetValue) {
    return 1f - Math.abs(value - targetValue);
  }
}
