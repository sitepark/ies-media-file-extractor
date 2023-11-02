package com.sitepark.extractor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.sitepark.extractor.test.FileInfoTestParameter;
import com.sitepark.extractor.types.DocInfo;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

class ExtractorTest {

	private final Extractor extractor = new Extractor();

	private static final String EXPECTION_FILE_SUFFIX = ".expected.json";

	@ParameterizedTest(name = "[{index}] {0}")
	@MethodSource("createArguments")
	void testWithArguments(Path path, DocInfo expected) throws ExtractionException {
		DocInfo docInfo = (DocInfo)this.extractor.extract(path);
		assertEquals(expected, docInfo, "unexpected docInfo");
	}

	@Test
	void testIfSupported() {
		assertTrue(this.extractor.isSupported("application/pdf"), "pdf should be supported");
	}

	@Test
	@SuppressFBWarnings("RV_EXCEPTION_NOT_THROWN")
	void testUnsupported() {
		Path path = Paths.get("src/test/resources/files/unsupported/unsupported");
		assertThrows(UnsupportedMediaTypeException.class, () -> {
			this.extractor.extract(path);
		});
	}

	@SuppressFBWarnings("UPM_UNCALLED_PRIVATE_METHOD")
	private static Stream<FileInfoTestParameter> createArguments() throws IOException {

		Path path = Paths.get("src/test/resources/files/docs");

		ObjectMapper mapper = JsonMapper.builder()
				.enable(SerializationFeature.INDENT_OUTPUT)
				.build();

		List<FileInfoTestParameter> arguments = new ArrayList<>();

		try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(path)) {
			for (Path file : dirStream) {
				if (Files.isDirectory(file)) {
					continue;
				}
				if (file.toString().endsWith(".expected.json")) {
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

		ObjectMapper mapper = JsonMapper.builder()
				.enable(SerializationFeature.INDENT_OUTPUT)
				.build();
		mapper.setSerializationInclusion(Include.NON_NULL);

		try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(path)) {
			for (Path file : dirStream) {
				if (Files.isDirectory(file)) {
					continue;
				}
				if (file.toString().endsWith(".expected.json")) {
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
