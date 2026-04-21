[![codecov](https://codecov.io/gh/sitepark/ies-media-file-extractor/graph/badge.svg?token=WM62RRnk4X)](https://codecov.io/gh/sitepark/ies-media-file-extractor)
[![Known Vulnerabilities](https://snyk.io/test/github/sitepark/ies-media-file-extractor/badge.svg)](https://snyk.io/test/github/sitepark/ies-media-file-extractor/)

# IES media file extractor

For the media management of the CMS IES, the media to be managed are analyzed and, depending on the file type, metadata
and — if applicable — the text content are extracted. This library handles that extraction.

## How to use

**Maven dependency**

```xml

<dependency>
    <groupId>com.sitepark.ies</groupId>
    <artifactId>ies-media-file-extractor</artifactId>
    <version>1.0.0</version>
</dependency>
```

**Extracting metadata**

```java
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
```

## How to build

```sh
mvn install package
```

