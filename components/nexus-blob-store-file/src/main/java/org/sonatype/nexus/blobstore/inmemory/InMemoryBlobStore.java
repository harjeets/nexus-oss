package org.sonatype.nexus.blobstore.inmemory;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import org.sonatype.nexus.blobstore.api.Blob;
import org.sonatype.nexus.blobstore.api.BlobId;
import org.sonatype.nexus.blobstore.api.BlobMetrics;
import org.sonatype.nexus.blobstore.api.BlobStore;
import org.sonatype.nexus.blobstore.api.BlobStoreException;
import org.sonatype.nexus.blobstore.api.BlobStoreMetrics;
import org.sonatype.nexus.util.DigesterUtils;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.IOUtils;


/**
 * A fake implementation of the blob store to demonstrate usage scenarios.
 *
 * @since 3.0
 */
public class InMemoryBlobStore
    implements BlobStore
{
  private Map<BlobId, Blob> blobs = new ConcurrentHashMap<>();

  public InMemoryBlobStore() {
  }

  @Override
  public Blob create(final InputStream inputStream, final Map<String, String> headers) throws BlobStoreException {
    final BlobId blobId = new BlobId(UUID.randomUUID().toString());

    byte[] bytes = readBytes(inputStream);

    final InMemoryBlob blob = new InMemoryBlob(blobId, headers, bytes);
    blobs.put(blob.getId(), blob);
    return blob;
  }

  private byte[] readBytes(final InputStream inputStream) {
    try {
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      IOUtils.copy(inputStream, output);
      return output.toByteArray();
    }
    catch (IOException e) {
      throw new BlobStoreException(e);
    }
  }

  @Nullable
  @Override
  public Blob get(final BlobId blobId) throws BlobStoreException {
    return blobs.get(blobId);
  }

  @Override
  public boolean delete(final BlobId blobId) throws BlobStoreException {
    return blobs.remove(blobId) != null;
  }

  @Override
  public BlobStoreMetrics getMetrics() throws BlobStoreException {
    return null;
  }

  private static class InMemoryBlob
      implements Blob
  {
    private BlobId blobId;

    private Map<String, String> headers;

    private byte[] content;

    private Date creationTime = new Date();

    private InMemoryBlob(final BlobId blobId, final Map<String, String> headers, final byte[] content) {
      this.blobId = blobId;
      this.headers = ImmutableMap.copyOf(headers);
      this.content = content;
    }

    @Override
    public BlobId getId() {
      return blobId;
    }

    @Override
    public Map<String, String> getHeaders() {
      return headers;
    }

    @Override
    public InputStream getInputStream() throws BlobStoreException {
      return new ByteArrayInputStream(content);
    }

    @Override
    public BlobMetrics getMetrics() throws BlobStoreException {
      return new BlobMetrics()
      {
        @Override
        public Date getCreationTime() {
          return creationTime;
        }

        @Override
        public String getSHA1Hash() {
          return DigesterUtils.getSha1Digest(content);
        }

        @Override
        public long getHeaderSize() {
          try (ByteArrayOutputStream b = new ByteArrayOutputStream();
               BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(b))) {
            for (Entry<String, String> entry : headers.entrySet()) {
              bw.write(entry.getKey());
              bw.write('\n');
              bw.write(entry.getValue());
              bw.write('\n');
            }
            bw.flush();
            return b.toByteArray().length;
          }
          catch (IOException e) {
            throw new BlobStoreException(e);
          }
        }

        @Override
        public long getContentSize() {
          return content.length;
        }

        @Override
        public long getTotalSize() {
          return getHeaderSize() + getContentSize();
        }
      };
    }
  }
}
