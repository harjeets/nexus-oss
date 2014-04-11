package org.sonatype.nexus.blobstore.api;

/**
 * @since 3.0
 */
public class BlobStoreException extends Exception
{
  public BlobStoreException(final String message) {
    super(message);
  }

  public BlobStoreException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public BlobStoreException(final Throwable cause) {
    super(cause);
  }
}
