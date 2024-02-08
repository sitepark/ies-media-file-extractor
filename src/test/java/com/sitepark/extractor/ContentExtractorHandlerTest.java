package com.sitepark.extractor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringWriter;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

class ContentExtractorHandlerTest {

  @Test
  void testWrite() throws SAXException {
    ContentExtractorHandler handler = new ContentExtractorHandler(new StringWriter(), 100);
    String s = "abcdefghijk";
    handler.characters(s.toCharArray(), 0, s.length());

    assertEquals(s, handler.toString(), "unexpected content");
  }

  @Test
  void testWriteLeadingWhitespaces() throws SAXException {
    ContentExtractorHandler handler = new ContentExtractorHandler(new StringWriter(), 100);
    String s = "   ";
    handler.ignorableWhitespace(s.toCharArray(), 0, s.length());

    assertTrue(handler.toString().isEmpty(), "content should be empty");
  }

  @Test
  void testWriteWhitespacesAfterText() throws SAXException {
    ContentExtractorHandler handler = new ContentExtractorHandler(new StringWriter(), 100);
    String s = "abcdefghijk";
    handler.characters(s.toCharArray(), 0, s.length());
    String ws = "   ";
    handler.ignorableWhitespace(ws.toCharArray(), 0, ws.length());

    assertEquals(s + ws, handler.toString(), "unexpected content");
  }

  @Test
  void testToTinyString() throws SAXException {
    ContentExtractorHandler handler = new ContentExtractorHandler(new StringWriter(), 100);
    String s = "  \n  abcdefghijk\n\t  abc\ncde\n ";
    handler.characters(s.toCharArray(), 0, s.length());

    assertEquals(
        "abcdefghijk abc cde", handler.toTidyString(), "content should be cleaned of whitespaces");
  }

  @Test
  void testWriteLimit() throws SAXException {
    ContentExtractorHandler handler = new ContentExtractorHandler(new StringWriter(), 10);
    String s = "12345678901";
    handler.characters(s.toCharArray(), 0, s.length());

    assertEquals("1234567890", handler.toString(), "content should be trancated");
  }

  @Test
  void testWriteLimitReached() throws SAXException {
    ContentExtractorHandler handler = new ContentExtractorHandler(new StringWriter(), 10);
    String s = "12345678901";
    handler.characters(s.toCharArray(), 0, s.length());
    handler.characters(s.toCharArray(), 0, s.length());

    assertEquals("1234567890", handler.toString(), "content should be trancated");
  }

  @Test
  void testWriteUnlimited() throws SAXException {
    ContentExtractorHandler handler = new ContentExtractorHandler(new StringWriter(), -1);
    String s = "1234567890";

    int iterations = 3;

    for (int i = 0; i < iterations; i++) {
      handler.characters(s.toCharArray(), 0, s.length());
    }

    assertEquals(
        iterations * s.length(), handler.toString().length(), "content should not be trancated");
  }
}
