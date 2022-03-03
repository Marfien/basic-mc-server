package dev.neodym.limbo.util.palette;

import dev.neodym.limbo.util.BitStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// TODO
public class PalettedContainer<T> {

  private final Palette<T> palette;
  private final BitStorage storage;

  public PalettedContainer(final @NotNull GlobalPalette<T> globalPalette) {
    this.palette = new DirektPalette<>(globalPalette);
    this.storage = new BitStorage(-1, -1);
  }

  public void set(final int x, final int y, final int z, final @Nullable T value) {

  }

  public void get(final int x, final int y, final int z) {

  }

  private int index(final int x, final int y, final int z) {
    return -1;
  }

}
