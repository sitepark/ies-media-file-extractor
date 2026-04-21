package com.sitepark.extractor.provider.image;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sitepark.extractor.values.HslColor;
import com.sitepark.extractor.values.RgbColor;
import org.junit.jupiter.api.Test;

class ColorCalculatorTest {

  @Test
  void testToHslHueForBlueMaxChannel() {
    // Exercises the `max == srgb.blue()` branch in getHue()
    HslColor hsl = ColorCalculator.toHsl(new RgbColor(0, 0, 200));
    assertEquals(240.0, hsl.hue(), "blue-dominant color should produce hue of 240°");
  }

  @Test
  void testToRgbWithZeroSaturationField() {
    // HslColor.saturation = 0 triggers the grayscale branch in toSrgb (r=g=b=lightness)
    RgbColor result = ColorCalculator.toRgb(new HslColor(0.0, 100.0, 0.0));
    assertEquals(new RgbColor(255, 255, 255), result, "zero saturation-field should produce white");
  }

  @Test
  void testToHslHueForGreenMaxChannel() {
    HslColor hsl = ColorCalculator.toHsl(new RgbColor(0, 200, 0));
    assertEquals(120.0, hsl.hue(), "green-dominant color should produce hue of 120°");
  }

  @Test
  void testToRgbGreenChannelNormalizesAboveFour() {
    // hue=240° → hue*6=4.0 → green channel's normalize() receives color=4.0, hitting return-p
    RgbColor result = ColorCalculator.toRgb(new HslColor(240.0, 50.0, 50.0));
    assertEquals(new RgbColor(64, 64, 191), result, "normalize with color=4.0 should return p");
  }

  @Test
  void testToRgbHue360TreatedAsHue0() {
    // hue=360/360=1.0 triggers the false branch (→ hue=0.0), same result as hue=0°
    RgbColor result = ColorCalculator.toRgb(new HslColor(360.0, 50.0, 50.0));
    assertEquals(
        new RgbColor(191, 64, 64), result, "hue=360° should produce the same output as hue=0°");
  }

  @Test
  void testToRgbNormalizesColorBetweenThreeAndFour() {
    // hue=210° → hue*6=3.5 → green channel's normalize() receives color=3.5, hitting
    // p+(q-p)*(4-color)
    RgbColor result = ColorCalculator.toRgb(new HslColor(210.0, 50.0, 50.0));
    assertEquals(
        new RgbColor(64, 128, 191),
        result,
        "normalize with color in [3,4) should return p+(q-p)*(4-color)");
  }

  @Test
  void testInvertDiffWhenEqual() {
    assertEquals(
        1.0, ColorCalculator.invertDiff(0.5, 0.5), "invertDiff of equal values should be 1.0");
  }

  @Test
  void testInvertDiffWhenOpposite() {
    assertEquals(
        0.0,
        ColorCalculator.invertDiff(0.0, 1.0),
        "invertDiff of maximally different values should be 0.0");
  }

  @Test
  void testWeightedMean() {
    // value=1.0 weight=1.0, value=0.0 weight=2.0 → weighted mean = (1*1 + 0*2)/(1+2) = 1/3
    assertEquals(
        1.0 / 3.0,
        ColorCalculator.weightedMean(1.0, 1.0, 0.0, 2.0),
        1e-9,
        "weighted mean should be 1/3 for (1,w=1) and (0,w=2)");
  }
}
