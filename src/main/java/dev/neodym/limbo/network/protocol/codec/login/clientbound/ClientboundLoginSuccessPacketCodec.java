package dev.neodym.limbo.network.protocol.codec.login.clientbound;

import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.network.protocol.codec.PacketCodec;
import dev.neodym.limbo.network.protocol.packet.login.clientbound.ClientboundLoginSuccessPacket;
import io.netty.handler.codec.DecoderException;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public class ClientboundLoginSuccessPacketCodec implements PacketCodec<ClientboundLoginSuccessPacket> {

  @Override
  public void encode(final @NotNull ClientboundLoginSuccessPacket packet, final @NotNull LimboByteBuf buf) {
    buf.write(packet.uniqueId());
    buf.write(packet.username());
  }

  @Override
  public @NotNull ClientboundLoginSuccessPacket decode(final @NotNull LimboByteBuf buf) throws DecoderException {
    return new ClientboundLoginSuccessPacket(
        buf.read(UUID.class),
        buf.read(String.class)
    );
  }
}
