package dev.neodym.limbo.util.palette;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dev.neodym.limbo.network.LimboByteBuf;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class IndirectPalette<T> implements Palette<T> {

  private final Object registryLock = new Object();

  private final List<T> byId = Lists.newLinkedList();
  private final Map<T, Integer> toId = Maps.newConcurrentMap();

  private final GlobalPalette<T> globalPalette;
  private final byte bitsPerBlock;

  @Override
  public int id(final @NotNull T t) {
    return this.toId.get(t);
  }

  @Override
  public @NotNull Optional<T> byId(final int id) {
    try {
      return Optional.of(this.byId.get(id));
    } catch (final IndexOutOfBoundsException ignored) {
      return Optional.empty();
    }
  }

  @Override
  public int register(final @NotNull T t) {
    synchronized (this.registryLock) {
      final int id = this.byId.size();
      this.byId.add(t);
      this.toId.put(t, id);

      // TODO set id to object?

      return id;
    }
  }

  @Override
  public void read(final @NotNull LimboByteBuf buf) {
    this.byId.clear();
    this.toId.clear();

    final int len = buf.read(int.class);

    for (int i = 0; i < len; i++) {
      int globalId = buf.read(int.class);

      final Optional<T> t = this.globalPalette.byId(globalId);

      assert t.isPresent();

      this.register(t.get());
    }
  }

  @Override
  public void write(final @NotNull LimboByteBuf buf) {
    synchronized (this.registryLock) {
      if (this.byId.size() != this.toId.size()) throw new AssertionError("byId and toId have different sizes.");

      buf.write(this.byId.size());

      for (final T t : this.byId) {
        final int globalId = this.globalPalette.id(t);
        buf.write(globalId);
      }
    }
  }

  @Override
  public byte bitsPerBlock() {
    return this.bitsPerBlock;
  }

  @Override
  public int serializedSize() {
    synchronized (this.registryLock) {
      final int lengthSize = LimboByteBuf.varIntSize(this.byId.size());
      final int dataSize = this.byId.stream()
          .mapToInt(this.globalPalette::id)
          .map(LimboByteBuf::varIntSize)
          .sum();

      return lengthSize + dataSize;
    }
  }
}
