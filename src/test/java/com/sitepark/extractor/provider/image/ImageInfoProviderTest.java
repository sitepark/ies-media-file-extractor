package com.sitepark.extractor.provider.image;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.sitepark.extractor.ExtractionException;
import com.sitepark.extractor.types.ImageInfo;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.file.Path;
import org.apache.tika.metadata.Metadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ImageInfoProviderTest {

  private ComDrewImageMetadataReader comDrewImageMetadataReader;
  private VipsExtractor vipsExtractor;
  private ImageInfoProvider provider;

  @BeforeEach
  void setUp() {
    this.comDrewImageMetadataReader = mock();
    this.vipsExtractor = mock();
    this.provider = new ImageInfoProvider(this.comDrewImageMetadataReader, this.vipsExtractor);
  }

  @Test
  void testIsSupported() {
    assertTrue(ImageInfoProvider.isSupported("image/jpeg"), "jpeg should be supported");
  }

  @Test
  void testIsNotSupported() {
    assertFalse(ImageInfoProvider.isSupported("application/pdf"), "pdf should not be supported");
  }

  @Test
  @SuppressFBWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
  void testIsSupportedWithNullType() {
    assertThrows(
        NullPointerException.class,
        () -> ImageInfoProvider.isSupported(null),
        "null type should throw NullPointerException");
  }

  @Test
  void testCreateCallsMetadataReader() throws ExtractionException {
    Path path = Path.of("test.jpg");
    this.provider.create(path, new Metadata(), null);
    verify(this.comDrewImageMetadataReader).applyData(eq(path), any(ImageInfo.Builder.class));
  }

  @Test
  @SuppressFBWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
  void testCreateWithNullMetadata() {
    assertThrows(
        NullPointerException.class,
        () -> this.provider.create(Path.of("test.jpg"), null, null),
        "null metadata should throw NullPointerException");
  }
}
