package com.sitepark.extractor.provider.image;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.drew.metadata.iptc.IptcDirectory;
import com.sitepark.extractor.ExtractionException;
import com.sitepark.extractor.types.ImageInfo;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ComDrewImageMetadataReaderTest {

  private static final Path MONA_LISA = Paths.get("src/test/resources/files/images/Mona_Lisa.jpg");

  private ComDrewImageMetadataReader reader;

  @BeforeEach
  void setUp() {
    this.reader = new ComDrewImageMetadataReader();
  }

  @Test
  void testApplyDataFromFileWithoutIptc() throws ExtractionException {
    ImageInfo.Builder builder = ImageInfo.builder();
    this.reader.applyData(MONA_LISA, builder);
    assertEquals(
        ImageInfo.builder().build(),
        builder.build(),
        "ImageInfo without IPTC metadata should be empty");
  }

  @Test
  void testApplyDataWithIptcCopyright() {
    com.drew.metadata.Metadata metadata =
        this.createMetadata(IptcDirectory.TAG_COPYRIGHT_NOTICE, "© 2024 Sitepark");
    ImageInfo.Builder builder = ImageInfo.builder();
    this.reader.applyData(metadata, builder);
    assertEquals(
        ImageInfo.builder().copyright("© 2024 Sitepark").build(),
        builder.build(),
        "copyright should be read from IPTC TAG_COPYRIGHT_NOTICE");
  }

  @Test
  void testApplyDataWithIptcHeadlineAsTitle() {
    com.drew.metadata.Metadata metadata =
        this.createMetadata(IptcDirectory.TAG_HEADLINE, "Breaking News");
    ImageInfo.Builder builder = ImageInfo.builder();
    this.reader.applyData(metadata, builder);
    assertEquals(
        ImageInfo.builder().title("Breaking News").build(),
        builder.build(),
        "title should be read from IPTC TAG_HEADLINE");
  }

  @Test
  void testApplyDataWithIptcObjectNameAsTitle() {
    com.drew.metadata.Metadata metadata =
        this.createMetadata(IptcDirectory.TAG_OBJECT_NAME, "Photo Title");
    ImageInfo.Builder builder = ImageInfo.builder();
    this.reader.applyData(metadata, builder);
    assertEquals(
        ImageInfo.builder().title("Photo Title").build(),
        builder.build(),
        "title should fall back to IPTC TAG_OBJECT_NAME when no headline is set");
  }

  @Test
  void testApplyDataHeadlineTakesPrecedenceOverObjectName() {
    IptcDirectory iptcDir = new IptcDirectory();
    iptcDir.setObject(IptcDirectory.TAG_HEADLINE, "Headline Title");
    iptcDir.setObject(IptcDirectory.TAG_OBJECT_NAME, "Object Name");
    com.drew.metadata.Metadata metadata = new com.drew.metadata.Metadata();
    metadata.addDirectory(iptcDir);

    ImageInfo.Builder builder = ImageInfo.builder();
    this.reader.applyData(metadata, builder);
    assertEquals(
        ImageInfo.builder().title("Headline Title").build(),
        builder.build(),
        "TAG_HEADLINE should take precedence over TAG_OBJECT_NAME as title");
  }

  @Test
  void testApplyDataWithIptcDescription() {
    com.drew.metadata.Metadata metadata =
        this.createMetadata(IptcDirectory.TAG_CAPTION, "A landscape photo.");
    ImageInfo.Builder builder = ImageInfo.builder();
    this.reader.applyData(metadata, builder);
    assertEquals(
        ImageInfo.builder().description("A landscape photo.").build(),
        builder.build(),
        "description should be read from IPTC TAG_CAPTION");
  }

  @Test
  void testApplyDataWithIptcMultilineDescription() {
    com.drew.metadata.Metadata metadata =
        this.createMetadata(IptcDirectory.TAG_CAPTION, "Line one\nLine two\nLine three");
    ImageInfo.Builder builder = ImageInfo.builder();
    this.reader.applyData(metadata, builder);
    assertEquals(
        ImageInfo.builder().description("Line one\nLine two\nLine three").build(),
        builder.build(),
        "multiline IPTC description should be preserved with newlines");
  }

  @Test
  void testApplyDataIgnoresEmptyIptcValues() {
    IptcDirectory iptcDir = new IptcDirectory();
    iptcDir.setObject(IptcDirectory.TAG_COPYRIGHT_NOTICE, "  ");
    iptcDir.setObject(IptcDirectory.TAG_HEADLINE, "");
    com.drew.metadata.Metadata metadata = new com.drew.metadata.Metadata();
    metadata.addDirectory(iptcDir);

    ImageInfo.Builder builder = ImageInfo.builder();
    this.reader.applyData(metadata, builder);
    assertEquals(
        ImageInfo.builder().build(),
        builder.build(),
        "blank IPTC values should not be applied to ImageInfo");
  }

  @Test
  void testApplyDataThrowsExtractionExceptionForNonExistentPath() {
    Path missing = Paths.get("src/test/resources/files/images/does-not-exist.jpg");
    ImageInfo.Builder builder = ImageInfo.builder();
    assertThrows(
        ExtractionException.class,
        () -> this.reader.applyData(missing, builder),
        "reading a non-existent file should throw ExtractionException");
  }

  @Test
  void testApplyDataIgnoresEmptyObjectName() {
    IptcDirectory iptcDir = new IptcDirectory();
    iptcDir.setObject(IptcDirectory.TAG_OBJECT_NAME, "  ");
    com.drew.metadata.Metadata metadata = new com.drew.metadata.Metadata();
    metadata.addDirectory(iptcDir);

    ImageInfo.Builder builder = ImageInfo.builder();
    this.reader.applyData(metadata, builder);
    assertEquals(
        ImageInfo.builder().build(),
        builder.build(),
        "blank TAG_OBJECT_NAME should not be applied as title");
  }

  private com.drew.metadata.Metadata createMetadata(int tagType, String value) {
    IptcDirectory iptcDir = new IptcDirectory();
    iptcDir.setObject(tagType, value);
    com.drew.metadata.Metadata metadata = new com.drew.metadata.Metadata();
    metadata.addDirectory(iptcDir);
    return metadata;
  }
}
