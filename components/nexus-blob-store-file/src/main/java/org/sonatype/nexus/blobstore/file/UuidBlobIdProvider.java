package org.sonatype.nexus.blobstore.file;

import java.util.UUID;

import org.sonatype.nexus.blobstore.api.BlobId;

/**
 * A very simple blob ID provider that returns UUID-based BlobIDs.
 *
 * @since 3.0
 */
public class UuidBlobIdProvider
    implements BlobIdProvider
{
  @Override
  public BlobId createBlobId() {
    return new BlobId(UUID.randomUUID().toString());
  }
}
