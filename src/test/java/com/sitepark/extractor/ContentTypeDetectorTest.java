package com.sitepark.extractor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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

  @Test
  @SuppressFBWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
  void testDetectWithNullPath() {
    ContentTypeDetector detector = new ContentTypeDetector();
    assertThrows(
        NullPointerException.class,
        () -> detector.detect(null),
        "null path should throw NullPointerException");
  }
}
