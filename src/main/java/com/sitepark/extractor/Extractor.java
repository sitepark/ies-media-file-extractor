package com.sitepark.extractor;

import com.sitepark.extractor.types.DocInfoFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.tika.exception.EncryptedDocumentException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;

public class Extractor {

  private final List<FileInfoFactory<?>> factoryList = new ArrayList<>();

  private final Parser parser;

  private int defaultWriteLimit = 100 * 1000;

  private final Set<MediaType> supportedMediaTypes = new HashSet<>();

  private static final FileInfoFactory<?>[] DEFAULT_FACTORY_LIST =
      new FileInfoFactory<?>[] {new DocInfoFactory()};

  private static final Parser DEFAULT_PARSER = new AutoDetectParser();

  public Extractor() {
    this(DEFAULT_FACTORY_LIST);
  }

  public void setDefaultWriteLimit(int defaultWriteLimit) {
    if (defaultWriteLimit <= 0) {
      throw new IllegalArgumentException("defaultWriteLimit must be greater than 0");
    }
    this.defaultWriteLimit = defaultWriteLimit;
  }

  public Extractor(FileInfoFactory<?>... factoryList) {
    this(DEFAULT_PARSER, factoryList);
  }

  public Extractor(Parser parser, FileInfoFactory<?>... factoryList) {
    this.parser = parser;
    for (FileInfoFactory<?> factory : factoryList) {
      this.addFileInfoFactory(factory);
    }
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
    return this.extract(path, this.defaultWriteLimit);
  }

  public FileInfo extract(Path path, int writeLimit) throws ExtractionException {

    Metadata metadata = new Metadata();

    ParseContext context = new ParseContext();

    ContentExtractorHandler handler = this.createContentHandler(writeLimit);

    try (InputStream inputstream = this.createInputStream(path, metadata); ) {
      this.parser.parse(inputstream, handler, metadata, context);
    } catch (EncryptedDocumentException e) { // NOPMD
      /*
       * If the document is encrypted we take the data we can get.
       * That should be enough for us.
       */
    } catch (Throwable t) {
      throw new ExtractionException(path + ": extraction failed", t);
    }

    return this.toFileInfo(metadata, handler);
  }

  private ContentExtractorHandler createContentHandler(int writeLimit) {
    return new ContentExtractorHandler(new StringWriter(), writeLimit);
  }

  protected InputStream createInputStream(Path path, Metadata metadata) throws IOException {
    return TikaInputStream.get(path, metadata);
  }

  private FileInfo toFileInfo(Metadata metadata, ContentExtractorHandler handler)
      throws ExtractionException, UnsupportedMediaTypeException {

    MediaType mediaType = MediaType.parse(metadata.get("Content-Type"));
    if (mediaType == null) {
      throw new ExtractionException("Content-Type not set in metadata");
    }

    FileInfoFactory<?> factory = this.getFileInfoFactory(mediaType);
    String extractedContent = handler.toTidyString();
    return factory.create(metadata, extractedContent);
  }

  private FileInfoFactory<?> getFileInfoFactory(MediaType mediaType)
      throws UnsupportedMediaTypeException {

    for (FileInfoFactory<?> factory : this.factoryList) {
      if (factory.getSupportedTypes().contains(mediaType)) {
        return factory;
      }
    }

    throw new UnsupportedMediaTypeException("No factory for mediaType " + mediaType);
  }
}
