package com.sitepark.extractor;

import java.util.Set;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;

public interface FileInfoFactory<T extends FileInfo> {

	Set<MediaType> getSupportedTypes();

	T create(Metadata metadata, String extractedContent);
}
