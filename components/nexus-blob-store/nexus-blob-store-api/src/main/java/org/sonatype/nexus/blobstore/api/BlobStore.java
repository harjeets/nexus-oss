package org.sonatype.nexus.blobstore.api;

import java.io.InputStream;
import java.util.Map;

/**
 * <p>A generic storage bin for binary objects of all sizes.</p>
 *
 * <p>In general, most methods can throw BlobStoreException for conditions such as network connectivity problems, or
 * file IO issues, blob store misconfiguration, or internal corruption.</p>
 *
 * @since 3.0
 */
public interface BlobStore
{
  /**
   * Creates a new blob.
   *
   * Consider whether headers should turn into something more concrete, since these aren't supposed to be a dumping
   * ground for arbitrary metadata.
   *
   * Likely mandatory headers are:
   *
   * <ul>
   * <li>Identifying name for DR purposes (which isn't required to be strictly unique)</li>
   * <li>Audit information (e.g. the name of a principal)</li>
   * </ul>
   *
   * @throws BlobStoreException (or a subclass) if the input stream can't be read correctly, if mandatory headers are
   *                            missing,
   */
  Blob create(InputStream inputStream, Map<String, String> header) throws BlobStoreException;

  /**
   * @return <code>null</code> if there is no blob at that id.  (Consider, this is a weird thing to happen.)
   */
  Blob get(BlobId blobId) throws BlobStoreException;

  /**
   * Removes a blob from the blob store.
   *
   * @return <code>true</code> if the blob has been deleted, <code>false</code> if no blob was found by that ID.
   */
  boolean delete(BlobId blobId) throws BlobStoreException;

  BlobStoreMetrics getMetrics() throws BlobStoreException;
}
