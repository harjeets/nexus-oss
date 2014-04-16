package org.sonatype.nexus.blobstore.file;

import java.nio.file.Path;

import org.sonatype.nexus.blobstore.api.BlobId;

/**
 * Indicates where a blob with a given blob ID should be stored, relative to a blob store root. Implementations might
 * use algorithms to divide files up into multiple directories for easier management.
 *
 * @since 3.0
 */
public interface FileLocationPolicy
{
  /**
   * Returns a path to a file where the blob's content should be stored.
   */
  Path getBlobPath(Path dataDirectory, BlobId blobId);

  /**
   * Returns a path to a file where the blob's headers are stored.
   */
  Path getHeaderPath(Path dataDirectory, BlobId blobId);
}
