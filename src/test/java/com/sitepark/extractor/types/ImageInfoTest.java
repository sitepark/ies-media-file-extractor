package com.sitepark.extractor.types;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.TooManyMethods"})
class ImageInfoTest {

  @Test
  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  void testEqualsContract() {
    EqualsVerifier.forClass(ImageInfo.class).verify();
  }

  @Test
  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  void testToString() {
    ToStringVerifier.forClass(ImageInfo.class).withClassName(NameStyle.SIMPLE_NAME).verify();
  }

  @Test
  void testTitle() {
    ImageInfo imageInfo = ImageInfo.builder().title("title").build();
    assertEquals("title", imageInfo.title(), "unexpected title");
  }

  @Test
  void testTitleNull() {
    ImageInfo imageInfo = ImageInfo.builder().build();
    assertNull(imageInfo.title(), "title should be null");
  }

  @Test
  void testDescription() {
    ImageInfo imageInfo = ImageInfo.builder().description("description").build();
    assertEquals("description", imageInfo.description(), "unexpected description");
  }

  @Test
  void testDescriptionNull() {
    ImageInfo imageInfo = ImageInfo.builder().build();
    assertNull(imageInfo.description(), "description should be null");
  }

  @Test
  void testCopyright() {
    ImageInfo imageInfo = ImageInfo.builder().copyright("© 2024").build();
    assertEquals("© 2024", imageInfo.copyright(), "unexpected copyright");
  }

  @Test
  void testCopyrightNull() {
    ImageInfo imageInfo = ImageInfo.builder().build();
    assertNull(imageInfo.copyright(), "copyright should be null");
  }

  @Test
  @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
  void testToBuilder() {
    ImageInfo imageInfo =
        ImageInfo.builder().title("title").description("description").copyright("© 2024").build();

    ImageInfo copy = imageInfo.toBuilder().copyright("© 2025").build();

    ImageInfo expected =
        ImageInfo.builder().title("title").description("description").copyright("© 2025").build();

    assertEquals(expected, copy, "unexpected ImageInfo after toBuilder()");
  }
}
