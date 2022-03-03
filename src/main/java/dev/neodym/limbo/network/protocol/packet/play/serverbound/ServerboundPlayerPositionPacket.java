package dev.neodym.limbo.network.protocol.packet.play.serverbound;

import dev.neodym.limbo.network.protocol.listener.PacketListener;
import dev.neodym.limbo.network.protocol.packet.Packet;
import org.jetbrains.annotations.NotNull;

public record ServerboundPlayerPositionPacket(
    double x,
    double y,
    double z,
    boolean onGround
) implements Packet {

  @Override
  public void handle(final @NotNull PacketListener listener) {
    listener.handlePlayerPositionPacket(this);
  }
}
