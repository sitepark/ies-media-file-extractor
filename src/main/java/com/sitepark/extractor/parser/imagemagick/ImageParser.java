package com.sitepark.extractor.parser.imagemagick;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.CompositeParser;
import org.apache.tika.parser.ParseContext;
import org.xml.sax.SAXException;

public class ImageParser extends CompositeParser {

	private static final long serialVersionUID = 1L;

	private static final Set<MediaType> SUPPORTED_TYPES =
			Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
							MediaType.image("bmp"),
							MediaType.image("gif"),
							MediaType.image("jpeg"),
							MediaType.image("png"),
							MediaType.image("svg+xml"),
							MediaType.image("tiff"),
							MediaType.image("vnd.adobe.photoshop"),
							MediaType.image("webp"),
							MediaType.image("wmf"),
							MediaType.image("vnd.microsoft.icon"),
							MediaType.image("x-icon"),
							MediaType.image("x-paintshoppro"),
							MediaType.image("image/xcf")
	)));

	public Set<MediaType> getSupportedTypes(ParseContext context) {
			return Collections.emptySet();
			//return SUPPORTED_TYPES;
	}

	@Override
	public void parse(InputStream stream, org.xml.sax.ContentHandler handler, Metadata metadata, ParseContext context)
			throws IOException, SAXException, TikaException {
		metadata.set(Metadata.CONTENT_TYPE, "image/abc");
		metadata.set("Hello", "World");

	}
}
