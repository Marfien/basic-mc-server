package dev.neodym.limbo.network.adapter;

import dev.neodym.limbo.network.LimboByteBuf;
import java.util.BitSet;
import org.jetbrains.annotations.NotNull;

public class BitSetAdapter implements ByteBufAdapter<BitSet> {

  @Override
  public void write(final @NotNull BitSet bitSet, final @NotNull LimboByteBuf buf, final @NotNull Object... args) {
    buf.writeArray(bitSet.toLongArray());
  }

  @Override
  public @NotNull BitSet read(final @NotNull LimboByteBuf buf, final @NotNull Object... args) {
    return BitSet.valueOf(buf.readArray(long.class));
  }
}
