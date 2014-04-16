package org.sonatype.nexus.blobstore.file;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * @since 3.0
 */
public class JsonHeaderFormatTest
{
  @Test
  public void simpleHeaders() throws IOException {
    final Map<String, String> map = ImmutableMap.of("hi", "bye");
    assertThat("headers", roundTripFormatting(map), equalTo(map));
  }

  @Test
  public void evilKeys() throws IOException {
    final Map<String, String> headers = ImmutableMap.of("\n\n''\"\"\"\"\t", "charlie");
    assertThat("evil headers", roundTripFormatting(headers), equalTo(headers));
  }

  private Map<String, String> roundTripFormatting(final Map<String, String> map) throws IOException {
    final JsonHeaderFormat formatter = new JsonHeaderFormat();

    final ByteArrayOutputStream b = new ByteArrayOutputStream();
    formatter.write(map, b);

    final byte[] bytes = b.toByteArray();

    final ByteArrayInputStream input = new ByteArrayInputStream(bytes);

    return formatter.read(input);
  }
}
