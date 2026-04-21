package com.sitepark.extractor.provider.image;

import com.drew.imaging.FileType;
import com.drew.imaging.FileTypeDetector;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.file.FileSystemMetadataReader;
import com.drew.metadata.file.FileTypeDirectory;
import com.drew.metadata.iptc.IptcDirectory;
import com.sitepark.extractor.ExtractionException;
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

/**
 * Reads IPTC metadata (title, description, copyright) from image files using the
 * <a href="https://drewnoakes.com/code/exif/">drewnoakes metadata-extractor</a> library and
 * applies the values to an {@link ImageInfo.Builder}.
 */
public class ComDrewImageMetadataReader {

  public void applyData(Path path, ImageInfo.Builder builder) throws ExtractionException {
    com.drew.metadata.Metadata imageMetadata = this.readImageMetadata(path);
    this.applyData(imageMetadata, builder);
  }

  void applyData(com.drew.metadata.Metadata imageMetadata, ImageInfo.Builder builder) {
    Collection<IptcDirectory> iptcDirectories =
        imageMetadata.getDirectoriesOfType(IptcDirectory.class);

    for (IptcDirectory iptc : iptcDirectories) {
      String iptcCopyright = iptc.getDescription(IptcDirectory.TAG_COPYRIGHT_NOTICE);
      iptcCopyright = this.normalizeString(iptcCopyright);
      if (iptcCopyright != null && !iptcCopyright.isEmpty()) {
        builder.copyright(iptcCopyright);
      }

      String title = null;
      String iptcHeadline = iptc.getDescription(IptcDirectory.TAG_HEADLINE);
      iptcHeadline = this.normalizeString(iptcHeadline);
      if (iptcHeadline != null && !iptcHeadline.isEmpty()) {
        title = iptcHeadline;
      }
      if (title == null) {
        String iptcTitle = iptc.getDescription(IptcDirectory.TAG_OBJECT_NAME);
        iptcTitle = this.normalizeString(iptcTitle);
        if (iptcTitle != null && !iptcTitle.isEmpty()) {
          title = iptcTitle;
        }
      }
      if (title != null) {
        builder.title(title);
      }

      String iptcCaptionAbstract = iptc.getDescription(IptcDirectory.TAG_CAPTION);
      iptcCaptionAbstract = this.normalizeMultiLineString(iptcCaptionAbstract);
      if (iptcCaptionAbstract != null && !iptcCaptionAbstract.isEmpty()) {
        builder.description(iptcCaptionAbstract);
      }
    }
  }

  private com.drew.metadata.Metadata readImageMetadata(@NotNull Path path)
      throws ExtractionException {

    try (InputStream inputStream = Files.newInputStream(path)) {
      com.drew.metadata.Metadata metadata = this.readImageMetadata(inputStream, Files.size(path));
      (new FileSystemMetadataReader()).read(path.toFile(), metadata);
      return metadata;
    } catch (IOException | ImageProcessingException e) {
      throw new ExtractionException("Unable to read image metadata", e);
    }
  }

  @NotNull
  private com.drew.metadata.Metadata readImageMetadata(
      @NotNull InputStream inputStream, long streamLength)
      throws ImageProcessingException, IOException {
    BufferedInputStream bufferedInputStream =
        inputStream instanceof BufferedInputStream
            ? (BufferedInputStream) inputStream
            : new BufferedInputStream(inputStream);
    FileType fileType = FileTypeDetector.detectFileType(bufferedInputStream);
    if (fileType == FileType.Unknown) {
      return new com.drew.metadata.Metadata();
    }
    com.drew.metadata.Metadata metadata =
        ImageMetadataReader.readMetadata(bufferedInputStream, streamLength, fileType);
    metadata.addDirectory(new FileTypeDirectory(fileType));
    return metadata;
  }

  private String normalizeString(String s) {
    if (s == null) {
      return s;
    }
    // Replace invalid Character
    s = s.replaceAll("\\p{C}", "");
    s = s.trim();
    return s;
  }

  private String normalizeMultiLineString(String s) {

    if (s == null) {
      return s;
    }

    BufferedReader lineReader = new BufferedReader(new StringReader(s));
    String line = null;
    try {
      List<String> lines = new ArrayList<>();
      while ((line = lineReader.readLine()) != null) {
        lines.add(this.normalizeString(line));
      }
      return String.join("\n", lines);
    } catch (IOException e) {
      return this.normalizeString(s);
    }
  }
}
