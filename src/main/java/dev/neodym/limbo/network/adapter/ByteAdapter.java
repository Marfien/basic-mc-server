package dev.neodym.limbo.network.adapter;

import dev.neodym.limbo.network.LimboByteBuf;
import org.jetbrains.annotations.NotNull;

public class ByteAdapter implements ByteBufAdapter<Byte> {

  @Override
  public void write(final @NotNull Byte b, final @NotNull LimboByteBuf buf, final @NotNull Object... args) {
    buf.writeByte(b);
  }

  @Override
  public @NotNull Byte read(final @NotNull LimboByteBuf buf, final @NotNull Object... args) {
    return buf.readByte();
  }
}
