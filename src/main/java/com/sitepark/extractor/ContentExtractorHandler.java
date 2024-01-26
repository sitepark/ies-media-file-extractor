package com.sitepark.extractor;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.StringWriter;
import org.apache.tika.sax.ToTextContentHandler;
import org.xml.sax.SAXException;

public class ContentExtractorHandler extends ToTextContentHandler {

  private final int writeLimit;

  private final StringWriter writer;

  private boolean writeLimitReached;

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
    return this.writeLimit != -1 && writeCount + length > this.writeLimit;
  }

  @Override
  public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    if (this.writer.getBuffer().length() == 0) {
      return;
    }
    super.characters(ch, start, length);
  }

  public String toTidyString() {
    String s = this.writer.getBuffer().toString();
    return s.replaceAll("\\s+", " ").trim();
  }
}
