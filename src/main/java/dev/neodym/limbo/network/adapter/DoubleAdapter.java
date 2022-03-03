package dev.neodym.limbo.network.adapter;

import dev.neodym.limbo.network.LimboByteBuf;
import org.jetbrains.annotations.NotNull;

public class DoubleAdapter implements ByteBufAdapter<Double> {

  @Override
  public void write(final @NotNull Double d, final @NotNull LimboByteBuf buf, final @NotNull Object... args) {
    buf.writeDouble(d);
  }

  @Override
  public @NotNull Double read(final @NotNull LimboByteBuf buf, final @NotNull Object... args) {
    return buf.readDouble();
  }
}
