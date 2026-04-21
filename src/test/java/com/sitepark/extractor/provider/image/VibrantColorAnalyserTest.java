package com.sitepark.extractor.provider.image;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.sitepark.extractor.values.ColorPalette;
import com.sitepark.extractor.values.ColorPaletteEntry;
import com.sitepark.extractor.values.RgbColor;
import com.sitepark.extractor.values.VibrantColors;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class VibrantColorAnalyserTest {

  private VibrantColors colors;

  @BeforeEach
  void setUp() {
    ColorPalette palette =
        new ColorPalette(
            List.of(
                new ColorPaletteEntry(new RgbColor(197, 88, 75), 10), // Vibrant
                new ColorPaletteEntry(new RgbColor(107, 126, 110), 10), // Muted
                new ColorPaletteEntry(new RgbColor(89, 55, 35), 10), // DarkVibrant
                new ColorPaletteEntry(new RgbColor(40, 78, 67), 10), // DarkMuted
                new ColorPaletteEntry(new RgbColor(218, 197, 169), 10), // LightVibrant
                new ColorPaletteEntry(new RgbColor(169, 158, 130), 10))); // LightMuted
    this.colors = VibrantColorAnalyser.create(palette).analyse();
  }

  private static String toHex(RgbColor color) {
    return String.format("#%02X%02X%02X", color.red(), color.green(), color.blue());
  }

  @Test
  void testVibrant() {
    assertEquals("#DAC5A9", toHex(this.colors.vibrant()), "unexpected vibrant color");
  }

  @Test
  void testMuted() {
    assertEquals("#593723", toHex(this.colors.muted()), "unexpected muted color");
  }

  @Test
  void testDarkVibrant() {
    assertEquals("#A99E82", toHex(this.colors.darkVibrant()), "unexpected darkVibrant color");
  }

  @Test
  void testDarkMuted() {
    assertEquals("#284E43", toHex(this.colors.darkMuted()), "unexpected darkMuted color");
  }

  @Test
  void testLightVibrantIsNull() {
    assertNull(this.colors.lightVibrant(), "lightVibrant should be null");
  }

  @Test
  void testLightMutedIsNull() {
    assertNull(this.colors.lightMuted(), "lightMuted should be null");
  }

  @Test
  void testAverageColor() {
    assertEquals("#897562", toHex(this.colors.average()), "unexpected average color");
  }

  @Test
  void testLightVibrantIsFoundAndBlocksLightMuted() {
    // RgbColor(0,0,180): sat≈0.353 (in lightVibrant min range) and luma=1.0 (>0.7, fails vibrant)
    // → assigned to lightVibrant; also qualifies for lightMuted so isAlreadySelected(entry) returns
    // true, covering the lightVibrant==entry branch
    ColorPalette palette =
        new ColorPalette(List.of(new ColorPaletteEntry(new RgbColor(0, 0, 180), 10)));
    VibrantColors result = VibrantColorAnalyser.create(palette).analyse();
    assertEquals(
        new RgbColor(0, 0, 180),
        result.lightVibrant(),
        "color with luma>0.7 should be selected as lightVibrant, not vibrant");
  }

  @Test
  void testDominantEntryWithHighestPixelCount() {
    ColorPalette palette =
        new ColorPalette(
            List.of(
                new ColorPaletteEntry(new RgbColor(10, 10, 10), 5),
                new ColorPaletteEntry(new RgbColor(200, 200, 200), 20)));
    VibrantColors result = VibrantColorAnalyser.create(palette).analyse();
    assertEquals(
        new RgbColor(200, 200, 200),
        result.dominant(),
        "entry with highest pixelCount should be selected as dominant");
  }

  @Nested
  class WithEmptyPalette {

    private VibrantColors colors;

    @BeforeEach
    void setUp() {
      this.colors = VibrantColorAnalyser.create(new ColorPalette(List.of())).analyse();
    }

    @Test
    void testDominantIsNull() {
      assertNull(this.colors.dominant(), "empty palette should produce null dominant");
    }

    @Test
    void testVibrantIsNull() {
      assertNull(this.colors.vibrant(), "empty palette should produce null vibrant");
    }

    @Test
    void testAverageIsBlack() {
      assertEquals(
          new RgbColor(0, 0, 0),
          this.colors.average(),
          "empty palette average color should be black");
    }
  }
}
