package org.sonatype.nexus.blobstore.api;

import java.io.InputStream;
import java.util.Map;

/**
 * A wrapper for binary data stored within a {@link BlobStore}.
 *
 * @since 3.0
 */
public interface Blob
{
  BlobId getId();

  /**
   * An immutable map of the headers that were provided when the blob was created.
   */
  Map<String,String> getHeaders();

  /**
   * An input stream to the blob's bytes.
   */
  InputStream getInputStream() throws BlobStoreException;

  BlobMetrics getMetrics() throws BlobStoreException;
}
