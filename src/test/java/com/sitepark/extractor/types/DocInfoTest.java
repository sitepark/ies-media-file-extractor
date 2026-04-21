package com.sitepark.extractor.types;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.TooManyMethods"})
class DocInfoTest {

  @Test
  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  void testEqualsContract() {
    EqualsVerifier.forClass(DocInfo.class).verify();
  }

  @Test
  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  void testToString() {
    ToStringVerifier.forClass(DocInfo.class).withClassName(NameStyle.SIMPLE_NAME).verify();
  }

  @Test
  void testTitle() {
    DocInfo docInfo = DocInfo.builder().title("title").build();
    assertEquals("title", docInfo.title(), "unexpected title");
  }

  @Test
  void testDescription() {
    DocInfo docInfo = DocInfo.builder().description("description").build();
    assertEquals("description", docInfo.description(), "unexpected description");
  }

  @Test
  void testCreationDate() {
    DocInfo docInfo = DocInfo.builder().creationDate(123L).build();
    assertEquals(123L, docInfo.creationDate(), "unexpected creationDate");
  }

  @Test
  void testLastModificationDate() {
    DocInfo docInfo = DocInfo.builder().lastModificationDate(123L).build();
    assertEquals(123L, docInfo.lastModificationDate(), "unexpected lastModificationDate");
  }

  @Test
  void testExtractedContent() {
    DocInfo docInfo = DocInfo.builder().extractedContent("abc").build();
    assertEquals("abc", docInfo.extractedContent(), "unexpected extractedContent");
  }

  @Test
  void testSetNullExtractedContent() {
    DocInfo docInfo = DocInfo.builder().extractedContent(null).build();
    assertNull(docInfo.extractedContent(), "extractedContent should be null");
  }

  @Test
  void testSetBlankExtractedContent() {
    DocInfo docInfo = DocInfo.builder().extractedContent(" ").build();
    assertNull(docInfo.extractedContent(), "extractedContent should be null");
  }

  @Test
  void testWithBlankExtractedContent() {
    DocInfo docInfo = DocInfo.builder().extractedContent("").build();
    assertNull(docInfo.extractedContent(), "extractedContent should be null");
  }

  @Test
  void testToBuilder() {
    DocInfo docInfo =
        DocInfo.builder()
            .title("title")
            .description("description")
            .creationDate(123L)
            .lastModificationDate(123L)
            .build();

    DocInfo copy = docInfo.toBuilder().description("description2").build();

    DocInfo expected =
        DocInfo.builder()
            .title("title")
            .description("description2")
            .creationDate(123L)
            .lastModificationDate(123L)
            .build();

    assertEquals(expected, copy, "unexpected doc-info copy");
  }
}
