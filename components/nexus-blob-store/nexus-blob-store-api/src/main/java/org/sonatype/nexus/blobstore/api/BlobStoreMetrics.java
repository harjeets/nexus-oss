package org.sonatype.nexus.blobstore.api;

/**
 * @since 3.0
 */
public interface BlobStoreMetrics
{
  long getBlobCount();

  /**
   * The total storage space consumed by this blob store, including blobs, headers, and any other metadata required by
   * the store.
   */
  long getTotalSizeInBytes();

  /**
   * An estimate of the remaining space available in the blob store. For certain blob stores like S3, this may return a
   * value set by a policy rather than some hard storage limit.
   */
  long getAvailableSpaceInBytes();

}
