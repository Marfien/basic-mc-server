package dev.neodym.limbo.network.protocol.packet.login.clientbound;

import dev.neodym.limbo.network.protocol.packet.Packet;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public record ClientboundLoginDisconnectPacket(
    @NotNull Component reason
) implements Packet {

  public ClientboundLoginDisconnectPacket() {
    this(Component.text("Disconnect"));
  }

}
