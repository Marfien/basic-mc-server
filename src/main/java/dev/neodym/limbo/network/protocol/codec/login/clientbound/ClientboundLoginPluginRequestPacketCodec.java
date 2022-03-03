package dev.neodym.limbo.network.protocol.codec.login.clientbound;

import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.network.protocol.codec.PacketCodec;
import dev.neodym.limbo.network.protocol.packet.login.clientbound.ClientboundLoginPluginRequestPacket;
import io.netty.handler.codec.DecoderException;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public class ClientboundLoginPluginRequestPacketCodec implements PacketCodec<ClientboundLoginPluginRequestPacket> {

  @Override
  public void encode(final @NotNull ClientboundLoginPluginRequestPacket packet, final @NotNull LimboByteBuf buf) {
    buf.write(packet.messageId());
    buf.write(packet.channel().asString());
    buf.writeBytes(packet.data());
  }

  @Override
  public @NotNull ClientboundLoginPluginRequestPacket decode(final @NotNull LimboByteBuf buf) throws DecoderException {
    return new ClientboundLoginPluginRequestPacket(
        buf.read(int.class),
        Key.key(buf.read(String.class)),
        LimboByteBuf.create(buf.readBytes(buf.readableBytes()), buf.locale())
    );
  }
}
