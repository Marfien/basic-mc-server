package dev.neodym.limbo.network.protocol.codec.play.clientbound;

import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.network.protocol.codec.PacketCodec;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundUpdateTimePacket;
import io.netty.handler.codec.DecoderException;
import org.jetbrains.annotations.NotNull;

public class ClientboundUpdateTimePacketCodec implements PacketCodec<ClientboundUpdateTimePacket> {


  @Override
  public void encode(final @NotNull ClientboundUpdateTimePacket packet, final @NotNull LimboByteBuf buf) {
    buf.write(packet.worldAge());
    buf.write(packet.dayTime());
  }

  @Override
  public @NotNull ClientboundUpdateTimePacket decode(final @NotNull LimboByteBuf buf) throws DecoderException {
    return new ClientboundUpdateTimePacket(
        buf.read(long.class),
        buf.read(long.class)
    );
  }
}
