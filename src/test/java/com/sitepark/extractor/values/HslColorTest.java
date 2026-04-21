package com.sitepark.extractor.values;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class HslColorTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(HslColor.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(HslColor.class).verify();
  }

  @Test
  void testHue() {
    HslColor hsl = new HslColor(180.0, 50.0, 75.0);
    assertEquals(180.0, hsl.hue(), "unexpected hue");
  }

  @Test
  void testLightness() {
    HslColor hsl = new HslColor(180.0, 50.0, 75.0);
    assertEquals(50.0, hsl.lightness(), "unexpected lightness");
  }

  @Test
  void testSaturation() {
    HslColor hsl = new HslColor(180.0, 50.0, 75.0);
    assertEquals(75.0, hsl.saturation(), "unexpected saturation");
  }

  @Test
  void testInvalidHueTooLow() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new HslColor(-0.1, 0.0, 0.0),
        "hue below 0 should throw IllegalArgumentException");
  }

  @Test
  void testInvalidHueTooHigh() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new HslColor(360.1, 0.0, 0.0),
        "hue above 360 should throw IllegalArgumentException");
  }

  @Test
  void testInvalidLightnessTooLow() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new HslColor(0.0, -0.1, 0.0),
        "lightness below 0 should throw IllegalArgumentException");
  }

  @Test
  void testInvalidLightnessTooHigh() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new HslColor(0.0, 100.1, 0.0),
        "lightness above 100 should throw IllegalArgumentException");
  }

  @Test
  void testInvalidSaturationTooLow() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new HslColor(0.0, 0.0, -0.1),
        "saturation below 0 should throw IllegalArgumentException");
  }

  @Test
  void testInvalidSaturationTooHigh() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new HslColor(0.0, 0.0, 100.1),
        "saturation above 100 should throw IllegalArgumentException");
  }
}
