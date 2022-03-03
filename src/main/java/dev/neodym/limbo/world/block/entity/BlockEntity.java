package dev.neodym.limbo.world.block.entity;

import dev.neodym.limbo.server.Tickable;
import dev.neodym.limbo.util.Vec3I;
import dev.neodym.limbo.world.block.preset.BlockPreset;
import dev.neodym.limbo.world.block.preset.Statable;
import dev.neodym.limbo.world.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public final class BlockEntity implements Tickable {

  private final @NotNull Vec3I position;

  private @NotNull BlockPreset preset;
  private @NotNull BlockState state;

  public BlockEntity(final @NotNull Vec3I position) {
    this.position = position;
    this.preset(BlockPreset.AIR);
  }

  public @NotNull Vec3I position() {
    return this.position;
  }

  public @NotNull BlockState blockState() {
    return this.state;
  }

  public @NotNull BlockPreset preset() {
    return this.preset;
  }

  /**
   * Sets the preset of this {@link BlockEntity}.
   *
   * @param preset the new preset.
   * @return if the preset has changed.
   */
  public boolean preset(final @NotNull BlockPreset preset) {
    if (this.preset == preset) return false;

    this.forcePreset(preset);
    return true;
  }

  public void forcePreset(final @NotNull BlockPreset preset) {
    this.preset = preset;
    this.state = this.createNewState();
  }

  public void resetState() {
    this.state = this.createNewState();
  }

  private @NotNull BlockState createNewState() {
    return this.preset instanceof Statable statable ? statable.createState(this.position) : BlockState.empty(this.preset);
  }

  @Override
  public void tick() {
    this.state.tryUpdate(this.position);
  }
}
