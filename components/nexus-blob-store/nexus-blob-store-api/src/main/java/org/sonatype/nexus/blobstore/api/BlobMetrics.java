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
