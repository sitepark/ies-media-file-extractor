package com.sitepark.extractor.types;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.metadata.TikaCoreProperties;
import org.junit.jupiter.api.Test;

class DocInfoFactoryTest {

	private final DocInfoFactory factory = new DocInfoFactory();

	@Test
	void testIsSupported() {
		assertTrue(DocInfoFactory.isSupported("application/pdf"), "pdf should be supported");
	}

	@Test
	void testIsNotSupported() {
		assertFalse(DocInfoFactory.isSupported("image/gif"), "gif should not be supported");
	}

	@Test
	void testWithoutTitle() {
		DocInfo info = this.factory.create(new Metadata(), null);
		assertNull(info.getTitle(), "title must be null");
	}

	@Test
	void testWithMissingTitle() {
		Metadata metadata = this.createMetadata(TikaCoreProperties.TITLE, "test");
		DocInfo info = this.factory.create(metadata, null);
		assertEquals("test", info.getTitle(), "unexpected title");
	}

	@Test
	void testWithDescription() {
		Metadata metadata = this.createMetadata(TikaCoreProperties.DESCRIPTION, "test");
		DocInfo info = this.factory.create(metadata, null);
		assertEquals("test", info.getDescription(), "unexpected title");
	}

	@Test
	void testWithCreationDate() {
		Date date = new Date(123000L);
		Metadata metadata = this.createMetadata(TikaCoreProperties.CREATED, date);
		DocInfo info = this.factory.create(metadata, null);
		assertEquals(123000L, info.getCreationDate(), "unexpected creationDate");
	}

	@Test
	void testWithLastModifiedDate() {
		Date date = new Date(123000L);
		Metadata metadata = this.createMetadata(TikaCoreProperties.MODIFIED, date);
		DocInfo info = this.factory.create(metadata, null);
		assertEquals(123000L, info.getLastModificationDate(), "unexpected lastModifiedDate");
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
