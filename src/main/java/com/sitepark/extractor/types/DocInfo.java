package com.sitepark.extractor.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sitepark.extractor.FileInfo;
import com.sitepark.extractor.MediaType;
import java.io.Serial;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

/**
 * Immutable value object representing document metadata and extracted text content.
 *
 * <p>Use {@link #builder()} to construct instances and {@link #toBuilder()} to create modified
 * copies. Supports JSON serialization and deserialization via Jackson.
 */
// TooManyMethods: value object with one accessor per property plus builder
@SuppressWarnings({"PMD.AvoidFieldNameMatchingMethodName", "PMD.TooManyMethods"})
@JsonDeserialize(builder = DocInfo.Builder.class)
public final class DocInfo extends FileInfo {

  @Serial private static final long serialVersionUID = 1L;

  private final @Nullable MediaType mediaType;

  private final @Nullable String title;

  private final @Nullable String description;

  private final @Nullable Long creationDate;

  private final @Nullable Long lastModificationDate;

  private final @Nullable String extractedContent;

  // LawOfDemeter: copying the builder state is the purpose of this constructor
  @SuppressWarnings("PMD.LawOfDemeter")
  private DocInfo(Builder builder) {
    this.mediaType = builder.mediaType;
    this.title = builder.title;
    this.description = builder.description;
    this.creationDate = builder.creationDate;
    this.lastModificationDate = builder.lastModificationDate;
    this.extractedContent = builder.extractedContent;
  }

  /**
   * Returns the media type, or {@code null} if not set.
   *
   * @return the media type, or {@code null}
   */
  @JsonProperty
  public @Nullable MediaType mediaType() {
    return this.mediaType;
  }

  /**
   * Returns the document title, or {@code null} if not set.
   *
   * @return the title, or {@code null}
   */
  @JsonProperty
  public @Nullable String title() {
    return this.title;
  }

  /**
   * Returns the document description, or {@code null} if not set.
   *
   * @return the description, or {@code null}
   */
  @JsonProperty
  public @Nullable String description() {
    return this.description;
  }

  /**
   * Returns the document creation date as milliseconds since the Unix epoch, or {@code null} if not
   * set.
   *
   * @return the creation date in epoch milliseconds, or {@code null}
   */
  @JsonProperty
  public @Nullable Long creationDate() {
    return this.creationDate;
  }

  /**
   * Returns the document last-modification date as milliseconds since the Unix epoch, or {@code
   * null} if not set.
   *
   * @return the last-modification date in epoch milliseconds, or {@code null}
   */
  @JsonProperty
  public @Nullable Long lastModificationDate() {
    return this.lastModificationDate;
  }

  /**
   * Returns the plain-text content extracted from the document, or {@code null} if no content was
   * extracted or the content was blank.
   *
   * @return the extracted content, or {@code null}
   */
  @JsonProperty
  public @Nullable String extractedContent() {
    return this.extractedContent;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        this.mediaType,
        this.title,
        this.description,
        this.creationDate,
        this.lastModificationDate,
        this.extractedContent);
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
    return "DocInfo [mediaType="
        + this.mediaType
        + ", title="
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
    return Objects.equals(that.mediaType(), this.mediaType)
        && Objects.equals(that.title(), this.title)
        && Objects.equals(that.description(), this.description)
        && Objects.equals(that.creationDate(), this.creationDate)
        && Objects.equals(that.lastModificationDate(), this.lastModificationDate)
        && Objects.equals(that.extractedContent(), this.extractedContent);
  }

  /** Builder for {@link DocInfo}. */
  @JsonPOJOBuilder(withPrefix = "", buildMethodName = "build")
  public static final class Builder {

    private @Nullable MediaType mediaType;

    private @Nullable String title;

    private @Nullable String description;

    private @Nullable Long creationDate;

    private @Nullable Long lastModificationDate;

    private @Nullable String extractedContent;

    private Builder() {}

    // LawOfDemeter: copying the value object state is the purpose of this constructor
    @SuppressWarnings("PMD.LawOfDemeter")
    private Builder(DocInfo docInfo) {
      this.mediaType = docInfo.mediaType;
      this.title = docInfo.title;
      this.description = docInfo.description;
      this.creationDate = docInfo.creationDate;
      this.lastModificationDate = docInfo.lastModificationDate;
      this.extractedContent = docInfo.extractedContent;
    }

    /**
     * Sets the media type.
     *
     * @param mediaType the media type, may be {@code null}
     * @return this builder
     */
    public Builder mediaType(@Nullable MediaType mediaType) {
      this.mediaType = mediaType;
      return this;
    }

    /**
     * Sets the document title.
     *
     * @param title the title, may be {@code null}
     * @return this builder
     */
    public Builder title(@Nullable String title) {
      this.title = title;
      return this;
    }

    /**
     * Sets the document description.
     *
     * @param description the description, may be {@code null}
     * @return this builder
     */
    public Builder description(@Nullable String description) {
      this.description = description;
      return this;
    }

    /**
     * Sets the document creation date.
     *
     * @param creationDate the creation date as milliseconds since the Unix epoch, may be {@code
     *     null}
     * @return this builder
     */
    public Builder creationDate(@Nullable Long creationDate) {
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
    public Builder lastModificationDate(@Nullable Long lastModificationDate) {
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
    public Builder extractedContent(@Nullable String extractedContent) {
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
