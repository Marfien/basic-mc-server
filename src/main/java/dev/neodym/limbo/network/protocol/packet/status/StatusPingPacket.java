package dev.neodym.limbo.network.protocol.packet.status;

import dev.neodym.limbo.network.protocol.listener.PacketListener;
import dev.neodym.limbo.network.protocol.packet.Packet;
import org.jetbrains.annotations.NotNull;

public record StatusPingPacket(
    long randomId
) implements Packet {

  @Override
  public void handle(final @NotNull PacketListener listener) {
    listener.handleStatusPing(this);
  }
}
