package com.sitepark.extractor.types;

import com.sitepark.extractor.FileInfoFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MediaType;

/**
 * {@link FileInfoFactory} implementation that creates {@link DocInfo} objects for document
 * formats: PDF, RTF, OpenDocument and Microsoft Office (legacy and Open XML).
 */
public class DocInfoFactory implements FileInfoFactory<DocInfo> {

  private static final Set<MediaType> SUPPORTED_TYPES =
      Collections.unmodifiableSet(
          new HashSet<>(
              Arrays.asList(
                  MediaType.application("rtf"),
                  MediaType.application("pdf"),

                  // OpenOffice
                  MediaType.application("vnd.oasis.opendocument.text"),
                  MediaType.application("vnd.oasis.opendocument.presentation"),
                  MediaType.application("vnd.oasis.opendocument.spreadsheet"),

                  // Microsoft
                  MediaType.application("msword"),
                  MediaType.application("vnd.ms-powerpoint"),
                  MediaType.application("vnd.ms-excel"),
                  MediaType.application(
                      "vnd.openxmlformats-officedocument.wordprocessingml.document"),
                  MediaType.application("vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
                  MediaType.application(
                      "vnd.openxmlformats-officedocument.presentationml.presentation"),
                  // Compatibility
                  MediaType.application("vnd.openxmlformats-officedocument.wordprocessingml"),
                  MediaType.application("vnd.openxmlformats-officedocument.spreadsheetml"),
                  MediaType.application("vnd.openxmlformats-officedocument.presentationml"))));

  /**
   * Returns {@code true} if the given MIME type string is among the supported document types.
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
   * Creates a {@link DocInfo} by extracting title, description, creation date, last-modification
   * date and text content from the given Tika {@link Metadata}.
   *
   * @param metadata the Tika metadata produced during parsing
   * @param extractedContent the plain-text content extracted from the document, may be {@code null}
   * @return a new {@link DocInfo} instance; never {@code null}
   * @throws NullPointerException if {@code metadata} is {@code null}
   */
  @Override
  public DocInfo create(Metadata metadata, String extractedContent) {
    Objects.requireNonNull(metadata, "metadata must not be null");
    return DocInfo.builder()
        .title(this.getTitle(metadata))
        .description(this.getDescription(metadata))
        .creationDate(this.getCreationDate(metadata))
        .lastModificationDate(this.getLastModifiedDate(metadata))
        .extractedContent(extractedContent)
        .build();
  }

  private String getTitle(Metadata metadata) {
    return this.getFirstString(metadata, TikaCoreProperties.TITLE);
  }

  private String getDescription(Metadata metadata) {
    return this.getFirstString(metadata, TikaCoreProperties.DESCRIPTION);
  }

  private Long getCreationDate(Metadata metadata) {
    return this.getDate(metadata, TikaCoreProperties.CREATED);
  }

  private Long getLastModifiedDate(Metadata metadata) {
    return this.getDate(metadata, TikaCoreProperties.MODIFIED);
  }

  private Long getDate(Metadata metadata, Property name) {
    Date date = metadata.getDate(name);
    if (date == null) {
      return null;
    }
    return date.getTime();
  }

  private String getFirstString(Metadata metadata, Property name) {

    String[] value = metadata.getValues(name);
    if (value.length == 0) {
      return null;
    }

    return value[0];
  }
}
