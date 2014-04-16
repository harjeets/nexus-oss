package org.sonatype.nexus.blobstore.file;

/**
 * A shared or exclusive lock to control access to a Blob.
 * Implements {@link AutoCloseable} as syntactic sugar.
 *
 * @since 3.0
 */
public interface BlobLock
    extends AutoCloseable
{
  /**
   * Releases the lock.
   */
  @Override
  void close();
}
