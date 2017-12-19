package net.andreho.dyn.classpath.impl;

import net.andreho.dyn.classpath.EntryFactory;
import net.andreho.dyn.classpath.EntryInstaller;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * <br/>Created by a.hofmann on 18.07.2017 at 13:52.
 */
public class Services {
  static final List<EntryInstaller<?>> PROVIDED_INSTALLERS = Services.loadInstallerServices();
  static final List<EntryFactory> PROVIDED_FACTORIES = Services.loadFactoryServices();

  public static List<EntryInstaller<?>> loadInstallerServices() {
    final ServiceLoader<EntryInstaller> serviceLoader =
      ServiceLoader.load(EntryInstaller.class);

    final List<EntryInstaller<?>> entryInstallers = new ArrayList<>();
    for (EntryInstaller entryInstaller : serviceLoader) {
      entryInstallers.add(entryInstaller);
    }
    return entryInstallers;
  }

  public static List<EntryFactory> loadFactoryServices() {
    final ServiceLoader<EntryFactory> serviceLoader =
      ServiceLoader.load(EntryFactory.class);

    final List<EntryFactory> factories = new ArrayList<>();
    for (EntryFactory factory : serviceLoader) {
      factories.add(factory);
    }
    return factories;
  }
}
