package org.sonatype.nexus.blobstore.api;


import java.util.Date;
import java.util.Map;

/**
 *
 * @since 3.0
 */
public interface BlobMetrics
{
  Date getCreationTime();

  Date getLastAccessTime();

  /**
   * <p>Various blob store-calculated hashes of the blob (not the header), with the hash algorithm as key.</p>
   *
   * <p><code>metrics.getHashes("SHA1");</code></p>
   */
  Map<String,String> getHashes();

  long getHeaderSizeInBytes();

  /**
   *
   */
  long getBlobSizeInBytes();

  /**
   * An estimate of the blob's total consumption of storage space, including
   */
  long getBlobStorageSizeInBytes();

  long numberOfAccesses();
}
