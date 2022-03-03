package dev.neodym.limbo.network.adapter;

import dev.neodym.limbo.network.LimboByteBuf;
import org.jetbrains.annotations.NotNull;

public class ShortAdapter implements ByteBufAdapter<Short> {

  @Override
  public void write(final @NotNull Short s, final @NotNull LimboByteBuf buf, final @NotNull Object... args) {
    buf.writeShort(s);
  }

  @Override
  public @NotNull Short read(final @NotNull LimboByteBuf buf, final @NotNull Object... args) {
    return buf.readShort();
  }
}
