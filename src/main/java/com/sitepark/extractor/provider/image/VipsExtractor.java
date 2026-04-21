package com.sitepark.extractor.provider.image;

import com.sitepark.extractor.ExtractionException;
import com.sitepark.extractor.types.ImageInfo;
import com.sitepark.extractor.values.ColorPalette;
import com.sitepark.extractor.values.ColorPaletteEntry;
import com.sitepark.extractor.values.RgbColor;
import com.sitepark.extractor.values.VibrantColors;
import com.sitepark.vips.command.ExtractResult;
import com.sitepark.vips.manager.VipsClientPool;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Extracts image dimensions, alpha-channel presence and vibrant colours from image files using
 * <a href="https://www.libvips.org/">libvips</a> via a {@link VipsClientPool}.
 */
public class VipsExtractor {

  private final VipsClientPool vipsClientPool;

  private static final int COLORS_PALETTE_BIT_DEPTH = 5;

  public VipsExtractor(VipsClientPool vipsClientPool) {
    this.vipsClientPool = vipsClientPool;
  }

  /**
   * Extracts image properties from the file at the given path and applies them to {@code builder}.
   *
   * @param path the path to the image file
   * @param builder the builder to populate with width, height, alpha and vibrant colours
   * @throws ExtractionException if the image cannot be read
   */
  public void applyData(Path path, ImageInfo.Builder builder) throws ExtractionException {
    try {
      ExtractResult result = this.vipsClientPool.extract(path, COLORS_PALETTE_BIT_DEPTH);
      builder
          .width(result.width())
          .height(result.height())
          .hasAlpha(result.hasAlpha())
          .vibrantColors(this.createVibrantColors(result));
    } catch (IOException e) {
      throw new ExtractionException("Failed to extract image", e);
    }
  }

  private VibrantColors createVibrantColors(ExtractResult result) {
    VibrantColorAnalyser analyse =
        VibrantColorAnalyser.create(this.mapColorPalette(result.colorPalette()));
    return analyse.analyse();
  }

  private ColorPalette mapColorPalette(com.sitepark.vips.command.ColorPalette vipsColorPalette) {
    return new ColorPalette(
        vipsColorPalette.colors().stream()
            .map(
                color ->
                    new ColorPaletteEntry(
                        new RgbColor(color.red(), color.green(), color.blue()), color.pixelCount()))
            .toList());
  }
}
