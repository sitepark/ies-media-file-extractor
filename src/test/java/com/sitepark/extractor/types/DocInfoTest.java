package com.sitepark.extractor.types;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;

import nl.jqno.equalsverifier.EqualsVerifier;

class DocInfoTest {

	@Test
	@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
	public void testEqualsContract() {
		EqualsVerifier.forClass(DocInfo.class).verify();
	}

	@Test
	@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
	public void testToString() {
		ToStringVerifier.forClass(DocInfo.class)
				.withClassName(NameStyle.SIMPLE_NAME)
				.verify();
	}

	@Test
	public void testGetTitle() {
		DocInfo docInfo = DocInfo.builder()
				.title("title")
				.build();
		assertEquals("title", docInfo.getTitle());
	}

	@Test
	public void testGetDescription() {
		DocInfo docInfo = DocInfo.builder()
				.description("description")
				.build();
		assertEquals("description", docInfo.getDescription());
	}

	@Test
	public void testGetCreationDate() {
		DocInfo docInfo = DocInfo.builder()
				.creationDate(123L)
				.build();
		assertEquals(123L, docInfo.getCreationDate());
	}

	@Test
	public void testGetLastModificationDate() {
		DocInfo docInfo = DocInfo.builder()
				.lastModificationDate(123L)
				.build();
		assertEquals(123L, docInfo.getLastModificationDate());
	}

	@Test
	public void testGetExtractedContent() {
		DocInfo docInfo = DocInfo.builder()
				.extractedContent("abc")
				.build();
		assertEquals("abc", docInfo.getExtractedContent());
	}

	@Test
	public void testWithBlankExtractedContent() {
		DocInfo docInfo = DocInfo.builder()
				.extractedContent("")
				.build();
		assertNull(docInfo.getExtractedContent(), "extractedContent should be null");
	}


	@Test
	public void testToBuilder() {
		DocInfo docInfo = DocInfo.builder()
				.title("title")
				.description("description")
				.creationDate(123L)
				.lastModificationDate(123L)
				.build();

		DocInfo copy = docInfo.toBuilder()
				.description("description2")
				.build();

		DocInfo expected = DocInfo.builder()
				.title("title")
				.description("description2")
				.creationDate(123L)
				.lastModificationDate(123L)
				.build();

		assertEquals(expected, copy, "unexpected doc-info copy");

	}
}
