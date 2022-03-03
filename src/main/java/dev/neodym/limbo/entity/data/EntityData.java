package dev.neodym.limbo.entity.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dev.neodym.limbo.entity.Entity;
import dev.neodym.limbo.network.LimboByteBuf;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class EntityData {

  private final Map<Byte, EntityDataEntry<?>> entries = Maps.newConcurrentMap();
  private final @NotNull Entity entity;

  public @NotNull Entity entity() {
    return this.entity;
  }

  public boolean isEmpty() {
    return this.entries.isEmpty();
  }

  public <T> void set(final @NotNull EntityDataAccessor<T> accessor, final @NotNull T value) {
    if (this.entries.containsKey(accessor.fieldId())) {
      ((EntityDataEntry<T>) this.entries.get(accessor.fieldId())).value(value);
      return;
    }

    this.entries.put(accessor.fieldId(), new EntityDataEntry<>(accessor, value));
  }

  public <T> void remove(final @NotNull EntityDataAccessor<T> accessor) {
    this.entries.remove(accessor.fieldId());
  }

  public <T> Optional<T> get(final @NotNull EntityDataAccessor<T> accessor) {
    final EntityDataEntry<T> entry = (EntityDataEntry<T>) this.entries.get(accessor.fieldId());

    if (entry == null) return Optional.empty();

    return Optional.of(entry.value);
  }

  public void writeTo(final @NotNull LimboByteBuf buf) {
    writeTo(this.entries.values(), buf);
  }

  public void writeChangedTo(final @NotNull LimboByteBuf buf) {
    writeTo(
        this.entries.values()
            .stream()
            .filter(EntityDataEntry::hasChanged).toList(),
        buf
    );
  }

  public @NotNull Collection<EntityDataEntry<?>> all() {
    return this.entries.values();
  }

  public @NotNull Collection<EntityDataEntry<?>> allChanged() {
    return this.entries.values().stream().filter(EntityDataEntry::hasChanged).toList();
  }

  public static void writeTo(final Collection<EntityDataEntry<?>> entries, final @NotNull LimboByteBuf buf) {
    entries.forEach(entry -> entry.writeTo(buf));

    buf.writeByte((byte) 0xFF);
  }

  public static @NotNull Collection<EntityDataEntry<?>> read(final @NotNull LimboByteBuf buf) {
    throw new UnsupportedOperationException("Not implemented yet.");
  }

  public static final class EntityDataEntry<T> {

    private final @NotNull EntityDataAccessor<T> accessor;
    private @NotNull T value;

    private boolean hasChanged;

    public EntityDataEntry(
        final @NotNull EntityDataAccessor<T> accessor,
        final @NotNull T value
    ) {
      this.accessor = accessor;
      this.value = value;
      this.hasChanged = true;
    }

    public void writeTo(final @NotNull LimboByteBuf buf) {
      this.accessor.writeTo(buf, this.value);
    }

    public @NotNull EntityDataAccessor<T> accessor() {
      return this.accessor;
    }

    public boolean hasChanged() {
      return this.hasChanged;
    }

    public @NotNull T value() {
      return this.value;
    }

    private void value(final @NotNull T value) {
      this.value = value;
      this.hasChanged = true;
    }

    private void updated() {
      this.hasChanged = false;
    }

  }

}
