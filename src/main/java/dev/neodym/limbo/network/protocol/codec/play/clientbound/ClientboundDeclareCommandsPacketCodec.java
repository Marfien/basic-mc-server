package dev.neodym.limbo.network.protocol.codec.play.clientbound;

import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.network.protocol.codec.PacketCodec;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundDeclareCommandsPacket;
import io.netty.handler.codec.DecoderException;
import org.jetbrains.annotations.NotNull;

// TODO implement command system
public class ClientboundDeclareCommandsPacketCodec implements PacketCodec<ClientboundDeclareCommandsPacket> {

  @Override
  public void encode(final @NotNull ClientboundDeclareCommandsPacket packet, final @NotNull LimboByteBuf buf) {
    buf.write(1);
    buf.write(0);
    buf.write(0);
    buf.write(0);
  }

  @Override
  public @NotNull ClientboundDeclareCommandsPacket decode(final @NotNull LimboByteBuf buf) throws DecoderException {
    return new ClientboundDeclareCommandsPacket();
  }
}
