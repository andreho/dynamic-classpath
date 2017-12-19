package net.andreho.dyn.classpath.impl;

import net.andreho.dyn.classpath.Entry;
import net.andreho.dyn.classpath.EntryFactory;

import java.net.URL;
import java.util.Optional;

/**
 * <br/>Created by a.hofmann on 18.07.2017 at 13:18.
 */
public class TempDirEntryFactory
  implements EntryFactory {
  private static final String SUPPORTED_PROTOCOL = "file";

  @Override
  public Optional<Entry> createFor(final String id,
                                  final ClassLoader classLoader,
                                  final URL classPathUrl) {
    if(!SUPPORTED_PROTOCOL.equalsIgnoreCase(classPathUrl.getProtocol())) {
      return Optional.empty();
    } 
    try {
      return Optional.of(new TempDirEntry(id, classLoader, classPathUrl));
    } catch (Exception e) {
      throw new IllegalStateException("Unable to create dynamic classpath-entry with id: "+id, e);
    }
  }
}
