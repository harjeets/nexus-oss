package org.sonatype.nexus.blobstore.file;

import org.sonatype.nexus.blobstore.api.BlobId;

/**
 * Provides read/write locks for blobs.
 *
 * @since 3.0
 */
public interface BlobLockProvider
{
  BlobLock exclusiveLock(BlobId blobId);

  BlobLock readLock(BlobId blobId);
}
