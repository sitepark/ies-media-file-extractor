package com.sitepark.extractor.values;

import java.util.List;

public record ColorPalette(List<ColorPaletteEntry> colors) {

  public ColorPalette(List<ColorPaletteEntry> colors) {
    this.colors = List.copyOf(colors);
  }
}
