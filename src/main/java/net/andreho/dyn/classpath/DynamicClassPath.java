package net.andreho.dyn.classpath;

import net.andreho.dyn.classpath.impl.InstallationManagerImpl;

import java.util.Objects;

/**
 * <br/>Created by a.hofmann on 18.07.2017 at 13:20.
 */
public abstract class DynamicClassPath {
  volatile static InstallationManager INSTANCE = new InstallationManagerImpl();

  /**
   * Allows to override default manager instance
   * @param manager ot use
   */
  static void overrideDefaultManager(InstallationManager manager) {
    INSTANCE = Objects.requireNonNull(manager, "Given manager instance can't be null.");
  }

  /**
   * @return the default manager
   */
  public static InstallationManager defaultManager() {
    return INSTANCE;
  }

  private DynamicClassPath() {
    throw new UnsupportedOperationException();
  }
}
