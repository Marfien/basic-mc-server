package dev.neodym.limbo.network.protocol.packet.play;

import dev.neodym.limbo.network.protocol.listener.PacketListener;
import dev.neodym.limbo.network.protocol.packet.Packet;
import org.jetbrains.annotations.NotNull;

public record KeepAlivePacket(
    long id
) implements Packet {

  @Override
  public void handle(final @NotNull PacketListener listener) {
    listener.handleKeepAlive(this);
  }
}
