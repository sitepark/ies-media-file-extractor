package com.sitepark.extractor.provider.image;

import com.sitepark.extractor.ExtractionException;
import com.sitepark.extractor.FileInfoProvider;
import com.sitepark.extractor.types.ImageInfo;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;

/**
 * {@link FileInfoProvider} implementation that creates {@link ImageInfo} objects for image formats
 * supported by <a href="https://www.libvips.org/">libvips</a>.
 */
public class ImageInfoProvider implements FileInfoProvider<ImageInfo> {

  private static final Set<MediaType> SUPPORTED_TYPES =
      Collections.unmodifiableSet(
          new HashSet<>(
              Arrays.asList(

                  // Raster formats
                  MediaType.image("jpeg"),
                  MediaType.image("png"),
                  MediaType.image("gif"),
                  MediaType.image("tiff"),
                  MediaType.image("webp"),
                  MediaType.image("avif"),
                  MediaType.image("bmp"),
                  MediaType.image("x-bmp"),
                  MediaType.image("jxl"),

                  // HEIF family
                  MediaType.image("heif"),
                  MediaType.image("heic"),

                  // Vector
                  MediaType.image("svg+xml"),

                  // Portable bitmap formats
                  MediaType.image("x-portable-pixmap"),
                  MediaType.image("x-portable-graymap"),
                  MediaType.image("x-portable-bitmap"),
                  MediaType.image("x-portable-anymap"),

                  // HDR / scientific
                  MediaType.image("x-exr"),
                  MediaType.image("vnd.radiance"),
                  MediaType.image("fits"),

                  // Icon
                  MediaType.image("x-ico"),
                  MediaType.image("vnd.microsoft.icon"),

                  // Postscript
                  MediaType.application("postscript"),

                  // DDS (DirectDraw Surface)
                  MediaType.image("vnd.ms-dds"))));

  private final ComDrewImageMetadataReader comDrewImageMetadataReader;
  private final VipsExtractor vipsExtractor;

  public ImageInfoProvider(
      ComDrewImageMetadataReader comDrewImageMetadataReader, VipsExtractor vipsExtractor) {
    this.comDrewImageMetadataReader = comDrewImageMetadataReader;
    this.vipsExtractor = vipsExtractor;
  }

  /**
   * Returns {@code true} if the given MIME type string is among the supported image types.
   *
   * @param type the MIME type string to check
   * @return {@code true} if supported, {@code false} otherwise
   * @throws NullPointerException if {@code type} is {@code null}
   */
  public static boolean isSupported(String type) {
    Objects.requireNonNull(type, "type must not be null");
    MediaType mediaType = MediaType.parse(type);
    return SUPPORTED_TYPES.contains(mediaType);
  }

  /** {@inheritDoc} */
  @Override
  public Set<MediaType> getSupportedTypes() {
    return SUPPORTED_TYPES;
  }

  /**
   * Creates an {@link ImageInfo} from the given Tika metadata.
   *
   * @param metadata the Tika metadata produced during parsing
   * @param extractedContent unused for image types; may be {@code null}
   * @return a new {@link ImageInfo} instance; never {@code null}
   * @throws NullPointerException if {@code metadata} is {@code null}
   */
  @Override
  public ImageInfo create(Path path, Metadata metadata, String extractedContent)
      throws ExtractionException {
    Objects.requireNonNull(metadata, "metadata must not be null");

    ImageInfo.Builder builder = ImageInfo.builder();
    this.comDrewImageMetadataReader.applyData(path, builder);
    this.vipsExtractor.applyData(path, builder);
    return builder.build();
  }
}
