package net.andreho.dyn.classpath.impl;

import net.andreho.dyn.classpath.Entry;
import net.andreho.dyn.classpath.EntryFactory;
import net.andreho.dyn.classpath.EntryInstaller;
import net.andreho.dyn.classpath.InstallationManager;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.util.Collections.unmodifiableList;

/**
 * <br/>Created by a.hofmann on 18.07.2017 at 11:28.
 */
public class InstallationManagerImpl
  implements InstallationManager {

  private final Collection<EntryFactory> availableEntryFactories;
  private final Collection<EntryInstaller<?>> availableEntryInstallers;
  private final ConcurrentMap<String, Entry> installedEntries = new ConcurrentHashMap<>();

  public InstallationManagerImpl() {
    this(Services.PROVIDED_INSTALLERS, Services.PROVIDED_FACTORIES);
  }

  public InstallationManagerImpl(final List<EntryInstaller<?>> availableEntryInstallers,
                                 final List<EntryFactory> availableEntryFactories) {
    this.availableEntryInstallers = unmodifiableList(new ArrayList<>(availableEntryInstallers));
    this.availableEntryFactories = unmodifiableList(new ArrayList<>(availableEntryFactories));
  }

  private Collection<EntryInstaller<?>> getAvailableEntryInstallers() {
    return availableEntryInstallers;
  }

  private Collection<EntryFactory> getAvailableEntryFactories() {
    return availableEntryFactories;
  }

  @Override
  public Result install(final String id,
                        final ClassLoader classLoader,
                        final URL classPathUrl) {
    for (EntryFactory factory : getAvailableEntryFactories()) {
      final Optional<Entry> entryOptional = factory.createFor(id, classLoader, classPathUrl);
      if (entryOptional.isPresent()) {
        return installEntry(entryOptional.get(), classLoader);
      }
    }
    return new Result(null, Outcome.UNSUPPORTED);
  }

  protected Result installEntry(final Entry entry,
                                final ClassLoader classLoader) {
    final Outcome outcome = proceedWithInstallation(classLoader, entry);
    if (outcome == Outcome.SUCCESSFUL) {
      this.installedEntries.merge(
        entry.getId(), entry, (current, newEntry) -> newEntry.merge(current)
      );
    } else if(outcome == Outcome.EXISTS) {
      return new Result(getEntryById(entry.getId()).get(), outcome);
    }
    return new Result(entry, outcome);
  }

  protected Outcome proceedWithInstallation(final ClassLoader classLoader,
                                            final Entry entry) {
    for (EntryInstaller entryInstaller : getAvailableEntryInstallers()) {
      if (entryInstaller.isCompatibleWith(classLoader)) {
        final Outcome outcome = entryInstaller.installOn(classLoader, entry);
        switch (outcome) {
          case FAILED:
          case EXISTS:
          case SUCCESSFUL:
            return outcome;
        }
      }
    }
    return Outcome.UNSUPPORTED;
  }

  public Iterable<Entry> getEntries() {
    return Collections.unmodifiableCollection(installedEntries.values());
  }

  public Optional<Entry> getEntryById(final String id) {
    return Optional.ofNullable(installedEntries.get(id));
  }
}
