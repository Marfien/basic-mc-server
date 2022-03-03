package dev.neodym.limbo.network.protocol.packet.login.clientbound;

import dev.neodym.limbo.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public record ClientboundLoginPluginRequestPacket(
    int messageId,
    @NotNull Key channel,
    ByteBuf data
) implements Packet {

  private static int counter = 0;

  public ClientboundLoginPluginRequestPacket(final @NotNull Key channel, final @NotNull ByteBuf data) {
    this(counter++, channel, data);
  }

  public ClientboundLoginPluginRequestPacket(final @NotNull Key channel) {
    this(channel, Unpooled.buffer());
  }
}
