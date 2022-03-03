package dev.neodym.limbo.world.block;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public record BlockSound(
    @NotNull Key breakSound,
    @NotNull Key stepSound,
    @NotNull Key placeSound,
    @NotNull Key hitSound,
    @NotNull Key fallSound,
    float volume,
    float pitch
) {

  public static @NotNull BlockSound EMPTY = new BlockSound(Key.key("none"), Key.key("none"), Key.key("none"), Key.key("none"), Key.key("none"), 1.0F, 1.0F);

}
