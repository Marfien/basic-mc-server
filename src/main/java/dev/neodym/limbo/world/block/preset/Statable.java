package dev.neodym.limbo.world.block.preset;

import dev.neodym.limbo.util.math.Vec3I;
import dev.neodym.limbo.world.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public interface Statable {

  <T extends BlockState> T createState(final @NotNull Vec3I position);

  <T extends BlockState> T createState(final @NotNull Vec3I position, final @NotNull BlockState state);
}
