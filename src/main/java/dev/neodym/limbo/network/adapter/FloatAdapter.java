package dev.neodym.limbo.network.adapter;

import dev.neodym.limbo.network.LimboByteBuf;
import org.jetbrains.annotations.NotNull;

public class FloatAdapter implements ByteBufAdapter<Float> {

  @Override
  public void write(final @NotNull Float f, final @NotNull LimboByteBuf buf, final @NotNull Object... args) {
    buf.writeFloat(f);
  }

  @Override
  public @NotNull Float read(final @NotNull LimboByteBuf buf, final @NotNull Object... args) {
    return buf.readFloat();
  }
}
