package com.sitepark.extractor;

import com.sitepark.extractor.provider.doc.DocInfoProvider;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.apache.tika.exception.EncryptedDocumentException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.xml.sax.SAXException;

/**
 * Extracts metadata and text content from media files using Apache Tika.
 *
 * <p>By default, supports PDF, Microsoft Office, OpenDocument and RTF formats via {@link
 * DocInfoProvider}. Register additional {@link FileInfoProvider} implementations via the
 * constructors to extend format support.
 *
 * <p>This class is not thread-safe due to the mutable {@code defaultWriteLimit} field.
 */
public class Extractor {

  private final List<FileInfoProvider<?>> providers = new ArrayList<>();

  private final Parser parser;

  private int defaultWriteLimit = 100 * 1000;

  private final Set<MediaType> supportedMediaTypes = new HashSet<>();

  private static final Parser DEFAULT_PARSER = new AutoDetectParser();

  /** Creates an {@code Extractor} with the default set of supported formats. */
  public Extractor() {
    this(DefaultProviderFactory.createProviders());
  }

  /**
   * Sets the default maximum number of characters to extract per document.
   *
   * @param defaultWriteLimit the character limit; must be greater than 0
   * @throws IllegalArgumentException if {@code defaultWriteLimit} is not greater than 0
   */
  public void setDefaultWriteLimit(int defaultWriteLimit) {
    if (defaultWriteLimit <= 0) {
      throw new IllegalArgumentException("defaultWriteLimit must be greater than 0");
    }
    this.defaultWriteLimit = defaultWriteLimit;
  }

  /**
   * Creates an {@code Extractor} with a custom set of {@link FileInfoProvider} implementations.
   *
   * @param providers one or more factories that handle specific media types
   */
  public Extractor(List<FileInfoProvider<?>> providers) {
    this(DEFAULT_PARSER, providers);
  }

  /**
   * Creates an {@code Extractor} with a custom Tika {@link Parser} and a custom set of {@link
   * FileInfoProvider} implementations.
   *
   * @param parser the Tika parser to use for content extraction
   * @param providers one or more factories that handle specific media types
   */
  public Extractor(Parser parser, List<FileInfoProvider<?>> providers) {
    this.parser = parser;
    for (FileInfoProvider<?> provider : providers) {
      this.addProvider(provider);
    }
  }

  private void addProvider(FileInfoProvider<?> provider) {
    this.providers.add(provider);
    this.supportedMediaTypes.addAll(provider.getSupportedTypes());
  }

  /**
   * Returns {@code true} if the given MIME type is supported by one of the registered factories.
   *
   * @param mediaType the MIME type string to check, e.g. {@code "application/pdf"}
   * @return {@code true} if the MIME type is supported, {@code false} otherwise
   * @throws NullPointerException if {@code mediaType} is {@code null}
   */
  public boolean isSupported(String mediaType) {
    Objects.requireNonNull(mediaType, "mediaType must not be null");
    MediaType type = MediaType.parse(mediaType);
    return this.supportedMediaTypes.contains(type);
  }

  /**
   * Extracts metadata and text content from the file at the given path using the default write
   * limit.
   *
   * @param path the path to the file to extract
   * @return the extracted {@link FileInfo}; never {@code null}
   * @throws ExtractionException if extraction fails or the media type is not supported
   * @throws NullPointerException if {@code path} is {@code null}
   */
  public FileInfo extract(Path path) throws ExtractionException {
    return this.extract(path, this.defaultWriteLimit);
  }

  /**
   * Extracts metadata and text content from the file at the given path with a custom character
   * limit for the extracted text.
   *
   * @param path the path to the file to extract
   * @param writeLimit the maximum number of characters to extract; use {@code -1} for no limit
   * @return the extracted {@link FileInfo}; never {@code null}
   * @throws ExtractionException if extraction fails or the media type is not supported
   * @throws NullPointerException if {@code path} is {@code null}
   */
  public FileInfo extract(Path path, int writeLimit) throws ExtractionException {
    Objects.requireNonNull(path, "path must not be null");

    Metadata metadata = new Metadata();

    ParseContext context = new ParseContext();

    ContentExtractorHandler handler = this.createContentHandler(writeLimit);

    try (InputStream inputstream = this.createInputStream(path, metadata); ) {
      this.parser.parse(inputstream, handler, metadata, context);
    } catch (EncryptedDocumentException e) { // NOPMD: EmptyCatchBlock
      /*
       * If the document is encrypted we take the data we can get.
       * That should be enough for us.
       */
    } catch (IOException | SAXException | TikaException e) {
      throw new ExtractionException(path + ": extraction failed", e);
    }

    return this.toFileInfo(path, metadata, handler);
  }

  private ContentExtractorHandler createContentHandler(int writeLimit) {
    return new ContentExtractorHandler(new StringWriter(), writeLimit);
  }

  protected InputStream createInputStream(Path path, Metadata metadata) throws IOException {
    return TikaInputStream.get(path, metadata);
  }

  private FileInfo toFileInfo(Path path, Metadata metadata, ContentExtractorHandler handler)
      throws ExtractionException, UnsupportedMediaTypeException {

    MediaType mediaType = MediaType.parse(metadata.get("Content-Type"));
    if (mediaType == null) {
      throw new ExtractionException("Content-Type not set in metadata");
    }

    FileInfoProvider<?> provider = this.findProvider(mediaType);
    String extractedContent = handler.toTidyString();
    return provider.create(path, metadata, extractedContent);
  }

  private FileInfoProvider<?> findProvider(MediaType mediaType)
      throws UnsupportedMediaTypeException {

    for (FileInfoProvider<?> provider : this.providers) {
      if (provider.getSupportedTypes().contains(mediaType)) {
        return provider;
      }
    }

    throw new UnsupportedMediaTypeException("No provider for mediaType " + mediaType);
  }
}
