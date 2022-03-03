package dev.neodym.limbo.network.adapter;

import dev.neodym.limbo.network.LimboByteBuf;
import org.jetbrains.annotations.NotNull;

public class LongAdapter implements ByteBufAdapter<Long> {

  @Override
  public void write(final @NotNull Long l, final @NotNull LimboByteBuf buf, final @NotNull Object... args) {
    buf.writeLong(l);
  }

  @Override
  public @NotNull Long read(final @NotNull LimboByteBuf buf, final @NotNull Object... args) {
    return buf.readLong();
  }
}
