package com.sitepark.extractor;

/**
 * Thrown when no registered {@link FileInfoFactory} supports the detected MIME type of a file.
 */
public class UnsupportedMediaTypeException extends ExtractionException {

  private static final long serialVersionUID = -7056186077764640757L;

  /**
   * Creates a new {@code UnsupportedMediaTypeException} with the given message.
   *
   * @param msg a description identifying the unsupported media type
   */
  public UnsupportedMediaTypeException(String msg) {
    super(msg);
  }
}
