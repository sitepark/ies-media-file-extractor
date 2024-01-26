package com.sitepark.extractor;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

class ContentTypeDetectorTest {

  @Test
  void testDetect() throws IOException {

    ContentTypeDetector detector = new ContentTypeDetector();

    Path path = Paths.get("src/test/resources/files/docs/Sample.pdf");

    String contentType = detector.detect(path);

    assertEquals("application/pdf", contentType, "unexpected content-type");
  }
}
