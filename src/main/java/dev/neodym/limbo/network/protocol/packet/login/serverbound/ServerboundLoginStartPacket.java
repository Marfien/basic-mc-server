package dev.neodym.limbo.network.protocol.packet.login.serverbound;

import dev.neodym.limbo.network.protocol.listener.PacketListener;
import dev.neodym.limbo.network.protocol.packet.Packet;
import org.jetbrains.annotations.NotNull;

public record ServerboundLoginStartPacket(
    @NotNull String username
) implements Packet {

  @Override
  public void handle(final @NotNull PacketListener listener) {
    listener.handleLoginStart(this);
  }
}
