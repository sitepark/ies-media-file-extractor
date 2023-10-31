package com.sitepark.extractor;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;

import com.sitepark.extractor.types.DocInfoFactory;

public class Extractor {

	private final List<FileInfoFactory<?>> factoryList = new ArrayList<>();

	private final Set<MediaType> supportedMediaTypes = new HashSet<>();

	public Extractor() {
		this.addFileInfoFactory(new DocInfoFactory());
	}

	private void addFileInfoFactory(FileInfoFactory<?> factory) {
		this.factoryList.add(factory);
		this.supportedMediaTypes.addAll(factory.getSupportedTypes());
	}

	public boolean isSupported(String mediaType) {
		MediaType type = MediaType.parse(mediaType);
		return this.supportedMediaTypes.contains(type);
	}

	public FileInfo extract(Path path) throws ExtractionException {

		Metadata metadata = new Metadata();
		Parser parser = new AutoDetectParser();
		BodyContentHandler handler = new BodyContentHandler();
		ParseContext context = new ParseContext();

		try {
			InputStream inputstream = Files.newInputStream(path);
			parser.parse(inputstream, handler, metadata, context);
		} catch (Throwable t) {
			throw new ExtractionException(t.getMessage(), t);
		}

		return this.toFileInfo(metadata, handler);
	}

	private FileInfo toFileInfo(Metadata metadata, BodyContentHandler handler)
			throws ExtractionException {

		MediaType mediaType = MediaType.parse(metadata.get("Content-Type"));
		if (mediaType == null) {
			throw new ExtractionException("Content-Type not set in metadata");
		}

		FileInfoFactory<?> factory = this.getFileInfoFactory(mediaType);
		return factory.create(metadata, handler.toString());
	}

	private FileInfoFactory<?> getFileInfoFactory(MediaType mediaType)
			throws ExtractionException {

		for (FileInfoFactory<?> factory : this.factoryList) {
			if (factory.getSupportedTypes().contains(mediaType)) {
				return factory;
			}
		}

		throw new ExtractionException("No factory for mediaType " + mediaType);
	}
}
