package dev.neodym.limbo.network.adapter;

import dev.neodym.limbo.network.LimboByteBuf;
import org.jetbrains.annotations.NotNull;

public class BooleanAdapter implements ByteBufAdapter<Boolean> {

  @Override
  public void write(final @NotNull Boolean b, final @NotNull LimboByteBuf buf, final @NotNull Object... args) {
    buf.writeBoolean(b);
  }

  @Override
  public @NotNull Boolean read(final @NotNull LimboByteBuf buf, final @NotNull Object... args) {
    return buf.readBoolean();
  }
}
