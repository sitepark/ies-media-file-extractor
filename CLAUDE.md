# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

`ies-media-file-extractor` is a Java 21 library by Sitepark that extracts metadata and text content from media files (PDF, Office formats, OpenDocument, RTF) using Apache Tika. It is a stateless library with no runtime configuration.

## Common Commands

```bash
# Build and test
mvn clean verify          # Full build with tests and all quality checks (use before committing)
mvn test                  # Run all tests
mvn test -Dtest=ExtractorTest                    # Run single test class
mvn test -Dtest=ExtractorTest#testIfSupported    # Run single test method

# Code quality
mvn spotless:apply        # Auto-format code (run before committing)
mvn spotless:check        # Check formatting without applying
mvn spotbugs:check        # SpotBugs static analysis
mvn pmd:check             # PMD analysis
mvn jacoco:report         # Generate coverage report (target/site/jacoco/)
```

## Architecture

The library implements a **factory + strategy pattern** for pluggable media type handling:

```
Extractor (orchestrator)
├── ContentTypeDetector → uses Tika DefaultDetector to detect MIME type
├── FileInfoFactory[] → registered factories, one per media type group
│   └── DocInfoFactory → handles PDF, Office, OpenDocument, RTF
└── ContentExtractorHandler → SAX handler that extracts plain text (100KB limit)
```

**Extraction flow:**
1. `Extractor` detects MIME type via `ContentTypeDetector`
2. Selects matching `FileInfoFactory` from registered list
3. Invokes Tika SAX parser with `ContentExtractorHandler` to capture text
4. Factory builds an immutable result object from Tika metadata + extracted text

**Result hierarchy:**
- `FileInfo` (abstract base, immutable)
  - `DocInfo` (document metadata: title, description, dates, content text) — uses Builder pattern, supports Jackson JSON serialization

**Key extension point:** Register additional `FileInfoFactory` implementations on `Extractor` to support new media types.

## Testing Approach

- Parameterized integration tests run against real files in `src/test/resources/files/docs/`
- Each test file has a paired `.expected.json` with the expected `DocInfo` output
- `FileInfoTestParameter` wires test file paths to expected JSON
- Timezone for tests is `Europe/Berlin` (configured in Surefire)

When adding support for a new file format: add a sample file + `.expected.json` to the test resources.

## Code Quality Configuration

- **SpotBugs**: Max effort, Low threshold; exclusions in `spotbugs-exclude-filter.xml`
- **PMD**: Custom ruleset in `pmd-ruleset.xml`; blocks on priority-1 violations
- **Spotless**: Google Java Format 1.31 — always run `spotless:apply` before committing
- **Enforcer**: Java 21 and Maven 3.8+ are required (build will fail otherwise)
