package net.andreho.dyn.classpath;

import java.net.URL;
import java.util.Optional;

/**
 * <br/>Created by a.hofmann on 18.07.2017 at 11:22.
 */
public interface InstallationManager {
  /**
   * Tries to install a dynamic classpath entry using given parameters
   * @param id of entry to install or fetch
   * @param classLoader for installation
   * @param classPathUrl for installation
   * @return result of installation attempt
   */
  Result install(String id, ClassLoader classLoader, URL classPathUrl);

  /**
   * Wrapper for installation result
   */
  class Result {
    private final Entry entry;
    private final Outcome outcome;
    public Result(final Entry entry,
                  final Outcome outcome) {
      this.entry = entry;
      this.outcome = outcome;
    }

    /**
     * @return
     */
    public Optional<Entry> getEntry() {
      return Optional.ofNullable(entry);
    }

    /**
     * @return
     */
    public Outcome getOutcome() {
      return outcome;
    }

    /**
     * @return <b>true</b> if the user may
     */
    public boolean isSatisfiable() {
      return entry != null && (outcome == Outcome.SUCCESSFUL || outcome == Outcome.EXISTS);
    }
  }

  /**
   * Possible outcomes during an installation attempt
   * <br/>Created by a.hofmann on 18.07.2017 at 11:26.
   */
  enum Outcome {
    FAILED,
    UNSUPPORTED,
    EXISTS,
    SUCCESSFUL
  }
}
