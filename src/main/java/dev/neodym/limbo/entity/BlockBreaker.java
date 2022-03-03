package dev.neodym.limbo.entity;

import dev.neodym.limbo.server.LimboServer;
import dev.neodym.limbo.util.Vec3I;
import dev.neodym.limbo.world.block.Block;
import org.jetbrains.annotations.NotNull;

public interface BlockBreaker {

  void breakBlock(final @NotNull Block block);

  default void breakBlock(final @NotNull Vec3I position) {
    this.breakBlock(LimboServer.get().world().blockAt(position));
  }

}
