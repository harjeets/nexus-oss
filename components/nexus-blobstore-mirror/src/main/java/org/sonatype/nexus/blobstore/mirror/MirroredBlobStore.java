/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2007-2014 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.blobstore.mirror;

import java.io.InputStream;
import java.util.Map;

import javax.annotation.Nullable;

import org.sonatype.nexus.blobstore.api.Blob;
import org.sonatype.nexus.blobstore.api.BlobId;
import org.sonatype.nexus.blobstore.api.BlobStore;
import org.sonatype.nexus.blobstore.api.BlobStoreMetrics;

import com.google.common.base.Preconditions;

/**
 * A blob store that reads and writes from a primary store, while mirroring writes to a backing store.
 *
 * @since 3.0
 */
public class MirroredBlobStore implements BlobStore
{
  private BlobStore primaryStore;
  private BlobStore mirrorStore;

  public MirroredBlobStore(final BlobStore primaryStore, final BlobStore mirrorStore) {
    Preconditions.checkNotNull(primaryStore);
    Preconditions.checkNotNull(mirrorStore);
    this.primaryStore = primaryStore;
    this.mirrorStore = mirrorStore;
  }

  @Override
  public Blob create(final InputStream inputStream, final Map<String, String> headers) {
    return null;
  }

  @Nullable
  @Override
  public Blob get(final BlobId blobId) {
    return null;
  }

  @Override
  public boolean delete(final BlobId blobId) {
    return false;
  }

  @Override
  public BlobStoreMetrics getMetrics() {
    return null;
  }
}
