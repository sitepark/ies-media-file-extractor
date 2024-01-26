package com.sitepark.extractor;

import java.io.IOException;
import java.nio.file.Path;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;

public class ContentTypeDetector {

  public String detect(Path path) throws IOException {
    Metadata metadata = new Metadata();
    Detector detector = new DefaultDetector();
    MediaType type = detector.detect(TikaInputStream.get(path, metadata), metadata);
    return type.toString();
  }
}
