package dev.neodym.limbo.util.palette;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public class GlobalPalette<T> {

  private final Object registryLock = new Object();

  private final List<T> byId = Lists.newLinkedList();
  private final Map<T, Integer> toId = Maps.newConcurrentMap();

  public int id(final @NotNull T t) {
    return this.toId.get(t);
  }

  public @NotNull Optional<T> byId(final int id) {
    try {
      return Optional.of(this.byId.get(id));
    } catch (final IndexOutOfBoundsException ignored) {
      return Optional.empty();
    }
  }

  public int register(final @NotNull T t) {
    synchronized (this.registryLock) {
      final int id = this.byId.size();
      this.byId.add(t);
      this.toId.put(t, id);

      // TODO set id to object?

      return id;
    }
  }

  public int size() {
    return this.byId.size();
  }

  public @NotNull Collection<T> values() {
    return Collections.unmodifiableCollection(this.byId);
  }

}
