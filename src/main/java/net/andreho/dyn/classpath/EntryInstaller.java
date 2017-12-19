package net.andreho.dyn.classpath;

/**
 * @param <C>
 */
@FunctionalInterface
public interface EntryInstaller<C extends ClassLoader> {
  /**
   * Checks the compatibility of this installer with the given classloader
   * @param classLoader to check
   * @return <b>true</b> if this installer can install an entry on the given classloader, <b>false</b> otherwise
   */
  default boolean isCompatibleWith(ClassLoader classLoader) {
    return true;
  }

  /**
   * @param classLoader to use for installation
   * @param entry       to install
   * @return value signalising status of this installation attempt.
   */
  InstallationManager.Outcome installOn(C classLoader, Entry entry);
}
