package net.andreho.dyn.classpath.impl;

import net.andreho.dyn.classpath.Entry;

import java.net.URL;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import static java.util.Objects.requireNonNull;

/**
 * <br/>Created by a.hofmann on 18.07.2017 at 12:07.
 */
public abstract class AbstractEntry
  implements Entry {

  private final String id;
  private final URL classPathUrl;
  private final Set<ClassLoader> classLoaderSet;

  public AbstractEntry(final String id,
                       final ClassLoader classLoader,
                       final URL classPathUrl) {
    this.id = requireNonNull(id);
    this.classPathUrl = requireNonNull(classPathUrl);
    this.classLoaderSet = Collections.newSetFromMap(new WeakHashMap<>());
    this.classLoaderSet.add(classLoader);
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public Set<ClassLoader> getClassLoaders() {
    return Collections.unmodifiableSet(classLoaderSet);
  }

  @Override
  public URL getClassPathUrl() {
    return classPathUrl;
  }

  @Override
  public Entry merge(final Entry other) {
    if(other != null &&
       other != this) {
      verifyIds(other);
      verifyClassPaths(other);
      addClassLoadersSafely(other);
    }
    return this;
  }

  private void addClassLoadersSafely(final Entry other) {
    final Set<ClassLoader> classLoaderSet = this.classLoaderSet;
    synchronized (classLoaderSet) {
      classLoaderSet.addAll(other.getClassLoaders());
    }
  }

  private void verifyClassPaths(final Entry other) {
    if(!getClassPathUrl().equals(other.getClassPathUrl())) {
      throw new IllegalArgumentException(
        "Only entries with equal ClassPaths may be merged: "+getClassPathUrl()+" <> "+other.getClassPathUrl());
    }
  }

  private void verifyIds(final Entry other) {
    if(!other.getId().equals(other.getId())) {
      throw new IllegalArgumentException(
        "Only entries with equal IDs can be merged: "+getId()+" <> "+other.getId());
    }
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Entry)) {
      return false;
    }
    final Entry that = (Entry) o;
    if (!id.equals(that.getId())) {
      return false;
    }
    return classPathUrl.equals(that.getClassPathUrl());
  }

  @Override
  public int hashCode() {
    int result = id.hashCode();
    result = 31 * result + classPathUrl.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "Entry(" + id + ")";
  }
}
