package com.sitepark.extractor;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.sitepark.extractor.types.DocInfo;

class ExtractorTest2 {

	private final Extractor extractor = new Extractor();

	@Test
	void testPdf() throws Exception {
		Path path = Paths.get("src/test/resources/files/docs/Sample.pdf");
		DocInfo docInfo = (DocInfo)this.extractor.extract(path);
		System.out.println(docInfo.getTitle());
		System.out.println(docInfo.getDescription());
		System.out.println(docInfo.getCreationDate());
		System.out.println(docInfo.getLastModificationDate());
		System.out.println(docInfo.getExtractedContent());
	}

	@Test
	void testJpg() throws Exception {
		Path path = Paths.get("src/test/resources/files/images/Fotolia_198915619_S.jpg");
		FileInfo fileInfo = this.extractor.extract(path);
	}

	@Test
	void testImages() throws Exception {

		Path path = Paths.get("src/test/resources/files/images");

		try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(path)) {
			for (Path file : dirStream) {
				System.out.println("### " + file);
				FileInfo fileInfo = this.extractor.extract(file);
			}
		}

	}

	@Test
	void testDocuments() throws Exception {

		Path path = Paths.get("src/test/resources/files/docs");

		try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(path)) {
			for (Path file : dirStream) {
				System.out.println("### " + file);
				FileInfo fileInfo = this.extractor.extract(file);
			}
		}
	}

	@Test
	void testAudio() throws Exception {

		Path path = Paths.get("src/test/resources/files/audio");

		try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(path)) {
			for (Path file : dirStream) {
				System.out.println("### " + file);
				FileInfo fileInfo = this.extractor.extract(file);
			}
		}
	}

	@Test
	void testVideo() throws Exception {

		Path path = Paths.get("src/test/resources/files/video");

		try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(path)) {
			for (Path file : dirStream) {
				System.out.println("### " + file);
				FileInfo fileInfo = this.extractor.extract(file);
			}
		}
	}
}
