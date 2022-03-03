package dev.neodym.limbo.world.block.state;

import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundBlockChangePacket;
import dev.neodym.limbo.server.LimboServer;
import dev.neodym.limbo.util.Vec3I;
import dev.neodym.limbo.world.block.preset.BlockPreset;
import org.jetbrains.annotations.NotNull;

class EmptyBlockState extends BlockState {

  EmptyBlockState(final @NotNull BlockPreset preset) {
    super(preset);
  }

  @Override
  public void tryUpdate(final @NotNull Vec3I position) {
    if (!this.updated) return;

    LimboServer.get().networkManager().spreadPacket(() -> new ClientboundBlockChangePacket(position, super.preset()));

    this.updated = true;
  }
}
