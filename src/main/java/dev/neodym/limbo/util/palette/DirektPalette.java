package dev.neodym.limbo.util.palette;

import dev.neodym.limbo.network.LimboByteBuf;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class DirektPalette<T> implements Palette<T> {

  private final GlobalPalette<T> globalPalette;

  @Override
  public int id(final @NotNull T t) {
    return this.globalPalette.id(t);
  }

  @Override
  public @NotNull Optional<T> byId(final int id) {
    return this.globalPalette.byId(id);
  }

  @Override
  public int register(final @NotNull T t) {
    throw new UnsupportedOperationException("DirectPalette uses the global palette.");
  }

  @Override
  public byte bitsPerBlock() {
    return (byte) Math.ceil(Math.log(this.globalPalette.size()) / Math.log(2));
  }

  @Override
  public void read(final @NotNull LimboByteBuf buf) {
    // No data to read.
  }

  @Override
  public void write(final @NotNull LimboByteBuf buf) {
    // No data to write.
  }

  @Override
  public int serializedSize() {
    return 0;
  }
}
