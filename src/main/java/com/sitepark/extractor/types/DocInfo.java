package com.sitepark.extractor.types;

import com.sitepark.extractor.FileInfo;

public final class DocInfo extends FileInfo {

	private final String title;

	private final String description;

	private final Long creationDate;

	private final Long lastModificationDate;

	private final String extractedContent;

	protected DocInfo(
			String title,
			String description,
			Long creationDate,
			Long lastModificationDate,
			String extractedContent
	) {
		this.title = title;
		this.description = description;
		this.creationDate = creationDate;
		this.lastModificationDate = lastModificationDate;
		this.extractedContent = extractedContent;
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
}
