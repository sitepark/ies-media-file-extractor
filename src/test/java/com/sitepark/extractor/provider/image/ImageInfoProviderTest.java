package com.sitepark.extractor.provider.image;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.sitepark.extractor.ExtractionException;
import com.sitepark.extractor.MediaType;
import com.sitepark.extractor.types.ImageInfo;
import java.nio.file.Path;
import org.apache.tika.metadata.Metadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ImageInfoProviderTest {

  private ComDrewImageMetadataReader comDrewImageMetadataReader;
  private ImageInfoProvider provider;

  @BeforeEach
  void setUp() {
    this.comDrewImageMetadataReader = mock();
    VipsExtractor vipsExtractor = mock();
    this.provider = new ImageInfoProvider(this.comDrewImageMetadataReader, vipsExtractor);
  }

  @Test
  void testIsSupported() {
    assertTrue(ImageInfoProvider.isSupported(MediaType.image("jpeg")), "jpeg should be supported");
  }

  @Test
  void testIsNotSupported() {
    assertFalse(
        ImageInfoProvider.isSupported(MediaType.application("pdf")), "pdf should not be supported");
  }

  @Test
  void testIsSupportedWithNullType() {
    assertThrows(
        NullPointerException.class,
        () -> ImageInfoProvider.isSupported(null),
        "null type should throw NullPointerException");
  }

  @Test
  void testCreateCallsMetadataReader() throws ExtractionException {
    Path path = Path.of("test.jpg");
    this.provider.create(path, MediaType.image("jpeg"), new Metadata(), null);
    verify(this.comDrewImageMetadataReader)
        .applyData(eq(path), any(MediaType.class), any(ImageInfo.Builder.class));
  }

  @Test
  void testCreateWithNullMetadata() {
    assertThrows(
        NullPointerException.class,
        () -> this.provider.create(Path.of("test.jpg"), MediaType.image("jpeg"), null, null),
        "null metadata should throw NullPointerException");
  }
}
