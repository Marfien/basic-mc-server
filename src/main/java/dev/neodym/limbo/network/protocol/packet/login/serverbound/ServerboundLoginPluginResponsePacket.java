package dev.neodym.limbo.network.protocol.packet.login.serverbound;

import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.network.protocol.listener.PacketListener;
import dev.neodym.limbo.network.protocol.packet.Packet;
import org.jetbrains.annotations.NotNull;

public record ServerboundLoginPluginResponsePacket(
    int messageId,
    boolean succeeded,
    LimboByteBuf data
) implements Packet {

  public ServerboundLoginPluginResponsePacket(final @NotNull LimboByteBuf buf) {
    this(
        buf.read(int.class),
        buf.read(boolean.class),
        LimboByteBuf.create(buf.readBytes(buf.readableBytes()), buf.locale())
    );
  }

  @Override
  public void handle(final @NotNull PacketListener listener) {
    listener.handleLoginPluginResponse(this);
  }
}
