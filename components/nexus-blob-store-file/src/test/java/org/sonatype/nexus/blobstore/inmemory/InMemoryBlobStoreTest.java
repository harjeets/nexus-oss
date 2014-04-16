package org.sonatype.nexus.blobstore.inmemory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import org.sonatype.nexus.blobstore.api.Blob;
import org.sonatype.nexus.blobstore.api.BlobMetrics;
import org.sonatype.nexus.blobstore.api.BlobStore;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

/**
 * A prototype to experiment with usage scenarios.  This and the in-memory blob store will probably go away soon.
 *
 * @since 3.0
 */
public class InMemoryBlobStoreTest
{
  private BlobStore blobStore;

  @Before
  public void setUp() {
    blobStore = new InMemoryBlobStore();
  }

  @Test
  public void walkThroughUsage() throws Exception {

    final String content = "Hello";
    final InputStream inputStream = toInputStream(content);

    final Map<String, String> headers = ImmutableMap.of("name", "/sauce/chocolate/stale");
    final Blob blob = blobStore.create(inputStream, headers);
    Blob retrievedBlob = blobStore.get(blob.getId());

    String result = deserialize(retrievedBlob.getInputStream());
    assertThat(result, equalTo(content));
  }

  @Test
  public void testBlobMetrics() throws Exception {
    final String content = "My Content";
    final InputStream inputStream = toInputStream(content);

    final Blob blob = blobStore.create(inputStream, ImmutableMap.of("name", "/robots/neurotic/marvin"));

    final BlobMetrics metrics = blob.getMetrics();

    assertThat(metrics.getTotalSize(), greaterThan(metrics.getContentSize()));
    assertThat(metrics.getTotalSize(), greaterThan(metrics.getHeaderSize()));
  }

  private InputStream toInputStream(final Object object)
      throws IOException
  {
    return new ByteArrayInputStream(serialize(object));
  }

  private byte[] serialize(final Object object) throws IOException {
    ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
    try (ObjectOutputStream objectOutput = new ObjectOutputStream(byteOutput)) {
      objectOutput.writeObject(object);
    }
    return byteOutput.toByteArray();
  }

  private <O> O deserialize(final InputStream inputStream) throws IOException, ClassNotFoundException {
    try (ObjectInputStream o = new ObjectInputStream(inputStream)) {
      return (O) o.readObject();
    }
  }
}