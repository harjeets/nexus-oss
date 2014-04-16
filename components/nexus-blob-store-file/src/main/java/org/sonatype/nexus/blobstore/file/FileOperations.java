package org.sonatype.nexus.blobstore.file;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Date;

/**
 * A wrapper around file operations to make mocking easier.
 *
 * @since 3.0
 */
public interface FileOperations
{
  /**
   * Creates a file (and its containing directories, if necessary) and populates it from the
   * InputStream, which gets closed.
   */
  void create(Path path, InputStream data) throws IOException;

  /**
   * Creates a file (and its containing directories, if necessary) and populates it from the byte array.
   */
  void create(Path path, byte[] data) throws IOException;

  boolean exists(Path path);

  InputStream openInputStream(Path path) throws IOException;

  Date fileCreationDate(Path path) throws IOException;

  String computeSha1Hash(Path path) throws IOException;

  /**
   * Returns true if the file existed before deletion, false otherwise.
   */
  boolean delete(Path path) throws IOException;

  long fileSize(Path path) throws IOException;
}
