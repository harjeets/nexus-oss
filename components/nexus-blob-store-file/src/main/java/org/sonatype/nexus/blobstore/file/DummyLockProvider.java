package org.sonatype.nexus.blobstore.file;

import org.sonatype.nexus.blobstore.api.BlobId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 3.0
 */
public class DummyLockProvider
    implements BlobLockProvider
{
  private static final Logger logger = LoggerFactory.getLogger(DummyLockProvider.class);

  @Override
  public BlobLock readLock(final BlobId blobId) {
    return doNothingLock(blobId);
  }

  @Override
  public BlobLock exclusiveLock(final BlobId blobId) {
    return doNothingLock(blobId);
  }

  private BlobLock doNothingLock(final BlobId blobId) {
    logger.debug("Obtaining dummy lock for blob " + blobId);

    return new BlobLock()
    {
      @Override
      public void close() {
        logger.debug("Closing dummy lock for blob " + blobId);
      }
    };
  }

}
