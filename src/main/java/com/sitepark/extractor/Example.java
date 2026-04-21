package com.sitepark.extractor;

import com.sitepark.extractor.types.DocInfo;
import com.sitepark.extractor.types.ImageInfo;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Example {

  static void main(String[] args) {

    Path path = Paths.get(args[0]);
    Extractor extractor = new Extractor();

    try {
      FileInfo fileInfo = extractor.extract(path);

      if (fileInfo instanceof DocInfo doc) {
        System.out.println("Title: " + doc.title());
        System.out.println("Content: " + doc.extractedContent());
      } else if (fileInfo instanceof ImageInfo img) {
        System.out.println("Dimensions: " + img.width() + " × " + img.height());

        if (img.vibrantColors() != null) {

          System.out.println("Vibrant: " + img.vibrantColors().vibrant());

          System.out.println("Dominant: " + img.vibrantColors().dominant());
        }
      }
    } catch (UnsupportedMediaTypeException e) {
      System.out.println("Unsupported format: " + e.getMessage());

    } catch (ExtractionException e) {
      System.out.println("Extraction failed: " + e.getMessage());
    }
  }
}
