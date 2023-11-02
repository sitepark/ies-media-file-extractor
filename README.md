# File extractor

For the media management of the CMS IES, the media to be managed are analyzed and, depending on the file type, metadata and, if necessary, also the text contained are read out. This project takes over the task to extract the required data from the files.

## How to build

```sh
mvn install package
```

## How to use

Maven-Dependency

```xml
<dependency>
	<groupId>com.sitepark.ies</groupId>
	<artifactId>ies-media-file-extractor</artifactId>
	<version>1.0.0</version>
</dependency>
```

Use extractor

```java
import java.nio.file.Path;
import java.nio.file.Paths;

import com.sitepark.extractor.ContentTypeDetector;
import com.sitepark.extractor.Extractor;
import com.sitepark.extractor.types.DocInfo;

public class Main {

	public static void main(String[] argv) throws Exception {

		Path path = Paths.get(argv[0]);
		ContentTypeDetector detector = new ContentTypeDetector();

		String contentType = detector.detect(path);

		Extractor extractor = new Extractor();
		if (extractor.isSupported(contentType)) {
			FileInfo fileInfo = extractor.extract(path);
			if (fileInfo instanceof DocInfo) {
				DocInfo docInfo = (DocInfo) fileInfo;
				System.out.println("Document-Info: " + docInfo);
			} else {
				System.out.println("File-Info: " + fileInfo);
			}
		} else {
			System.out.println("Unsupported content-type: " + contentType);
		}
	}
}
```