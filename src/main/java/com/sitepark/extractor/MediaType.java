package com.sitepark.extractor;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serial;
import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;
import org.jspecify.annotations.Nullable;

public record MediaType(
    @JsonProperty("type") String type,
    @JsonProperty("subtype") String subtype,
    @JsonProperty("parameters") Map<String, String> parameters)
    implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  private static final Pattern SPECIAL = Pattern.compile("[\\(\\)<>@,;:\\\\\"/\\[\\]\\?=]");
  private static final Pattern SPECIAL_OR_WHITESPACE =
      Pattern.compile("[\\(\\)<>@,;:\\\\\"/\\[\\]\\?=\\s]");

  public MediaType {
    parameters = Map.copyOf(parameters);
  }

  public static @Nullable MediaType parse(@Nullable String s) {
    if (s == null) {
      return null;
    }
    org.apache.tika.mime.MediaType mediaType = org.apache.tika.mime.MediaType.parse(s);
    return new MediaType(mediaType.getType(), mediaType.getSubtype(), mediaType.getParameters());
  }

  public static MediaType application(String subtype) {
    return new MediaType("application", subtype, Map.of());
  }

  public static MediaType audio(String subtype) {
    return new MediaType("audio", subtype, Map.of());
  }

  public static MediaType image(String subtype) {
    return new MediaType("image", subtype, Map.of());
  }

  public static MediaType text(String subtype) {
    return new MediaType("text", subtype, Map.of());
  }

  public static MediaType video(String subtype) {
    return new MediaType("video", subtype, Map.of());
  }

  @Override
  public String toString() {
    if (parameters.isEmpty()) {
      return type + "/" + subtype;
    } else {
      StringBuilder builder = new StringBuilder();
      builder.append(type).append('/').append(subtype);
      SortedMap<String, String> map = new TreeMap<>();

      for (Map.Entry<String, String> entry : parameters.entrySet()) {
        String key = entry.getKey().trim().toLowerCase(Locale.ENGLISH);
        map.put(key, entry.getValue());
      }

      for (Map.Entry<String, String> entry : map.entrySet()) {
        builder.append("; ").append(entry.getKey()).append('=');
        String value = entry.getValue();
        if (SPECIAL_OR_WHITESPACE.matcher(value).find()) {
          builder.append('"').append(SPECIAL.matcher(value).replaceAll("\\\\$0")).append('"');
        } else {
          builder.append(value);
        }
      }
      return builder.toString();
    }
  }
}
