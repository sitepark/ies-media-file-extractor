package com.sitepark.extractor.values;

import java.io.Serializable;
import org.jspecify.annotations.Nullable;

public record VibrantColors(
    RgbColor average,
    @Nullable RgbColor dominant,
    @Nullable RgbColor vibrant,
    @Nullable RgbColor muted,
    @Nullable RgbColor darkVibrant,
    @Nullable RgbColor darkMuted,
    @Nullable RgbColor lightVibrant,
    @Nullable RgbColor lightMuted)
    implements Serializable {}
