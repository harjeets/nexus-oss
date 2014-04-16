package org.sonatype.nexus.blobstore.file;

import java.nio.file.Path;

import org.sonatype.nexus.blobstore.api.BlobId;

/**
 * Stores all the files in a single directory. An alternate implementation could compute a hash of the BlobId and
 * use that to distribute files across multiple directories.
 *
 * @since 3.0
 */
public class FlatDirectoryLocationPolicy
    implements FileLocationPolicy
{
  @Override
  public Path getHeaderPath(final Path dataDirectory, final BlobId blobId) {
    return dataDirectory.resolve(blobId.toString() + ".header");
  }

  @Override
  public Path getBlobPath(final Path dataDirectory, final BlobId blobId) {
    return dataDirectory.resolve(blobId.toString() + ".blob");
  }
}
