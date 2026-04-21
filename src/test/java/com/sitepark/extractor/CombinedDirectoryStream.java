package com.sitepark.extractor;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Combines multiple DirectoryStreams into a single stream. Can be used directly in
 * try-with-resources statements.
 *
 * <p>Examples:
 *
 * <pre>
 * // With paths as strings
 * try (CombinedDirectoryStream combined = CombinedDirectoryStream.of(
 *         "/path/to/dir1", "/path/to/dir2")) {
 *     for (Path path : combined) {
 *         System.out.println(path);
 *     }
 * }
 *
 * // With Path objects
 * try (CombinedDirectoryStream combined = CombinedDirectoryStream.of(
 *         Paths.get("/dir1"), Paths.get("/dir2"))) {
 *     for (Path path : combined) {
 *         System.out.println(path);
 *     }
 * }
 * </pre>
 */
public final class CombinedDirectoryStream implements Iterable<Path>, AutoCloseable {

  private final List<DirectoryStream<Path>> streams;

  /**
   * Creates a combined stream from multiple DirectoryStreams.
   *
   * @param streams Any number of DirectoryStream objects
   */
  private CombinedDirectoryStream(DirectoryStream<Path>... streams) {
    this.streams = Arrays.asList(streams);
  }

  /**
   * Creates a combined stream from a list of DirectoryStreams.
   *
   * @param streamList A list of DirectoryStream objects
   */
  private CombinedDirectoryStream(List<DirectoryStream<Path>> streamList) {
    this.streams = new ArrayList<>(streamList);
  }

  // ========== FACTORY METHODS ==========

  /**
   * Creates a CombinedDirectoryStream from multiple paths (as strings).
   *
   * @param paths Any number of paths as strings
   * @return CombinedDirectoryStream
   * @throws IOException if a directory cannot be read
   */
  public static CombinedDirectoryStream of(String... paths) throws IOException {
    return of(Arrays.stream(paths).map(java.nio.file.Paths::get).toArray(Path[]::new));
  }

  /**
   * Creates a CombinedDirectoryStream from multiple Path objects.
   *
   * @param paths Any number of Path objects
   * @return CombinedDirectoryStream
   * @throws IOException if a directory cannot be read
   */
  public static CombinedDirectoryStream of(Path... paths) throws IOException {
    List<DirectoryStream<Path>> streams = new ArrayList<>();
    boolean success = false;

    try {
      for (Path path : paths) {
        streams.add(Files.newDirectoryStream(path));
      }
      success = true;
      return new CombinedDirectoryStream(streams);
    } finally {
      // Cleanup if something goes wrong
      if (!success) {
        IOException exception = null;
        for (DirectoryStream<Path> stream : streams) {
          try {
            stream.close();
          } catch (IOException e) {
            if (exception == null) {
              exception = e;
            } else {
              exception.addSuppressed(e);
            }
          }
        }
        if (exception != null) {
          throw exception;
        }
      }
    }
  }

  /**
   * Creates a CombinedDirectoryStream from multiple paths with a glob filter.
   *
   * @param glob Glob pattern (e.g., "*.txt")
   * @param paths Any number of paths as strings
   * @return CombinedDirectoryStream
   * @throws IOException if a directory cannot be read
   */
  public static CombinedDirectoryStream ofGlob(String glob, String... paths) throws IOException {
    return ofGlob(glob, Arrays.stream(paths).map(java.nio.file.Paths::get).toArray(Path[]::new));
  }

  /**
   * Creates a CombinedDirectoryStream from multiple Path objects with a glob filter.
   *
   * @param glob Glob pattern (e.g., "*.txt")
   * @param paths Any number of Path objects
   * @return CombinedDirectoryStream
   * @throws IOException if a directory cannot be read
   */
  public static CombinedDirectoryStream ofGlob(String glob, Path... paths) throws IOException {
    List<DirectoryStream<Path>> streams = new ArrayList<>();
    boolean success = false;

    try {
      for (Path path : paths) {
        streams.add(Files.newDirectoryStream(path, glob));
      }
      success = true;
      return new CombinedDirectoryStream(streams);
    } finally {
      if (!success) {
        IOException exception = null;
        for (DirectoryStream<Path> stream : streams) {
          try {
            stream.close();
          } catch (IOException e) {
            if (exception == null) {
              exception = e;
            } else {
              exception.addSuppressed(e);
            }
          }
        }
        if (exception != null) {
          throw exception;
        }
      }
    }
  }

  // ========== METHODS ==========

  /**
   * Returns an iterator over all paths from all streams.
   *
   * @return Iterator over all paths
   */
  @Override
  public Iterator<Path> iterator() {
    return new CombinedIterator(streams);
  }

  /**
   * Converts the combined stream into a Java Stream.
   *
   * @return Stream over all paths
   */
  public java.util.stream.Stream<Path> stream() {
    return java.util.stream.StreamSupport.stream(spliterator(), false);
  }

  /**
   * Closes all combined DirectoryStreams.
   *
   * @throws IOException if an error occurs while closing
   */
  @Override
  public void close() throws IOException {
    IOException exception = null;

    for (DirectoryStream<Path> stream : streams) {
      try {
        stream.close();
      } catch (IOException e) {
        if (exception == null) {
          exception = e;
        } else {
          exception.addSuppressed(e);
        }
      }
    }

    if (exception != null) {
      throw exception;
    }
  }

  /** Internal iterator that chains multiple iterators. */
  private static class CombinedIterator implements Iterator<Path> {
    private final Iterator<DirectoryStream<Path>> streamIterator;
    private Iterator<Path> currentIterator;

    CombinedIterator(List<DirectoryStream<Path>> streams) {
      this.streamIterator = streams.iterator();
      this.currentIterator = Collections.emptyIterator();
      advanceToNextStream();
    }

    @Override
    public boolean hasNext() {
      while (!currentIterator.hasNext() && streamIterator.hasNext()) {
        advanceToNextStream();
      }
      return currentIterator.hasNext();
    }

    @Override
    public Path next() {
      if (!hasNext()) {
        throw new NoSuchElementException("No more elements available");
      }
      return currentIterator.next();
    }

    private void advanceToNextStream() {
      if (streamIterator.hasNext()) {
        currentIterator = streamIterator.next().iterator();
      }
    }
  }
}
