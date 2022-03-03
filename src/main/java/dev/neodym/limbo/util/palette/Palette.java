package dev.neodym.limbo.util.palette;

import dev.neodym.limbo.network.LimboByteBuf;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public interface Palette<T> {

  int id(final @NotNull T t);
  @NotNull Optional<T> byId(final int id);

  int register(final @NotNull T t);

  void read(final @NotNull LimboByteBuf buf);
  void write(final @NotNull LimboByteBuf buf);

  byte bitsPerBlock();

  int serializedSize();

  static <T> Palette<T> create(final @NotNull GlobalPalette<T> globalPalette, final byte bitsPerBlock) {
    if (bitsPerBlock <= 4) return new IndirectPalette<>(globalPalette, (byte) 4);
    if (bitsPerBlock <= 8) return new IndirectPalette<>(globalPalette, bitsPerBlock);

    return new DirektPalette<>(globalPalette);
  }

}
