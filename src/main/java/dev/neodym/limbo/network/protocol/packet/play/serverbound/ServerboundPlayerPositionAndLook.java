package dev.neodym.limbo.network.protocol.packet.play.serverbound;

import dev.neodym.limbo.network.protocol.listener.PacketListener;
import dev.neodym.limbo.network.protocol.packet.Packet;
import dev.neodym.limbo.util.Position;
import org.jetbrains.annotations.NotNull;

public record ServerboundPlayerPositionAndLook(
    @NotNull Position position,
    boolean onGround
) implements Packet {

  @Override
  public void handle(final @NotNull PacketListener listener) {
    listener.handlePlayerPositionAndLookPacket(this);
  }
}
