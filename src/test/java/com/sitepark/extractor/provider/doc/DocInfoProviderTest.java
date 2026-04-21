package com.sitepark.extractor.provider.doc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sitepark.extractor.ExtractionException;
import com.sitepark.extractor.types.DocInfo;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.file.Path;
import java.util.Date;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.metadata.TikaCoreProperties;
import org.junit.jupiter.api.Test;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class DocInfoProviderTest {

  private final DocInfoProvider factory = new DocInfoProvider();

  @Test
  void testIsSupported() {
    assertTrue(DocInfoProvider.isSupported("application/pdf"), "pdf should be supported");
  }

  @Test
  void testIsNotSupported() {
    assertFalse(DocInfoProvider.isSupported("image/gif"), "gif should not be supported");
  }

  @Test
  @SuppressFBWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
  void testIsSupportedWithNullType() {
    assertThrows(
        NullPointerException.class,
        () -> DocInfoProvider.isSupported(null),
        "null type should throw NullPointerException");
  }

  @Test
  void testWithoutTitle() throws ExtractionException {
    DocInfo info = this.factory.create(Path.of(""), new Metadata(), null);
    assertNull(info.title(), "title must be null");
  }

  @Test
  void testWithMissingTitle() throws ExtractionException {
    Metadata metadata = this.createMetadata(TikaCoreProperties.TITLE, "test");
    DocInfo info = this.factory.create(Path.of(""), metadata, null);
    assertEquals("test", info.title(), "unexpected title");
  }

  @Test
  void testWithDescription() throws ExtractionException {
    Metadata metadata = this.createMetadata(TikaCoreProperties.DESCRIPTION, "test");
    DocInfo info = this.factory.create(Path.of(""), metadata, null);
    assertEquals("test", info.description(), "unexpected description");
  }

  @Test
  void testWithCreationDate() throws ExtractionException {
    Date date = new Date(123000L);
    Metadata metadata = this.createMetadata(TikaCoreProperties.CREATED, date);
    DocInfo info = this.factory.create(Path.of(""), metadata, null);
    assertEquals(123000L, info.creationDate(), "unexpected creationDate");
  }

  @Test
  void testWithLastModifiedDate() throws ExtractionException {
    Date date = new Date(123000L);
    Metadata metadata = this.createMetadata(TikaCoreProperties.MODIFIED, date);
    DocInfo info = this.factory.create(Path.of(""), metadata, null);
    assertEquals(123000L, info.lastModificationDate(), "unexpected lastModifiedDate");
  }

  @Test
  @SuppressFBWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
  void testCreateWithNullMetadata() {
    assertThrows(
        NullPointerException.class,
        () -> this.factory.create(null, null, null),
        "null metadata should throw NullPointerException");
  }

  private Metadata createMetadata(Property name, String value) {
    Metadata metadata = new Metadata();
    metadata.set(name, value);
    return metadata;
  }

  private Metadata createMetadata(Property name, Date date) {
    Metadata metadata = new Metadata();
    metadata.set(name, date);
    return metadata;
  }
}
