package com.sitepark.extractor.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sitepark.extractor.FileInfo;
import com.sitepark.extractor.values.VibrantColors;
import java.io.Serial;
import java.util.Objects;

/**
 * Immutable value object representing image metadata.
 *
 * <p>Use {@link #builder()} to construct instances and {@link #toBuilder()} to create modified
 * copies. Supports JSON serialization and deserialization via Jackson.
 */
@SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
@JsonDeserialize(builder = ImageInfo.Builder.class)
public final class ImageInfo extends FileInfo {

  @Serial private static final long serialVersionUID = 1L;

  private final int width;
  private final int height;
  private final boolean hasAlpha;
  private final String title;
  private final String description;
  private final String copyright;
  private final VibrantColors vibrantColors;

  private ImageInfo(Builder builder) {
    this.width = builder.width;
    this.height = builder.height;
    this.hasAlpha = builder.hasAlpha;
    this.title = builder.title;
    this.description = builder.description;
    this.copyright = builder.copyright;
    this.vibrantColors = builder.vibrantColors;
  }

  /**
   * Returns the image width in pixels, or {@code 0} if not set.
   *
   * @return the width in pixels
   */
  @JsonProperty
  public int width() {
    return this.width;
  }

  /**
   * Returns the image height in pixels, or {@code 0} if not set.
   *
   * @return the height in pixels
   */
  @JsonProperty
  public int height() {
    return this.height;
  }

  /**
   * Returns {@code true} if the image has an alpha (transparency) channel.
   *
   * @return {@code true} if the image has an alpha channel
   */
  @JsonProperty
  public boolean hasAlpha() {
    return this.hasAlpha;
  }

  /**
   * Returns the image title, or {@code null} if not set.
   *
   * @return the title, or {@code null}
   */
  @JsonProperty
  public String title() {
    return this.title;
  }

  /**
   * Returns the image description, or {@code null} if not set.
   *
   * @return the description, or {@code null}
   */
  @JsonProperty
  public String description() {
    return this.description;
  }

  /**
   * Returns the image copyright notice, or {@code null} if not set.
   *
   * @return the copyright, or {@code null}
   */
  @JsonProperty
  public String copyright() {
    return this.copyright;
  }

  /**
   * Returns the vibrant color analysis result, or {@code null} if not set.
   *
   * @return the {@link VibrantColors}, or {@code null}
   */
  @JsonProperty
  public VibrantColors vibrantColors() {
    return this.vibrantColors;
  }

  /**
   * Returns a new builder for {@link ImageInfo}.
   *
   * @return a new builder instance
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Returns a new builder pre-populated with all values from this instance.
   *
   * @return a new builder instance
   */
  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        this.width,
        this.height,
        this.hasAlpha,
        this.title,
        this.description,
        this.copyright,
        this.vibrantColors);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof ImageInfo that)
        && this.width == that.width()
        && this.height == that.height()
        && this.hasAlpha == that.hasAlpha()
        && Objects.equals(this.title, that.title())
        && Objects.equals(this.description, that.description())
        && Objects.equals(this.copyright, that.copyright())
        && Objects.equals(this.vibrantColors, that.vibrantColors());
  }

  @Override
  public String toString() {
    return "ImageInfo [width="
        + this.width
        + ", height="
        + this.height
        + ", hasAlpha="
        + this.hasAlpha
        + ", title="
        + this.title
        + ", description="
        + this.description
        + ", copyright="
        + this.copyright
        + ", vibrantColors="
        + this.vibrantColors
        + "]";
  }

  /** Builder for {@link ImageInfo}. */
  @JsonPOJOBuilder(withPrefix = "", buildMethodName = "build")
  public static final class Builder {

    private int width;
    private int height;
    private boolean hasAlpha;
    private String title;
    private String description;
    private String copyright;
    private VibrantColors vibrantColors;

    private Builder() {}

    private Builder(ImageInfo imageInfo) {
      this.width = imageInfo.width();
      this.height = imageInfo.height();
      this.hasAlpha = imageInfo.hasAlpha();
      this.title = imageInfo.title();
      this.description = imageInfo.description();
      this.copyright = imageInfo.copyright();
      this.vibrantColors = imageInfo.vibrantColors();
    }

    /**
     * Sets the image width in pixels.
     *
     * @param width the width in pixels
     * @return this builder
     */
    public Builder width(int width) {
      this.width = width;
      return this;
    }

    /**
     * Sets the image height in pixels.
     *
     * @param height the height in pixels
     * @return this builder
     */
    public Builder height(int height) {
      this.height = height;
      return this;
    }

    /**
     * Sets whether the image has an alpha (transparency) channel.
     *
     * @param hasAlpha {@code true} if the image has an alpha channel
     * @return this builder
     */
    public Builder hasAlpha(boolean hasAlpha) {
      this.hasAlpha = hasAlpha;
      return this;
    }

    /**
     * Sets the image title.
     *
     * @param title the title, may be {@code null}
     * @return this builder
     */
    public Builder title(String title) {
      this.title = title;
      return this;
    }

    /**
     * Sets the image description.
     *
     * @param description the description, may be {@code null}
     * @return this builder
     */
    public Builder description(String description) {
      this.description = description;
      return this;
    }

    /**
     * Sets the image copyright notice.
     *
     * @param copyright the copyright, may be {@code null}
     * @return this builder
     */
    public Builder copyright(String copyright) {
      this.copyright = copyright;
      return this;
    }

    /**
     * Sets the vibrant color analysis result.
     *
     * @param vibrantColors the vibrant colors, may be {@code null}
     * @return this builder
     */
    public Builder vibrantColors(VibrantColors vibrantColors) {
      this.vibrantColors = vibrantColors;
      return this;
    }

    /**
     * Builds a new {@link ImageInfo} from the current builder state.
     *
     * @return a new {@link ImageInfo} instance
     */
    public ImageInfo build() {
      return new ImageInfo(this);
    }
  }
}
