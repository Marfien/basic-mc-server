package dev.neodym.limbo.network.protocol.codec.login.serverbound;

import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.network.protocol.codec.PacketCodec;
import dev.neodym.limbo.network.protocol.packet.login.serverbound.ServerboundLoginPluginResponsePacket;
import io.netty.handler.codec.DecoderException;
import org.jetbrains.annotations.NotNull;

public class ServerboundLoginPluginResponsePacketCodec implements PacketCodec<ServerboundLoginPluginResponsePacket> {

  @Override
  public void encode(final @NotNull ServerboundLoginPluginResponsePacket packet, final @NotNull LimboByteBuf buf) {
    buf.write(packet.messageId());
    buf.write(packet.succeeded());
    buf.writeBytes(packet.data());
  }

  @Override
  public @NotNull ServerboundLoginPluginResponsePacket decode(final @NotNull LimboByteBuf buf) throws DecoderException {
    return new ServerboundLoginPluginResponsePacket(
        buf.read(int.class),
        buf.read(boolean.class),
        LimboByteBuf.create(buf.readBytes(buf.readableBytes()), buf.locale())
    );
  }
}
