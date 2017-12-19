package net.andreho.dyn.classpath.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

/**
 * <br/>Created by a.hofmann on 18.07.2017 at 12:06.
 */
public class TempDirEntry
  extends AbstractEntry {
  private static final int WRITE_BUFFER_SIZE = 2048;
  private final Path target;

  private static URL createTempFolderAsUrl(String id)
  throws IOException {
    return Files.createTempDirectory(id + "_").toUri().toURL();
  }

  public TempDirEntry(final String id,
                      final ClassLoader classLoader)
  throws IOException, URISyntaxException {
    this(id, classLoader, createTempFolderAsUrl(id));
  }

  public TempDirEntry(final String id,
                      final ClassLoader classLoader,
                      final URL classPathUrl)
  throws URISyntaxException {
    super(id, classLoader, classPathUrl);
    if (!"file".equals(classPathUrl.getProtocol())) {
      throw new IllegalArgumentException("Unsupported protocol: " + classPathUrl.getProtocol());
    }
    this.target = Paths.get(classPathUrl.toURI());
  }

  @Override
  public boolean add(final InputStream inputStream,
                     final String first,
                     final String... rest)
  throws IOException {
    Path resolved = target.resolve(Paths.get(first, rest));

    if(Files.exists(resolved)) {
      return false;
    }

    if(rest.length > 0 || first.contains("/")) {
      if(!Files.isDirectory(resolved.getParent())) {
        Files.createDirectories(resolved.getParent());
      }
    }

    try(InputStream input = inputStream) {
      try(OutputStream outputStream = Files.newOutputStream(resolved, StandardOpenOption.CREATE_NEW)) {
        int read;
        final byte[] buffer = new byte[WRITE_BUFFER_SIZE];
        while ((read = input.read(buffer)) > -1) {
          outputStream.write(buffer, 0, read);
        }
      }
    }
    return true;
  }

  @Override
  public boolean add(final byte[] bytes,
                     final String first,
                     final String... rest)
  throws IOException {
    return add(new ByteArrayInputStream(bytes), first, rest);
  }

  @Override
  public Stream<String> stream()
  throws IOException {
    return Files.walk(target)
                .map(target::relativize)
                .map(Path::toString);
  }
}
