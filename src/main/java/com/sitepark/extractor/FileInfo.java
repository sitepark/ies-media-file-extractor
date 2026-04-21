package com.sitepark.extractor;

import java.io.Serial;
import java.io.Serializable;

/**
 * Abstract base class for all extraction result types.
 *
 * <p>Subclasses hold the metadata and text content extracted from a specific category of media
 * file. All implementations are immutable and serializable.
 */
public abstract class FileInfo implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  protected FileInfo() {
    // protect the default constructor
  }
}
