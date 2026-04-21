package com.sitepark.extractor.values;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class RgbColorTest {

  @Test
  void testEquals() {
    EqualsVerifier.forClass(RgbColor.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(RgbColor.class).verify();
  }

  @Test
  void testRed() {
    RgbColor color = new RgbColor(200, 0, 0);
    assertEquals(200, color.red(), "unexpected red");
  }

  @Test
  void testGreen() {
    RgbColor color = new RgbColor(0, 200, 0);
    assertEquals(200, color.green(), "unexpected green");
  }

  @Test
  void testBlue() {
    RgbColor color = new RgbColor(0, 0, 200);
    assertEquals(200, color.blue(), "unexpected blue");
  }

  @Test
  void testInvalidRedTooLow() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new RgbColor(-1, 0, 0),
        "red below 0 should throw IllegalArgumentException");
  }

  @Test
  void testInvalidRedTooHigh() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new RgbColor(256, 0, 0),
        "red above 255 should throw IllegalArgumentException");
  }

  @Test
  void testInvalidGreenTooLow() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new RgbColor(0, -1, 0),
        "green below 0 should throw IllegalArgumentException");
  }

  @Test
  void testInvalidGreenTooHigh() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new RgbColor(0, 256, 0),
        "green above 255 should throw IllegalArgumentException");
  }

  @Test
  void testInvalidBlueTooLow() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new RgbColor(0, 0, -1),
        "blue below 0 should throw IllegalArgumentException");
  }

  @Test
  void testInvalidBlueTooHigh() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new RgbColor(0, 0, 256),
        "blue above 255 should throw IllegalArgumentException");
  }

  @Test
  void testOfSrgbColor() {
    assertEquals(
        new SrgbColor(0.5, 0.5, 0.5).toRgbColor(),
        RgbColor.ofSrgbColor(0.5, 0.5, 0.5),
        "ofSrgbColor should convert 0-1 sRGB to 0-255 RGB");
  }

  @Test
  void testToSrgbColor() {
    RgbColor color = new RgbColor(0, 0, 0);
    assertEquals(
        new SrgbColor(0.0, 0.0, 0.0),
        color.toSrgbColor(),
        "black RgbColor should map to sRGB(0,0,0)");
  }
}
