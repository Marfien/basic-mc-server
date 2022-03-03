package dev.neodym.limbo.network.protocol.codec.play.clientbound;

import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.network.protocol.codec.PacketCodec;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundChunkViewPacket;
import io.netty.handler.codec.DecoderException;
import org.jetbrains.annotations.NotNull;

public class ClientboundChunkViewPacketCodec implements PacketCodec<ClientboundChunkViewPacket> {


  @Override
  public void encode(final @NotNull ClientboundChunkViewPacket packet, final @NotNull LimboByteBuf buf) {
    buf.write(packet.chunkX());
    buf.write(packet.chunkY());
  }

  @Override
  public @NotNull ClientboundChunkViewPacket decode(final @NotNull LimboByteBuf buf) throws DecoderException {
    return new ClientboundChunkViewPacket(
        buf.read(int.class),
        buf.read(int.class)
    );
  }
}
