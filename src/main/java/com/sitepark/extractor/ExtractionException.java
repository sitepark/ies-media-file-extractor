package com.sitepark.extractor;

public class ExtractionException extends Exception {

	private static final long serialVersionUID = -2475991392211002674L;

	public ExtractionException(String msg) {
		super(msg);
	}

	public ExtractionException(String msg, Throwable t) {
		super(msg, t);
	}
}
