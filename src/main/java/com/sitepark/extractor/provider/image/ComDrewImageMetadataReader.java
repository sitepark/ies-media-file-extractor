package com.sitepark.extractor.provider.image;

import com.adobe.internal.xmp.XMPException;
import com.drew.imaging.FileType;
import com.drew.imaging.FileTypeDetector;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.file.FileSystemMetadataReader;
import com.drew.metadata.file.FileTypeDirectory;
import com.drew.metadata.iptc.IptcDirectory;
import com.drew.metadata.xmp.XmpDirectory;
import com.sitepark.extractor.ExtractionException;
import com.sitepark.extractor.MediaType;
import com.sitepark.extractor.types.ImageInfo;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import org.jspecify.annotations.Nullable;

/**
 * Reads IPTC metadata (title, description, copyright) and the XMP digital source type from image
 * files using the <a href="https://drewnoakes.com/code/exif/">drewnoakes metadata-extractor</a>
 * library and applies the values to an {@link ImageInfo.Builder}.
 */
public class ComDrewImageMetadataReader {

  static final String IPTC_EXT_NAMESPACE = "http://iptc.org/std/Iptc4xmpExt/2008-02-29/";

  static final String DIGITAL_SOURCE_TYPE = "DigitalSourceType";

  private static final String SVG_XML_SUBTYPE = "svg+xml";

  public void applyData(Path path, MediaType mediaType, ImageInfo.Builder builder)
      throws ExtractionException {
    ReaderResult result = this.readImageMetadata(path);
    this.applyData(result, mediaType, builder);
  }

  void applyData(ReaderResult result, MediaType mediaType, ImageInfo.Builder builder) {
    this.applyType(result, mediaType, builder);

    Collection<IptcDirectory> iptcDirectories =
        result.metadata().getDirectoriesOfType(IptcDirectory.class);
    for (IptcDirectory iptc : iptcDirectories) {
      this.applyIptcData(iptc, builder);
    }

    for (XmpDirectory xmp : result.metadata().getDirectoriesOfType(XmpDirectory.class)) {
      String digitalSourceType = this.normalizeString(this.readDigitalSourceType(xmp));
      if (digitalSourceType != null && !digitalSourceType.isEmpty()) {
        builder.digitalSourceType(digitalSourceType);
      }
    }
  }

  private void applyType(ReaderResult result, MediaType mediaType, ImageInfo.Builder builder) {
    if (result.fileType() != FileType.Unknown) {
      builder.type(result.fileType().getName().toLowerCase(Locale.ROOT));
    } else if (SVG_XML_SUBTYPE.equals(mediaType.subtype())) {
      builder.type("svg");
    } else {
      builder.type(mediaType.subtype().toLowerCase(Locale.ROOT));
    }
  }

  private void applyIptcData(IptcDirectory iptc, ImageInfo.Builder builder) {
    String iptcCopyright =
        this.normalizeString(iptc.getDescription(IptcDirectory.TAG_COPYRIGHT_NOTICE));
    if (iptcCopyright != null && !iptcCopyright.isEmpty()) {
      builder.copyright(iptcCopyright);
    }

    String title = this.normalizeString(iptc.getDescription(IptcDirectory.TAG_HEADLINE));
    if (title == null || title.isEmpty()) {
      title = this.normalizeString(iptc.getDescription(IptcDirectory.TAG_OBJECT_NAME));
    }
    if (title != null && !title.isEmpty()) {
      builder.title(title);
    }

    String iptcCaptionAbstract =
        this.normalizeMultiLineString(iptc.getDescription(IptcDirectory.TAG_CAPTION));
    if (iptcCaptionAbstract != null && !iptcCaptionAbstract.isEmpty()) {
      builder.description(iptcCaptionAbstract);
    }
  }

  // chained access is dictated by the XMP library API
  @SuppressWarnings("PMD.LawOfDemeter")
  private @Nullable String readDigitalSourceType(XmpDirectory xmp) {
    try {
      return xmp.getXMPMeta().getPropertyString(IPTC_EXT_NAMESPACE, DIGITAL_SOURCE_TYPE);
    } catch (XMPException e) {
      return null;
    }
  }

  private ReaderResult readImageMetadata(@NotNull Path path) throws ExtractionException {

    try (InputStream inputStream = Files.newInputStream(path)) {
      ReaderResult result = this.readImageMetadata(inputStream, Files.size(path));
      new FileSystemMetadataReader().read(path.toFile(), result.metadata());
      return result;
    } catch (IOException | ImageProcessingException e) {
      throw new ExtractionException("Unable to read image metadata", e);
    }
  }

  @NotNull
  private ReaderResult readImageMetadata(@NotNull InputStream inputStream, long streamLength)
      throws ImageProcessingException, IOException {
    BufferedInputStream bufferedInputStream =
        inputStream instanceof BufferedInputStream buffered
            ? buffered
            : new BufferedInputStream(inputStream);
    FileType fileType = FileTypeDetector.detectFileType(bufferedInputStream);
    if (fileType == FileType.Unknown) {
      return new ReaderResult(fileType, new com.drew.metadata.Metadata());
    }

    com.drew.metadata.Metadata metadata =
        ImageMetadataReader.readMetadata(bufferedInputStream, streamLength, fileType);
    metadata.addDirectory(new FileTypeDirectory(fileType));
    return new ReaderResult(fileType, metadata);
  }

  private @Nullable String normalizeString(@Nullable String s) {
    if (s == null) {
      return null;
    }
    return this.stripInvalidCharacters(s);
  }

  private String stripInvalidCharacters(String s) {
    // Replace invalid Character
    return s.replaceAll("\\p{C}", "").trim();
  }

  private @Nullable String normalizeMultiLineString(@Nullable String s) {

    if (s == null) {
      return null;
    }

    try (BufferedReader lineReader = new BufferedReader(new StringReader(s))) {
      List<String> lines = new ArrayList<>();
      String line = lineReader.readLine();
      while (line != null) {
        lines.add(this.stripInvalidCharacters(line));
        line = lineReader.readLine();
      }
      return String.join("\n", lines);
    } catch (IOException e) {
      return this.stripInvalidCharacters(s);
    }
  }

  protected record ReaderResult(FileType fileType, com.drew.metadata.Metadata metadata) {
    @Override
    public com.drew.metadata.Metadata metadata() {
      return this.metadata;
    }
  }
}
