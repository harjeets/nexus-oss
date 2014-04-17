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


import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.Random;

import org.sonatype.nexus.blobstore.api.Blob;
import org.sonatype.nexus.blobstore.inmemory.InMemoryBlobStore;
import org.sonatype.sisu.litmus.testsupport.TestSupport;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;

/**
 * @since 3.0
 */
public class MirroredBlobStoreIT extends TestSupport
{
  private Map<String, String> testHeaders;

  private byte[] testData;

  @Before
  public void setUp() {
    testHeaders = ImmutableMap.of("name", "children/artificial/pinocchio");
    new Random().nextBytes(testData);
  }

  @Test
  public void test() {
    final InMemoryBlobStore memStore1 = new InMemoryBlobStore();
    final InMemoryBlobStore memStore2 = new InMemoryBlobStore();

    final MirroredBlobStore mirrored = new MirroredBlobStore(memStore1, memStore2);

    final Blob blob = mirrored.create(new ByteArrayInputStream(testData), testHeaders);
  }
}
