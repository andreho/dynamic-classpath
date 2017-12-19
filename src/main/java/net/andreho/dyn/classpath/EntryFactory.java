package net.andreho.dyn.classpath;

import java.net.URL;
import java.util.Optional;

/**
 *
 */
@FunctionalInterface
public interface EntryFactory {

  /**
   * Creates an entry according to given parameters
   *
   * @param id           of new entry
   * @param classLoader  that should be associated with the new entry
   * @param classPathUrl that should be associated with the new entry
   * @return a new entry or {@link Optional#empty()} if not supported
   */
  Optional<Entry> createFor(String id,
                           ClassLoader classLoader,
                           URL classPathUrl);
}
