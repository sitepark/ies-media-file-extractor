package com.sitepark.extractor.provider.image;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.adobe.internal.xmp.XMPError;
import com.adobe.internal.xmp.XMPException;
import com.adobe.internal.xmp.XMPIterator;
import com.adobe.internal.xmp.XMPMeta;
import com.adobe.internal.xmp.XMPMetaFactory;
import com.drew.imaging.FileType;
import com.drew.metadata.iptc.IptcDirectory;
import com.drew.metadata.xmp.XmpDirectory;
import com.sitepark.extractor.ExtractionException;
import com.sitepark.extractor.MediaType;
import com.sitepark.extractor.types.ImageInfo;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ComDrewImageMetadataReaderTest {

  private static final MediaType JPEG = MediaType.image("jpeg");
  private static final Path MONA_LISA = Paths.get("src/test/resources/files/images/Mona_Lisa.jpg");
  private static final Path DIGITAL_SOURCE_TYPE_IMAGE =
      Paths.get("src/test/resources/files/metadata/digital-source-type.jpg");
  private static final String TRAINED_ALGORITHMIC_MEDIA =
      "http://cv.iptc.org/newscodes/digitalsourcetype/trainedAlgorithmicMedia";

  private ComDrewImageMetadataReader reader;

  @BeforeEach
  void setUp() {
    this.reader = new ComDrewImageMetadataReader();
  }

  @Test
  void testApplyDataFromFileWithoutIptc() throws ExtractionException {
    ImageInfo.Builder builder = ImageInfo.builder();
    this.reader.applyData(MONA_LISA, JPEG, builder);
    assertEquals(
        ImageInfo.builder().type("jpeg").build(),
        builder.build(),
        "ImageInfo without IPTC metadata should be empty");
  }

  @Test
  void testApplyDataWithIptcCopyright() {
    ComDrewImageMetadataReader.ReaderResult result =
        this.createReaderResult(IptcDirectory.TAG_COPYRIGHT_NOTICE, "© 2024 Sitepark");

    ImageInfo.Builder builder = ImageInfo.builder();
    this.reader.applyData(result, JPEG, builder);
    assertEquals(
        ImageInfo.builder().type("jpeg").copyright("© 2024 Sitepark").build(),
        builder.build(),
        "copyright should be read from IPTC TAG_COPYRIGHT_NOTICE");
  }

  @Test
  void testApplyDataWithIptcHeadlineAsTitle() {
    ComDrewImageMetadataReader.ReaderResult result =
        this.createReaderResult(IptcDirectory.TAG_HEADLINE, "Breaking News");
    ImageInfo.Builder builder = ImageInfo.builder();
    this.reader.applyData(result, JPEG, builder);
    assertEquals(
        ImageInfo.builder().type("jpeg").title("Breaking News").build(),
        builder.build(),
        "title should be read from IPTC TAG_HEADLINE");
  }

  @Test
  void testApplyDataWithIptcObjectNameAsTitle() {
    ComDrewImageMetadataReader.ReaderResult result =
        this.createReaderResult(IptcDirectory.TAG_OBJECT_NAME, "Photo Title");
    ImageInfo.Builder builder = ImageInfo.builder();
    this.reader.applyData(result, JPEG, builder);
    assertEquals(
        ImageInfo.builder().type("jpeg").title("Photo Title").build(),
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

    ComDrewImageMetadataReader.ReaderResult result =
        new ComDrewImageMetadataReader.ReaderResult(FileType.Jpeg, metadata);

    ImageInfo.Builder builder = ImageInfo.builder();
    this.reader.applyData(result, JPEG, builder);
    assertEquals(
        ImageInfo.builder().type("jpeg").title("Headline Title").build(),
        builder.build(),
        "TAG_HEADLINE should take precedence over TAG_OBJECT_NAME as title");
  }

  @Test
  void testApplyDataWithIptcDescription() {
    ComDrewImageMetadataReader.ReaderResult result =
        this.createReaderResult(IptcDirectory.TAG_CAPTION, "A landscape photo.");
    ImageInfo.Builder builder = ImageInfo.builder();
    this.reader.applyData(result, JPEG, builder);
    assertEquals(
        ImageInfo.builder().type("jpeg").description("A landscape photo.").build(),
        builder.build(),
        "description should be read from IPTC TAG_CAPTION");
  }

  @Test
  void testApplyDataWithIptcMultilineDescription() {
    ComDrewImageMetadataReader.ReaderResult result =
        this.createReaderResult(IptcDirectory.TAG_CAPTION, "Line one\nLine two\nLine three");
    ImageInfo.Builder builder = ImageInfo.builder();
    this.reader.applyData(result, JPEG, builder);
    assertEquals(
        ImageInfo.builder().type("jpeg").description("Line one\nLine two\nLine three").build(),
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

    ComDrewImageMetadataReader.ReaderResult result =
        new ComDrewImageMetadataReader.ReaderResult(FileType.Jpeg, metadata);

    ImageInfo.Builder builder = ImageInfo.builder();
    this.reader.applyData(result, JPEG, builder);
    assertEquals(
        ImageInfo.builder().type("jpeg").build(),
        builder.build(),
        "blank IPTC values should not be applied to ImageInfo");
  }

  @Test
  void testApplyDataThrowsExtractionExceptionForNonExistentPath() {
    Path missing = Paths.get("src/test/resources/files/images/does-not-exist.jpg");
    ImageInfo.Builder builder = ImageInfo.builder();
    assertThrows(
        ExtractionException.class,
        () -> this.reader.applyData(missing, JPEG, builder),
        "reading a non-existent file should throw ExtractionException");
  }

  @Test
  void testApplyDataIgnoresEmptyObjectName() {
    IptcDirectory iptcDir = new IptcDirectory();
    iptcDir.setObject(IptcDirectory.TAG_OBJECT_NAME, "  ");
    com.drew.metadata.Metadata metadata = new com.drew.metadata.Metadata();
    metadata.addDirectory(iptcDir);

    ComDrewImageMetadataReader.ReaderResult result =
        new ComDrewImageMetadataReader.ReaderResult(FileType.Jpeg, metadata);

    ImageInfo.Builder builder = ImageInfo.builder();
    this.reader.applyData(result, JPEG, builder);
    assertEquals(
        ImageInfo.builder().type("jpeg").build(),
        builder.build(),
        "blank TAG_OBJECT_NAME should not be applied as title");
  }

  @Test
  void testApplyDataFromFileWithXmpDigitalSourceType() throws ExtractionException {
    ImageInfo.Builder builder = ImageInfo.builder();
    this.reader.applyData(DIGITAL_SOURCE_TYPE_IMAGE, JPEG, builder);
    assertEquals(
        ImageInfo.builder().type("jpeg").digitalSourceType(TRAINED_ALGORITHMIC_MEDIA).build(),
        builder.build(),
        "digitalSourceType should be read from the XMP metadata of the file");
  }

  @Test
  void testApplyDataWithXmpDigitalSourceType() throws XMPException {
    ComDrewImageMetadataReader.ReaderResult result =
        this.createXmpReaderResult("  " + TRAINED_ALGORITHMIC_MEDIA + "  ");
    ImageInfo.Builder builder = ImageInfo.builder();
    this.reader.applyData(result, JPEG, builder);
    assertEquals(
        ImageInfo.builder().type("jpeg").digitalSourceType(TRAINED_ALGORITHMIC_MEDIA).build(),
        builder.build(),
        "digitalSourceType should be read from XMP Iptc4xmpExt:DigitalSourceType");
  }

  @Test
  void testApplyDataIgnoresEmptyXmpDigitalSourceType() throws XMPException {
    ComDrewImageMetadataReader.ReaderResult result = this.createXmpReaderResult("  ");
    ImageInfo.Builder builder = ImageInfo.builder();
    this.reader.applyData(result, JPEG, builder);
    assertEquals(
        ImageInfo.builder().type("jpeg").build(),
        builder.build(),
        "blank XMP DigitalSourceType should not be applied to ImageInfo");
  }

  @Test
  void testApplyDataWithXmpWithoutDigitalSourceType() {
    XmpDirectory xmpDir = new XmpDirectory();
    xmpDir.setXMPMeta(XMPMetaFactory.create());
    com.drew.metadata.Metadata metadata = new com.drew.metadata.Metadata();
    metadata.addDirectory(xmpDir);

    ComDrewImageMetadataReader.ReaderResult result =
        new ComDrewImageMetadataReader.ReaderResult(FileType.Jpeg, metadata);

    ImageInfo.Builder builder = ImageInfo.builder();
    this.reader.applyData(result, JPEG, builder);
    assertEquals(
        ImageInfo.builder().type("jpeg").build(),
        builder.build(),
        "XMP without DigitalSourceType should not set digitalSourceType");
  }

  @Test
  void testApplyDataIgnoresUnreadableXmpDigitalSourceType() throws XMPException {
    XMPMeta xmpMeta = mock(XMPMeta.class);
    when(xmpMeta.iterator(any())).thenReturn(mock(XMPIterator.class));
    when(xmpMeta.getPropertyString(
            ComDrewImageMetadataReader.IPTC_EXT_NAMESPACE,
            ComDrewImageMetadataReader.DIGITAL_SOURCE_TYPE))
        .thenThrow(new XMPException("broken xmp", XMPError.BADXMP));
    XmpDirectory xmpDir = new XmpDirectory();
    xmpDir.setXMPMeta(xmpMeta);
    com.drew.metadata.Metadata metadata = new com.drew.metadata.Metadata();
    metadata.addDirectory(xmpDir);

    ComDrewImageMetadataReader.ReaderResult result =
        new ComDrewImageMetadataReader.ReaderResult(FileType.Jpeg, metadata);

    ImageInfo.Builder builder = ImageInfo.builder();
    this.reader.applyData(result, JPEG, builder);
    assertEquals(
        ImageInfo.builder().type("jpeg").build(),
        builder.build(),
        "unreadable XMP DigitalSourceType should be ignored");
  }

  private ComDrewImageMetadataReader.ReaderResult createXmpReaderResult(String digitalSourceType)
      throws XMPException {
    XMPMeta xmpMeta = XMPMetaFactory.create();
    xmpMeta.setProperty(
        ComDrewImageMetadataReader.IPTC_EXT_NAMESPACE,
        ComDrewImageMetadataReader.DIGITAL_SOURCE_TYPE,
        digitalSourceType);
    XmpDirectory xmpDir = new XmpDirectory();
    xmpDir.setXMPMeta(xmpMeta);
    com.drew.metadata.Metadata metadata = new com.drew.metadata.Metadata();
    metadata.addDirectory(xmpDir);

    return new ComDrewImageMetadataReader.ReaderResult(FileType.Jpeg, metadata);
  }

  private ComDrewImageMetadataReader.ReaderResult createReaderResult(int tagType, String value) {
    IptcDirectory iptcDir = new IptcDirectory();
    iptcDir.setObject(tagType, value);
    com.drew.metadata.Metadata metadata = new com.drew.metadata.Metadata();
    metadata.addDirectory(iptcDir);

    return new ComDrewImageMetadataReader.ReaderResult(FileType.Jpeg, metadata);
  }
}
