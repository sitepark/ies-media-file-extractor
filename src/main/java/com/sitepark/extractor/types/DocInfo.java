package com.sitepark.extractor.types;

import java.util.Objects;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sitepark.extractor.FileInfo;

@JsonDeserialize(builder = DocInfo.Builder.class)
public final class DocInfo extends FileInfo {

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
		return title;
	}

	public String getDescription() {
		return description;
	}

	public Long getCreationDate() {
		return creationDate;
	}

	public Long getLastModificationDate() {
		return lastModificationDate;
	}

	public String getExtractedContent() {
		return extractedContent;
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

		StringBuilder b = new StringBuilder(50)
				.append("DocInfo[")
				.append("title:")
				.append(this.title)
				.append(", ")
				.append("description:")
				.append(this.description)
				.append(", ")
				.append("creationDate:")
				.append(this.creationDate)
				.append(", ")
				.append("lastModificationDate:")
				.append(this.lastModificationDate)
				.append(", ")
				.append("extractedContent:")
				.append(this.extractedContent)
				.append(']');
		return b.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof DocInfo)) {
			return false;
		}
		DocInfo info = (DocInfo)o;
		return
				Objects.equals(info.getTitle(), this.title) &&
				Objects.equals(info.getDescription(), this.description) &&
				Objects.equals(info.getCreationDate(), this.creationDate) &&
				Objects.equals(info.getLastModificationDate(), this.lastModificationDate) &&
				Objects.equals(info.getExtractedContent(), this.extractedContent);
	}

	@JsonPOJOBuilder(withPrefix = "", buildMethodName = "build")
	public final static class Builder {

		private String title;

		private String description;

		private Long creationDate;

		private Long lastModificationDate;

		private String extractedContent;

		private Builder() {
		}

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

		public Builder extractedContent(String extractedContent) {
			if (extractedContent != null && extractedContent.isBlank()) {
				extractedContent = null;
			}
			this.extractedContent = extractedContent;
			return this;
		}

		public DocInfo build() {
			return new DocInfo(this);
		}
	}
}
