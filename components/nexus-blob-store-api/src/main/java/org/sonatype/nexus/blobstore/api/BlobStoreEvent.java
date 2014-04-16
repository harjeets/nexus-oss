package org.sonatype.nexus.blobstore.api;


import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

public class BlobStoreEvent
{
  private BlobId blobId;

  private Map<String, String> headers;

  private BlobMetrics metrics;

  private String message;

  BlobStoreEvent(final BlobId blobId, final Map<String, String> headers, final BlobMetrics metrics,
                 final String message)
  {
    Preconditions.checkNotNull(blobId);
    Preconditions.checkNotNull(headers);
    Preconditions.checkNotNull(metrics);
    this.blobId = blobId;
    this.headers = headers;
    this.metrics = metrics;
    this.message = message;
  }

  public BlobId getBlobId() {
    return blobId;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public BlobMetrics getMetrics() {
    return metrics;
  }

  /**
   * A BlobStore implementation-specific message.  It might describe, for example, which file a blob was stored in on
   * disk.
   */
  @Nullable
  public String getMessage() {
    return message;
  }
}
