package com.sitepark.extractor;

import com.sitepark.extractor.types.DocInfo;
import com.sitepark.extractor.types.ImageInfo;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonPOJOBuilder;
import tools.jackson.databind.json.JsonMapper;

/**
 * The JSON mapper of the tests. The library has no dependency on jackson-databind, so the builders
 * of the file infos are made known to Jackson 3 by mixins.
 */
final class JsonMappers {

  private JsonMappers() {}

  static JsonMapper.Builder builder() {
    return JsonMapper.builder()
        .disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
        .enable(SerializationFeature.INDENT_OUTPUT)
        .addMixIn(ImageInfo.class, ImageInfoMixin.class)
        .addMixIn(ImageInfo.Builder.class, BuilderMixin.class)
        .addMixIn(DocInfo.class, DocInfoMixin.class)
        .addMixIn(DocInfo.Builder.class, BuilderMixin.class);
  }

  @JsonDeserialize(builder = ImageInfo.Builder.class)
  private interface ImageInfoMixin {}

  @JsonDeserialize(builder = DocInfo.Builder.class)
  private interface DocInfoMixin {}

  @JsonPOJOBuilder(withPrefix = "")
  private interface BuilderMixin {}
}
