package com.sitepark.extractor.values;

import java.io.Serializable;

public record VibrantColors(
    RgbColor average,
    RgbColor dominant,
    RgbColor vibrant,
    RgbColor muted,
    RgbColor darkVibrant,
    RgbColor darkMuted,
    RgbColor lightVibrant,
    RgbColor lightMuted)
    implements Serializable {}
