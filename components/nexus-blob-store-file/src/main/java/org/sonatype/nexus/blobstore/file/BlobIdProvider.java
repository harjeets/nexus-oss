package org.sonatype.nexus.blobstore.file;

import org.sonatype.nexus.blobstore.api.BlobId;

/**
 * A factory for BlobIds.
 *
 * @since 3.0
 */
public interface BlobIdProvider
{
  BlobId createBlobId();
}
