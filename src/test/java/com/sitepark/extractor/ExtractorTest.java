package com.sitepark.extractor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.sitepark.extractor.test.FileInfoTestParameter;
import com.sitepark.extractor.types.DocInfo;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.Parser;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class ExtractorTest {

  private final Extractor extractor = new Extractor();

  private static final String EXPECTION_FILE_SUFFIX = ".expected.json";

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("createArguments")
  void testWithArguments(Path path, DocInfo expected) throws ExtractionException {
    DocInfo docInfo = (DocInfo) this.extractor.extract(path);
    assertEquals(expected, docInfo, "unexpected docInfo");
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
        10,
        docInfo.getExtractedContent().length(),
        "Content should be truncated after 10 characters");
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
    assertThrows(
        ExtractionException.class,
        () -> {
          extractor.extract(path);
        },
        """
        	So far, this PDF has led to an error, which is probably due\
        	to a bug in PDFBox. If this test now fails, the problem seems\s\
        	to have been solved and the test can be deleted.""");
  }

  @Test
  void testNoContentType() {
    Path path = Paths.get("src/test/resources/files/docs/Sample.pdf");
    Parser parser = mock();
    InputStream inputstream = mock();
    Extractor extractor =
        new Extractor(parser) {
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
  private static Stream<FileInfoTestParameter> createArguments() throws IOException {

    Path path = Paths.get("src/test/resources/files/docs");

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
        DocInfo docInfo = mapper.readValue(json.toFile(), DocInfo.class);

        arguments.add(new FileInfoTestParameter(file, docInfo));
      }
    }

    return arguments.stream();
  }

  @Test
  @Disabled // Enable to create and update *.expected.json files
  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  void createNewExpectedJsonFiles() throws IOException, ExtractionException {

    Path path = Paths.get("src/test/resources/files/docs");

    ObjectMapper mapper = JsonMapper.builder().enable(SerializationFeature.INDENT_OUTPUT).build();
    mapper.setSerializationInclusion(Include.NON_NULL);

    try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(path)) {
      for (Path file : dirStream) {
        if (Files.isDirectory(file) || file.toString().endsWith(".expected.json")) {
          continue;
        }
        FileInfo fileInfo = this.extractor.extract(file);
        Path dir = file.getParent();
        if (dir == null) {
          continue;
        }
        Path json = dir.resolve(file.getFileName() + EXPECTION_FILE_SUFFIX);
        mapper.writeValue(json.toFile(), fileInfo);
      }
    }
  }
}
