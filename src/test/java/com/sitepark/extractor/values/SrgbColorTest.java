package com.sitepark.extractor.values;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class SrgbColorTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(SrgbColor.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(SrgbColor.class).verify();
  }

  @Test
  void testRed() {
    SrgbColor color = new SrgbColor(0.5, 0.0, 0.0);
    assertEquals(0.5, color.red(), "unexpected red");
  }

  @Test
  void testGreen() {
    SrgbColor color = new SrgbColor(0.0, 0.5, 0.0);
    assertEquals(0.5, color.green(), "unexpected green");
  }

  @Test
  void testBlue() {
    SrgbColor color = new SrgbColor(0.0, 0.0, 0.5);
    assertEquals(0.5, color.blue(), "unexpected blue");
  }

  @Test
  void testInvalidRedTooLow() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new SrgbColor(-0.01, 0.5, 0.5),
        "red below 0 should throw IllegalArgumentException");
  }

  @Test
  void testInvalidRedTooHigh() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new SrgbColor(1.01, 0.5, 0.5),
        "red above 1 should throw IllegalArgumentException");
  }

  @Test
  void testInvalidGreenTooLow() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new SrgbColor(0.5, -0.01, 0.5),
        "green below 0 should throw IllegalArgumentException");
  }

  @Test
  void testInvalidGreenTooHigh() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new SrgbColor(0.5, 1.01, 0.5),
        "green above 1 should throw IllegalArgumentException");
  }

  @Test
  void testInvalidBlueTooLow() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new SrgbColor(0.5, 0.5, -0.01),
        "blue below 0 should throw IllegalArgumentException");
  }

  @Test
  void testInvalidBlueTooHigh() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new SrgbColor(0.5, 0.5, 1.01),
        "blue above 1 should throw IllegalArgumentException");
  }

  @Test
  void testOfRgbColor() {
    assertEquals(
        new RgbColor(255, 128, 0).toSrgbColor(),
        SrgbColor.ofRgbColor(255, 128, 0),
        "ofRgbColor should convert 0-255 RGB to sRGB");
  }

  @Test
  void testToRgbColor() {
    SrgbColor srgb = new SrgbColor(1.0, 0.0, 0.0);
    assertEquals(
        new RgbColor(255, 0, 0),
        srgb.toRgbColor(),
        "full-red sRGB should map to RgbColor(255,0,0)");
  }
}
