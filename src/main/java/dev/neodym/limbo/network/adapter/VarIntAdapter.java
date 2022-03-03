package dev.neodym.limbo.network.adapter;

import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.server.LimboServer;
import org.jetbrains.annotations.NotNull;

public class VarIntAdapter implements ByteBufAdapter<Integer> {

  @Override
  public void write(final @NotNull Integer integer, final @NotNull LimboByteBuf buf, final @NotNull Object...args) {
    int value = integer;

    while ((value & -128) != 0) {
      buf.writeByte(value & 127 | 128);
      value >>>= 7;
    }

    buf.writeByte(value);
  }

  @Override
  public @NotNull Integer read(final @NotNull LimboByteBuf buf, final @NotNull Object...args) {
    int result = 0;
    int readBytes = 0;

    byte currentByte;

    do {
      currentByte = buf.readByte();
      result |= (currentByte & 127) << readBytes++ * 7;

      if (readBytes > 5) throw new RuntimeException("VarInt too big.");
    } while ((currentByte & 128) == 128);

    return result;
  }
}
