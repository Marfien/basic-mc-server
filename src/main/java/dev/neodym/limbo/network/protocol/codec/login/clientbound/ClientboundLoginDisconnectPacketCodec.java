package dev.neodym.limbo.network.protocol.codec.login.clientbound;

import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.network.protocol.codec.PacketCodec;
import dev.neodym.limbo.network.protocol.packet.login.clientbound.ClientboundLoginDisconnectPacket;
import io.netty.handler.codec.DecoderException;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class ClientboundLoginDisconnectPacketCodec implements PacketCodec<ClientboundLoginDisconnectPacket> {

  @Override
  public void encode(final @NotNull ClientboundLoginDisconnectPacket packet, final @NotNull LimboByteBuf buf) {
    buf.write(packet.reason(), Component.class);
  }

  @Override
  public @NotNull ClientboundLoginDisconnectPacket decode(final @NotNull LimboByteBuf buf) throws DecoderException {
    return new ClientboundLoginDisconnectPacket(buf.read(Component.class));
  }
}
