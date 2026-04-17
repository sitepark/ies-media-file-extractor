package com.sitepark.extractor;

/** Thrown when a media file cannot be parsed or its content cannot be extracted. */
public class ExtractionException extends Exception {

  private static final long serialVersionUID = -2475991392211002674L;

  /**
   * Creates a new {@code ExtractionException} with the given message.
   *
   * @param msg a description of the extraction failure
   */
  public ExtractionException(String msg) {
    super(msg);
  }

  /**
   * Creates a new {@code ExtractionException} with the given message and cause.
   *
   * @param msg a description of the extraction failure
   * @param t the underlying cause
   */
  public ExtractionException(String msg, Throwable t) {
    super(msg, t);
  }
}
