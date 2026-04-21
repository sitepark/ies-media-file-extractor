package com.sitepark.extractor.provider.image;

import com.sitepark.extractor.values.*;

public final class VibrantColorAnalyser {

  private final ColorPalette palette;
  private ColorPaletteEntry highestPopulationSwatch;
  private ColorPaletteEntry vibrant;
  private ColorPaletteEntry muted;
  private ColorPaletteEntry darkVibrant;
  private ColorPaletteEntry darkMuted;
  private ColorPaletteEntry lightVibrant;
  private ColorPaletteEntry lightMuted;

  private static final double TARGET_DARK_LUMA = 0.26;
  private static final double MAX_DARK_LUMA = 0.45;
  private static final double MIN_LIGHT_LUMA = 0.55;
  private static final double TARGET_LIGHT_LUMA = 0.74;
  private static final double MIN_NORMAL_LUMA = 0.3;
  private static final double TARGET_NORMAL_LUMA = 0.5;
  private static final double MAX_NORMAL_LUMA = 0.7;
  private static final double TARGET_MUTED_SATURATION = 0.3;
  private static final double MAX_MUTED_SATURATION = 0.4;
  private static final double TARGET_VIBRANT_SATURATION = 1;
  private static final double MIN_VIBRANT_SATURATION = 0.35;

  private VibrantColorAnalyser(ColorPalette palette) {
    this.palette = palette;
  }

  public static VibrantColorAnalyser create(ColorPalette palette) {
    return new VibrantColorAnalyser(palette);
  }

  public VibrantColors analyse() {
    this.findColors();
    return new VibrantColors(
        this.getAverageColor(),
        this.getDominant(),
        this.getVibrant(),
        this.getMuted(),
        this.getDarkVibrant(),
        this.getDarkMuted(),
        this.getLightVibrant(),
        this.getLightMuted());
  }

  private void findColors() {
    for (ColorPaletteEntry entry : this.palette.colors()) {
      if (this.highestPopulationSwatch == null
          || entry.pixelCount() > this.highestPopulationSwatch.pixelCount()) {
        this.highestPopulationSwatch = entry;
      }
    }

    if (this.highestPopulationSwatch == null) {
      return;
    }

    this.vibrant =
        this.findColor(
            TARGET_NORMAL_LUMA,
            MIN_NORMAL_LUMA,
            MAX_NORMAL_LUMA,
            TARGET_VIBRANT_SATURATION,
            MIN_VIBRANT_SATURATION,
            1f);
    this.lightVibrant =
        this.findColor(
            TARGET_LIGHT_LUMA,
            MIN_LIGHT_LUMA,
            1f,
            TARGET_VIBRANT_SATURATION,
            MIN_VIBRANT_SATURATION,
            1f);
    this.darkVibrant =
        this.findColor(
            TARGET_DARK_LUMA,
            0f,
            MAX_DARK_LUMA,
            TARGET_VIBRANT_SATURATION,
            MIN_VIBRANT_SATURATION,
            1f);
    this.muted =
        this.findColor(
            TARGET_NORMAL_LUMA,
            MIN_NORMAL_LUMA,
            MAX_NORMAL_LUMA,
            TARGET_MUTED_SATURATION,
            0f,
            MAX_MUTED_SATURATION);
    this.lightMuted =
        this.findColor(
            TARGET_LIGHT_LUMA,
            MIN_LIGHT_LUMA,
            1f,
            TARGET_MUTED_SATURATION,
            0f,
            MAX_MUTED_SATURATION);
    this.darkMuted =
        this.findColor(
            TARGET_DARK_LUMA, 0f, MAX_DARK_LUMA, TARGET_MUTED_SATURATION, 0f, MAX_MUTED_SATURATION);
    // Now try and generate any missing colors
    this.generateEmptySwatches();
  }

