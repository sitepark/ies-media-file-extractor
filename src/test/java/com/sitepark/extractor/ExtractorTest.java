package com.sitepark.extractor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.sitepark.extractor.test.FileInfoTestParameter;
import com.sitepark.extractor.types.DocInfo;
import com.sitepark.extractor.types.ImageInfo;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.Parser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings({"PMD.UnitTestContainsTooManyAsserts", "PMD.UnitTestShouldIncludeAssert"})
class ExtractorTest {

  private final Extractor extractor = new Extractor();

  @AfterAll
  static void generateVibrantColorsReport() throws IOException {
    VibrantColorsReport.generate();
  }

  private static final String EXPECTION_FILE_SUFFIX = ".expected.json";

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("createDocsArguments")
  void testWithDocsArguments(Path path, DocInfo expected) throws ExtractionException {
    DocInfo docInfo = (DocInfo) this.extractor.extract(path);
    assertEquals(expected, docInfo, "unexpected docInfo");
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("createImagesArguments")
  void testWithImagesArguments(Path path, ImageInfo expected) throws ExtractionException {
    ImageInfo imageInfo = (ImageInfo) this.extractor.extract(path);
    assertEquals(expected, imageInfo, "unexpected imageInfo");
  }

  @Test
  void testIfSupported() {
    assertTrue(this.extractor.isSupported("application/pdf"), "pdf should be supported");
  }

  @Test
  void testExtractionException() {
    Path path = Paths.get("src/test/resources/files/notfound");
    assertThrows(
        ExtractionException.class,
        () -> {
          this.extractor.extract(path);
        });
  }

  @Test
  void testWriteLimit() throws ExtractionException {
    Path path = Paths.get("src/test/resources/files/docs/Sample.pdf");
    Extractor extractor = new Extractor();
    extractor.setDefaultWriteLimit(10);
    DocInfo docInfo = (DocInfo) extractor.extract(path);

    assertEquals(
        10, docInfo.extractedContent().length(), "Content should be truncated after 10 characters");
  }

  @Test
  void testInvalidWriteLimit() throws ExtractionException {
    Extractor extractor = new Extractor();
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          extractor.setDefaultWriteLimit(0);
        },
        "writeLimit must be greater then 0");
  }

  @Test
  void testWithBrokenPdf() throws ExtractionException {
    Path path = Paths.get("src/test/resources/files/docs/broken/pressemeldung.pdf");
    Extractor extractor = new Extractor();

    DocInfo docInfo = (DocInfo) extractor.extract(path);
    assertThat(docInfo.extractedContent())
        .withFailMessage("Unexpected content")
        .startsWith("Iseborjer Kinno Pressemitteilung Das");
  }

  @Test
  void testNoContentType() {
    Path path = Paths.get("src/test/resources/files/docs/Sample.pdf");
    Parser parser = mock();
    InputStream inputstream = mock();
    Extractor extractor =
        new Extractor(parser, List.of()) {
          @Override
          protected InputStream createInputStream(Path path, Metadata metadata) {
            return inputstream;
          }
        };
    ExtractionException e =
        assertThrows(
            ExtractionException.class,
            () -> {
              extractor.extract(path);
            });
    assertTrue(
        e.getMessage().contains("Content-Type"),
        "message should be contains 'Content-Type': " + e.getMessage());
  }

  @Test
  @SuppressFBWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
  void testExtractWithNullPath() {
    assertThrows(
        NullPointerException.class,
        () -> this.extractor.extract((Path) null),
        "null path should throw NullPointerException");
  }

  @Test
  @SuppressFBWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
  void testExtractWithWriteLimitAndNullPath() {
    assertThrows(
        NullPointerException.class,
        () -> this.extractor.extract(null, 100),
        "null path should throw NullPointerException");
  }

  @Test
  @SuppressFBWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
  void testIsSupportedWithNullMediaType() {
    assertThrows(
        NullPointerException.class,
        () -> this.extractor.isSupported(null),
        "null mediaType should throw NullPointerException");
  }

  @Test
  @SuppressFBWarnings("RV_EXCEPTION_NOT_THROWN")
  void testUnsupported() {
    Path path = Paths.get("src/test/resources/files/unsupported/unsupported");
    assertThrows(
        UnsupportedMediaTypeException.class,
        () -> {
          this.extractor.extract(path);
        });
  }

  @SuppressFBWarnings("UPM_UNCALLED_PRIVATE_METHOD")
  private static Stream<FileInfoTestParameter> createDocsArguments() throws IOException {
    return createArguments(Paths.get("src/test/resources/files/docs"), DocInfo.class);
  }

  @SuppressFBWarnings("UPM_UNCALLED_PRIVATE_METHOD")
  private static Stream<FileInfoTestParameter> createImagesArguments() throws IOException {
    return createArguments(Paths.get("src/test/resources/files/images"), ImageInfo.class);
  }

  private static Stream<FileInfoTestParameter> createArguments(
      Path path, Class<? extends FileInfo> type) throws IOException {

    ObjectMapper mapper = JsonMapper.builder().enable(SerializationFeature.INDENT_OUTPUT).build();

    List<FileInfoTestParameter> arguments = new ArrayList<>();

    try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(path)) {
      for (Path file : dirStream) {
        if (Files.isDirectory(file) || file.toString().endsWith(".expected.json")) {
          continue;
        }
        Path dir = file.getParent();
        if (dir == null) {
          continue;
        }
        Path json = dir.resolve(file.getFileName() + EXPECTION_FILE_SUFFIX);
        FileInfo fileInfo = mapper.readValue(json.toFile(), type);

        arguments.add(new FileInfoTestParameter(file, fileInfo));
      }
    }

    return arguments.stream();
  }

  @Test
  // @Disabled // Enable to create and update *.expected.json files
  void createNewExpectedJsonFiles() throws IOException, ExtractionException {
    ObjectMapper mapper = JsonMapper.builder().enable(SerializationFeature.INDENT_OUTPUT).build();
    mapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);

    for (Path dir :
        List.of(
            Paths.get("src/test/resources/files/docs"),
            Paths.get("src/test/resources/files/images"))) {
      try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(dir)) {
        for (Path file : dirStream) {
          if (Files.isDirectory(file) || file.toString().endsWith(".expected.json")) {
            continue;
          }
          try {
            FileInfo fileInfo = this.extractor.extract(file);
            Path json = dir.resolve(file.getFileName() + EXPECTION_FILE_SUFFIX);
            mapper.writeValue(json.toFile(), fileInfo);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    }
  }

  private static class CombinedDirectoryStream implements Iterable<Path>, AutoCloseable {
    private final List<DirectoryStream<Path>> streams;

    public CombinedDirectoryStream(DirectoryStream<Path>... streams) {
      this.streams = Arrays.asList(streams);
    }

    @Override
    public Iterator<Path> iterator() {
      return streams.stream()
          .flatMap(stream -> StreamSupport.stream(stream.spliterator(), false))
          .iterator();
    }

    @Override
    public void close() throws IOException {
      IOException exception = null;
      for (DirectoryStream<Path> stream : streams) {
        try {
          stream.close();
        } catch (IOException e) {
          if (exception == null) {
            exception = e;
          } else {
            exception.addSuppressed(e);
          }
        }
      }
      if (exception != null) throw exception;
    }
  }
}
