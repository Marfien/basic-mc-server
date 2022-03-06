package dev.neodym.limbo.world.block.state;

import dev.neodym.limbo.util.math.Vec3I;
import dev.neodym.limbo.world.block.preset.BlockPreset;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public abstract class BlockState {

  public static BlockState empty(final @NotNull BlockPreset preset) {
    return new EmptyBlockState(preset);
  }

  private @NotNull BlockPreset preset;
  protected boolean updated = false;

  public @NotNull BlockPreset preset() {
    return this.preset;
  }

  public abstract void tryUpdate(final @NotNull Vec3I position);
}
