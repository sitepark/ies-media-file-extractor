package com.sitepark.extractor.types;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sitepark.extractor.FileInfo;
import java.util.Objects;

/**
 * Immutable value object representing document metadata and extracted text content.
 *
 * <p>Use {@link #builder()} to construct instances and {@link #toBuilder()} to create modified
 * copies. Supports JSON serialization and deserialization via Jackson.
 */
@SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
@JsonDeserialize(builder = DocInfo.Builder.class)
public final class DocInfo extends FileInfo {

  private static final long serialVersionUID = 1L;

  private final String title;

  private final String description;

  private final Long creationDate;

  private final Long lastModificationDate;

  private final String extractedContent;

  protected DocInfo(Builder builder) {
    this.title = builder.title;
    this.description = builder.description;
    this.creationDate = builder.creationDate;
    this.lastModificationDate = builder.lastModificationDate;
    this.extractedContent = builder.extractedContent;
  }

  /**
   * Returns the document title, or {@code null} if not set.
   *
   * @return the title, or {@code null}
   */
  public String getTitle() {
    return this.title;
  }

  /**
   * Returns the document description, or {@code null} if not set.
   *
   * @return the description, or {@code null}
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * Returns the document creation date as milliseconds since the Unix epoch, or {@code null} if
   * not set.
   *
   * @return the creation date in epoch milliseconds, or {@code null}
   */
  public Long getCreationDate() {
    return this.creationDate;
  }

  /**
   * Returns the document last-modification date as milliseconds since the Unix epoch, or
   * {@code null} if not set.
   *
   * @return the last-modification date in epoch milliseconds, or {@code null}
   */
  public Long getLastModificationDate() {
    return this.lastModificationDate;
  }

  /**
   * Returns the plain-text content extracted from the document, or {@code null} if no content was
   * extracted or the content was blank.
   *
   * @return the extracted content, or {@code null}
   */
  public String getExtractedContent() {
    return this.extractedContent;
  }

  @Override
  public int hashCode() {
    int hashCode = 0;
    if (this.title != null) {
      hashCode += this.title.hashCode();
    }
    if (this.description != null) {
      hashCode += this.description.hashCode();
    }
    if (this.creationDate != null) {
      hashCode += this.creationDate.hashCode();
    }
    if (this.lastModificationDate != null) {
      hashCode += this.lastModificationDate.hashCode();
    }
    if (this.extractedContent != null) {
      hashCode += this.extractedContent.hashCode();
    }

    return hashCode;
  }

  /**
   * Returns a new builder for {@link DocInfo}.
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
  public String toString() {
    return "DocInfo [title="
        + this.title
        + ", description="
        + this.description
        + ", creationDate="
        + this.creationDate
        + ", lastModificationDate="
        + this.lastModificationDate
        + ", extractedContent="
        + this.extractedContent
        + "]";
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof DocInfo that)) {
      return false;
    }
    return Objects.equals(that.getTitle(), this.title)
        && Objects.equals(that.getDescription(), this.description)
        && Objects.equals(that.getCreationDate(), this.creationDate)
        && Objects.equals(that.getLastModificationDate(), this.lastModificationDate)
        && Objects.equals(that.getExtractedContent(), this.extractedContent);
  }

  /** Builder for {@link DocInfo}. */
  @JsonPOJOBuilder(withPrefix = "", buildMethodName = "build")
  public static final class Builder {

    private String title;

    private String description;

    private Long creationDate;

    private Long lastModificationDate;

    private String extractedContent;

    private Builder() {}

    private Builder(DocInfo docInfo) {
      this.title = docInfo.title;
      this.description = docInfo.description;
      this.creationDate = docInfo.creationDate;
      this.lastModificationDate = docInfo.lastModificationDate;
      this.extractedContent = docInfo.extractedContent;
    }

    /**
     * Sets the document title.
     *
     * @param title the title, may be {@code null}
     * @return this builder
     */
    public Builder title(String title) {
      this.title = title;
      return this;
    }

    /**
     * Sets the document description.
     *
     * @param description the description, may be {@code null}
     * @return this builder
     */
    public Builder description(String description) {
      this.description = description;
      return this;
    }

    /**
     * Sets the document creation date.
     *
     * @param creationDate the creation date as milliseconds since the Unix epoch, may be
     *     {@code null}
     * @return this builder
     */
    public Builder creationDate(Long creationDate) {
      this.creationDate = creationDate;
      return this;
    }

    /**
     * Sets the document last-modification date.
     *
     * @param lastModificationDate the last-modification date as milliseconds since the Unix epoch,
     *     may be {@code null}
     * @return this builder
     */
    public Builder lastModificationDate(Long lastModificationDate) {
      this.lastModificationDate = lastModificationDate;
      return this;
    }

    /**
     * Sets the extracted text content. Blank and empty values are normalized to {@code null}.
     *
     * @param extractedContent the extracted text, may be {@code null} or blank
     * @return this builder
     */
    @SuppressWarnings("PMD.NullAssignment")
    public Builder extractedContent(String extractedContent) {
      if ((extractedContent == null) || extractedContent.isBlank()) {
        this.extractedContent = null;
      } else {
        this.extractedContent = extractedContent;
      }
      return this;
    }

    /**
     * Builds a new {@link DocInfo} from the current builder state.
     *
     * @return a new {@link DocInfo} instance
     */
    public DocInfo build() {
      return new DocInfo(this);
    }
  }
}
