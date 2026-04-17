package com.sitepark.extractor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;

/** Detects the MIME type of a file using Apache Tika's {@link DefaultDetector}. */
public class ContentTypeDetector {

  /**
   * Detects and returns the MIME type of the file at the given path.
   *
   * @param path the path to the file whose MIME type should be detected
   * @return the detected MIME type string, e.g. {@code "application/pdf"}
   * @throws IOException if the file cannot be read
   * @throws NullPointerException if {@code path} is {@code null}
   */
  public String detect(Path path) throws IOException {
    Objects.requireNonNull(path, "path must not be null");
    Metadata metadata = new Metadata();
    Detector detector = new DefaultDetector();
    MediaType type = detector.detect(TikaInputStream.get(path, metadata), metadata);
    return type.toString();
  }
}
