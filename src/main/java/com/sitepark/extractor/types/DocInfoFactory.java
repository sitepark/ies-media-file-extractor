package com.sitepark.extractor.types;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MediaType;

import com.sitepark.extractor.FileInfoFactory;

public class DocInfoFactory implements FileInfoFactory<DocInfo> {

	private static final Set<MediaType> SUPPORTED_TYPES =
			Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
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
					MediaType.application("vnd.openxmlformats-officedocument.wordprocessingml.document"),
					MediaType.application("vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
					MediaType.application("vnd.openxmlformats-officedocument.presentationml.presentation"),
					// Compatibility
					MediaType.application("vnd.openxmlformats-officedocument.wordprocessingml"),
					MediaType.application("vnd.openxmlformats-officedocument.spreadsheetml"),
					MediaType.application("vnd.openxmlformats-officedocument.presentationml")
	)));

	public static boolean isSupported(String type) {
		MediaType mediaType = MediaType.parse(type);
		return SUPPORTED_TYPES.contains(mediaType);
	}

	@Override
	public Set<MediaType> getSupportedTypes() {
		return SUPPORTED_TYPES;
	}

	@Override
	public DocInfo create(Metadata metadata, String extractedContent) {
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
