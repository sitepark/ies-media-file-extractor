package com.sitepark.extractor;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.StringWriter;
import org.apache.tika.sax.ToTextContentHandler;
import org.xml.sax.SAXException;

/**
 * SAX content handler that captures plain text from Tika parsing up to a configurable character
 * limit, and provides a cleaned-up view of the extracted content.
 */
public class ContentExtractorHandler extends ToTextContentHandler {

  private final int writeLimit;

  private final StringWriter writer;

  private boolean writeLimitReached;

  /**
   * Creates a new handler that writes to the given {@link StringWriter} up to {@code writeLimit}
   * characters.
   *
   * @param writer the writer to which extracted characters are appended
   * @param writeLimit the maximum number of characters to write; use {@code -1} for no limit
   */
  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public ContentExtractorHandler(StringWriter writer, int writeLimit) {
    super(writer);
    this.writer = writer;
    this.writeLimit = writeLimit;
  }

  /** Writes the given characters to the given character stream. */
  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {

    if (this.writeLimitReached) {
      return;
    }

    if (this.wouldLimitBeReached(length)) {
      int writeCount = this.writer.getBuffer().length();
      super.characters(ch, start, this.writeLimit - writeCount);
      this.writeLimitReached = true;
      return;
    }

    super.characters(ch, start, length);
  }

  private boolean wouldLimitBeReached(int length) {
    int writeCount = this.writer.getBuffer().length();
    return (this.writeLimit != -1) && ((writeCount + length) > this.writeLimit);
  }

  @Override
  public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    if (this.writer.getBuffer().length() == 0) {
      return;
    }
    super.characters(ch, start, length);
  }

  /**
   * Returns the extracted content with all whitespace sequences collapsed to a single space and
   * leading/trailing whitespace removed.
   *
   * <p>Unlike {@link #toString()}, which returns the raw buffered content, this method normalizes
   * whitespace for use as indexed text.
   *
   * @return the cleaned extracted content; never {@code null}
   */
  public String toTidyString() {
    String s = this.writer.getBuffer().toString();
    return s.replaceAll("\\s+", " ").trim();
  }
}