  private ColorPaletteEntry findColor(
      double targetLuma,
      double minLuma,
      double maxLuma,
      double targetSaturation,
      double minSaturation,
      double maxSaturation) {
    ColorPaletteEntry max = null;
    double maxValue = 0f;
    for (ColorPaletteEntry entry : this.palette.colors()) {
      HslColor hsl = ColorCalculator.toHsl(entry.color());
      final double sat = hsl.saturation() / 100;
      final double luma = hsl.lightness() / 100;

      if (sat >= minSaturation
          && sat <= maxSaturation
          && luma >= minLuma
          && luma <= maxLuma
          && !this.isAlreadySelected(entry)) {
        double thisValue =
            ColorCalculator.createComparisonValue(
                sat,
                targetSaturation,
                luma,
                targetLuma,
                entry.pixelCount(),
                this.highestPopulationSwatch.pixelCount());
        if (max == null || thisValue > maxValue) {
          max = entry;
          maxValue = thisValue;
        }
      }
    }
    return max;
  }

  /** Try and generate any missing swatches from the swatches we did find. */
  private void generateEmptySwatches() {
    if (this.vibrant == null) {
      // If we do not have a vibrant color...
      if (this.darkVibrant != null) {
        // ...but we do have a dark vibrant, generate the value by
        // modifying the luma
        HslColor hsl = ColorCalculator.toHsl(this.darkVibrant.color());
        HslColor newHslColor = new HslColor(hsl.hue(), hsl.lightness(), TARGET_NORMAL_LUMA * 100);
        RgbColor newColor = ColorCalculator.toRgb(newHslColor);
        this.vibrant = new ColorPaletteEntry(newColor, 0);
      }
    }
    if (this.darkVibrant == null) {
      // If we do not have a dark vibrant color...
      if (this.vibrant != null) {
        // ...but we do have a vibrant, generate the value by modifying
        // the luma
        final HslColor hsl = ColorCalculator.toHsl(this.vibrant.color());
        final HslColor newHslColor =
            new HslColor(hsl.hue(), hsl.lightness(), TARGET_DARK_LUMA * 100);
        RgbColor newColor = ColorCalculator.toRgb(newHslColor);
        this.darkVibrant = new ColorPaletteEntry(newColor, 0);
      }
    }
  }

  private RgbColor getAverageColor() {
    double r = 0.0;
    double g = 0.0;
    double b = 0.0;
    long total = 0;
    for (ColorPaletteEntry entry : this.palette.colors()) {
      RgbColor color = entry.color();
      total += entry.pixelCount();
      r += color.red() * entry.pixelCount();
      g += color.green() * entry.pixelCount();
      b += color.blue() * entry.pixelCount();
    }
    if (total > 0) {
      r /= total;
      g /= total;
      b /= total;
    }
    return new RgbColor((int) Math.round(r), (int) Math.round(g), (int) Math.round(b));
  }

  public RgbColor getDominant() {
    return this.highestPopulationSwatch != null ? this.highestPopulationSwatch.color() : null;
  }

  public RgbColor getVibrant() {
    return this.vibrant != null ? this.vibrant.color() : null;
  }

  public RgbColor getLightVibrant() {
    return this.lightVibrant != null ? this.lightVibrant.color() : null;
  }

  public RgbColor getDarkVibrant() {
    return this.darkVibrant != null ? this.darkVibrant.color() : null;
  }

  public RgbColor getMuted() {
    return this.muted != null ? this.muted.color() : null;
  }

  public RgbColor getLightMuted() {
    return this.lightMuted != null ? this.lightMuted.color() : null;
  }

  public RgbColor getDarkMuted() {
    return this.darkMuted != null ? this.darkMuted.color() : null;
  }

  private boolean isAlreadySelected(ColorPaletteEntry entry) {
    return this.vibrant == entry
        || this.darkVibrant == entry
        || this.lightVibrant == entry
        || this.muted == entry
        || this.darkMuted == entry
        || this.lightMuted == entry;
  }
}
