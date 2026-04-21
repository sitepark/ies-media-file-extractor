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

## System Requirements

Image extraction depends on [vips-ipc](https://github.com/sitepark/vips-ipc), which uses libvips
internally. Some image formats require additional system libraries alongside libvips:

| Format | Required package | Effect if missing |
|--------|-----------------|-------------------|
| SVG | `librsvg2-2` (Debian/Ubuntu) | SVG files cannot be loaded |
| EPS / PostScript | `ghostscript` | Falls back to embedded preview bitmap (see below) |

Install on Debian/Ubuntu:

```bash
sudo apt-get install -y librsvg2-2 ghostscript
```

### EPS files without Ghostscript: embedded preview bitmap fallback

EPS files produced by design tools (Illustrator, CorelDRAW, etc.) usually contain an embedded
raster preview bitmap alongside the actual PostScript data. When Ghostscript is not installed,
libvips reads this preview bitmap instead of rendering the PostScript. This has three observable
effects on the extracted `ImageInfo`:

- **Dimensions differ** — the preview bitmap has its own pixel dimensions, which may differ from
  the rendered result (typically by one pixel in height)
- **`hasAlpha = true`** — the preview bitmap carries an alpha channel
- **Colors are wrong** — transparent pixels have RGBA(0, 0, 0, 0); their RGB values count as black
  during color palette analysis, pulling all `VibrantColors` values toward black

If `ImageInfo.hasAlpha()` is `true` and all vibrant colors are near black for an EPS file,
Ghostscript is most likely not installed.

## How to build

```sh
mvn install package
```

