package dev.neodym.limbo.util;

import org.jetbrains.annotations.Range;

public enum GameMode {
  UNDEFINED,
  SURVIVAL,
  CREATIVE,
  ADVENTURE,
  SPECTATOR;

  private final byte id;

  GameMode() {
    this.id = (byte) (this.ordinal() - 1);
  }

  public byte id() {
    return this.id;
  }

  public static GameMode byId(final @Range(from = -1, to = 3) int id) {
    return id == -1 ? UNDEFINED : values()[id];
  }

}
