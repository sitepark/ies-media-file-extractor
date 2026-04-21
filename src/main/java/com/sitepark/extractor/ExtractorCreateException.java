package com.sitepark.extractor;

import java.io.Serial;

public class ExtractorCreateException extends RuntimeException {

  @Serial private static final long serialVersionUID = 1L;

  /**
   * Creates a new {@code ExtractorCreateException} with the given message.
   *
   * @param msg a description of the extraction failure
   */
  public ExtractorCreateException(String msg) {
    super(msg);
  }

  /**
   * Creates a new {@code ExtractorCreateException} with the given message and cause.
   *
   * @param msg a description of the extraction failure
   * @param t the underlying cause
   */
  public ExtractorCreateException(String msg, Throwable t) {
    super(msg, t);
  }
}
