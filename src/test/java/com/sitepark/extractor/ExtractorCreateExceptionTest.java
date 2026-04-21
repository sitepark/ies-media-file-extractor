package com.sitepark.extractor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class ExtractorCreateExceptionTest {

  @Test
  void testMessage() {
    ExtractorCreateException e = new ExtractorCreateException("test message");
    assertEquals("test message", e.getMessage(), "unexpected exception message");
  }

  @Test
  void testCause() {
    Throwable cause = new RuntimeException("root cause");
    ExtractorCreateException e = new ExtractorCreateException("msg", cause);
    assertSame(cause, e.getCause(), "unexpected exception cause");
  }
}
