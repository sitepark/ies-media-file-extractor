package com.sitepark.extractor.types;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sitepark.extractor.FileInfo;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Objects;

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

  public String getTitle() {
    return this.title;
  }

  public String getDescription() {
    return this.description;
  }

  public Long getCreationDate() {
    return this.creationDate;
  }

  public Long getLastModificationDate() {
    return this.lastModificationDate;
  }

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

  public static Builder builder() {
    return new Builder();
  }

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

  @JsonPOJOBuilder(withPrefix = "", buildMethodName = "build")
  public static final class Builder {

    private String title;

    private String description;

    private Long creationDate;

    private Long lastModificationDate;

    private String extractedContent;

    @SuppressFBWarnings("PI_DO_NOT_REUSE_PUBLIC_IDENTIFIERS_CLASS_NAMES")
    private Builder() {}

    private Builder(DocInfo docInfo) {
      this.title = docInfo.title;
      this.description = docInfo.description;
      this.creationDate = docInfo.creationDate;
      this.lastModificationDate = docInfo.lastModificationDate;
      this.extractedContent = docInfo.extractedContent;
    }

    public Builder title(String title) {
      this.title = title;
      return this;
    }

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    public Builder creationDate(Long creationDate) {
      this.creationDate = creationDate;
      return this;
    }

    public Builder lastModificationDate(Long lastModificationDate) {
      this.lastModificationDate = lastModificationDate;
      return this;
    }

    @SuppressWarnings("PMD.NullAssignment")
    public Builder extractedContent(String extractedContent) {
      if ((extractedContent == null) || extractedContent.isBlank()) {
        this.extractedContent = null;
      } else {
        this.extractedContent = extractedContent;
      }
      return this;
    }

    public DocInfo build() {
      return new DocInfo(this);
    }
  }
}
