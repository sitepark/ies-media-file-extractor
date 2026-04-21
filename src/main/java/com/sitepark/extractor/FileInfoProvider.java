package com.sitepark.extractor;

import java.nio.file.Path;
import java.util.Set;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;

/**
 * Strategy interface for creating {@link FileInfo} instances from Tika parsing results.
 *
 * <p>Implementations handle a specific set of MIME types and transform the raw Tika {@link
 * Metadata} and extracted text into a typed {@link FileInfo} subclass.
 *
 * @param <T> the concrete {@link FileInfo} type produced by this factory
 */
public interface FileInfoProvider<T extends FileInfo> {

  /**
   * Returns the set of MIME types handled by this factory.
   *
   * @return an unmodifiable set of supported {@link MediaType} values; never {@code null}
   */
  Set<MediaType> getSupportedTypes();

  /**
   * Creates a {@link FileInfo} from the given Tika metadata and extracted text content.
   *
   * @param metadata the Tika metadata produced during parsing; must not be {@code null}
   * @param extractedContent the plain-text content extracted from the file, may be {@code null}
   * @return a new {@link FileInfo} instance; never {@code null}
   */
  T create(Path path, Metadata metadata, String extractedContent) throws ExtractionException;
}
