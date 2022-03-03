package dev.neodym.limbo.util.palette;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;

public class KeyedGlobalPalette<T extends Keyed> extends GlobalPalette<T> {

  private final Map<Key, T> byKey = Maps.newConcurrentMap();

  public @NotNull Optional<T> byKey(final @NotNull Key key) {
    return Optional.ofNullable(this.byKey.get(key));
  }

  public @NotNull Key key(final @NotNull T t) {
    return t.key();
  }

  @Override
  public int register(final @NotNull T t) {
    this.byKey.put(t.key(), t);
    return super.register(t);
  }
}
