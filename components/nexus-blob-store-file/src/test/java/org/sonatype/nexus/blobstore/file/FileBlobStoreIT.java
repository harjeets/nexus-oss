package org.sonatype.nexus.blobstore.file;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Random;

import org.sonatype.nexus.blobstore.api.Blob;
import org.sonatype.nexus.blobstore.api.BlobId;
import org.sonatype.nexus.blobstore.api.BlobMetrics;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.nullValue;

/**
 * Integration test for the file blob store. As the design becomes clearer, I expect to move functionality out of this
 * into unit tests.
 *
 * @since 3.0
 */
public class FileBlobStoreIT
{
  private FileBlobStore blobStore;

  private byte[] tinyBlob = new byte[100];

  @Before
  public void setUp() throws Exception {
    final Path fileBlobStore = Files.createTempDirectory("fileBlobStore");
    blobStore = new FileBlobStore(fileBlobStore, null);

    // Similarly, this will be replaced with mocking, or in a final IT, auto-injection
    blobStore.setBlobIdProvider(new UuidBlobIdProvider());
    blobStore.setFileLocationPolicy(new FlatDirectoryLocationPolicy());
    blobStore.setHeaderFormat(new JsonHeaderFormat());
    blobStore.setFileOperations(new SimpleFileOperations());
    blobStore.setLockProvider(new DummyLockProvider());

    new Random().nextBytes(tinyBlob);
  }

  @Test
  public void headersSurviveRoundTrip() throws Exception {

    final InputStream data = new ByteArrayInputStream(tinyBlob);
    final Map<String, String> headers = ImmutableMap.of("name", "tinyblob", "originalFilename", "image.jpg");

    final BlobId blobId = blobStore.create(data, headers).getId();

    final Blob blob = blobStore.get(blobId);
    final Map<String, String> retrievedHeaders = blob.getHeaders();

    assertThat("headers", retrievedHeaders, equalTo(headers));
  }

  @Test
  public void blobContentSurvivesRoundTrip() throws Exception {

    final InputStream data = new ByteArrayInputStream(tinyBlob);
    final Map<String, String> headers = ImmutableMap.of("name", "tinyblob");

    final BlobId id = blobStore.create(data, headers).getId();
    final Blob blob = blobStore.get(id);

    assertThat("foo", tinyBlob, equalTo(extractContentBytes(blob)));
  }

  @Test
  public void metricsAreCorrect() throws Exception {
    final InputStream data = new ByteArrayInputStream(tinyBlob);
    final Map<String, String> headers = ImmutableMap.of("name", "tinyblob");

    final Blob blob = blobStore.create(data, headers);

    final BlobMetrics metrics = blob.getMetrics();

    assertThat("content fileSize", metrics.getContentSize(), equalTo((long) tinyBlob.length));
    assertThat("totals fileSize", metrics.getTotalSize(), equalTo(metrics.getHeaderSize() + tinyBlob.length));


    final long oneHourAgo = System.currentTimeMillis() - 3600 * 1000;
    assertThat("creation date", metrics.getCreationTime().getTime(), greaterThan(oneHourAgo));
  }

  @Test
  public void deletedBlobsAreUnavailable() throws Exception {
    final ByteArrayInputStream data = new ByteArrayInputStream(tinyBlob);
    final Map<String, String> headers = ImmutableMap.of("name", "tinyblob");

    final BlobId id = blobStore.create(data, headers).getId();

    assertThat(blobStore.delete(id), equalTo(true));
    assertThat(blobStore.get(id), nullValue());
  }

  @Test
  public void deletingNonexistentBlob() throws Exception {
    final BlobId fakeBlobId = new BlobId("fake");

    assertThat(blobStore.delete(fakeBlobId), equalTo(false));
  }

  private byte[] extractContentBytes(final Blob blob) throws IOException {
    try (InputStream storedData = blob.getInputStream();
         ByteArrayOutputStream b = new ByteArrayOutputStream()) {
      IOUtils.copy(storedData, b);
      return b.toByteArray();
    }
  }
}
