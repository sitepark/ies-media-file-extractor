package com.sitepark.extractor.test;

import com.sitepark.extractor.FileInfo;
import java.nio.file.Path;
import org.junit.jupiter.params.provider.Arguments;

public class FileInfoTestParameter implements Arguments {

  private final Path path;

  private final FileInfo expected;

  public FileInfoTestParameter(Path path, FileInfo expected) {
    this.path = path;
    this.expected = expected;
  }

  @Override
  public Object[] get() {
    return new Object[] {this.path, this.expected};
  }
}
