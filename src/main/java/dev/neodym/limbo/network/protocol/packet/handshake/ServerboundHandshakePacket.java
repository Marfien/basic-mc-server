package dev.neodym.limbo.network.protocol.packet.handshake;

import dev.neodym.limbo.network.protocol.ConnectionState;
import dev.neodym.limbo.network.protocol.listener.PacketListener;
import dev.neodym.limbo.network.protocol.packet.Packet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public record ServerboundHandshakePacket(
    @Range(from = 0, to = Integer.MAX_VALUE) int protocolVersion,
    @NotNull String host,
    @Range(from = 0, to = Integer.MAX_VALUE) int port,
    @NotNull ConnectionState nextState
) implements Packet {

  @Override
  public void handle(final @NotNull PacketListener listener) {
    listener.handleHandshake(this);
  }

}
