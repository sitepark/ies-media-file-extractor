package com.sitepark.extractor.test;

import java.nio.file.Path;

import org.junit.jupiter.params.provider.Arguments;

import com.sitepark.extractor.FileInfo;

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
