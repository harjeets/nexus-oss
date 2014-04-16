package org.sonatype.nexus.blobstore.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * Encapsulates an approach for transforming header information into an on-disk format. 
 *
 * @since 3.0
 */
public interface HeaderFileFormat
{
  void write(Map<String, String> headers, OutputStream outputStream) throws IOException;

  Map<String, String> read(InputStream inputStream) throws IOException;
}
