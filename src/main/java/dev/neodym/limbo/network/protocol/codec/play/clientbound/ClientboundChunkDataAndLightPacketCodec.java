package dev.neodym.limbo.network.protocol.codec.play.clientbound;

import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.network.protocol.codec.PacketCodec;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundChunkDataAndLightPacket;
import io.netty.handler.codec.DecoderException;
import org.jetbrains.annotations.NotNull;

public class ClientboundChunkDataAndLightPacketCodec implements PacketCodec<ClientboundChunkDataAndLightPacket> {

  @Override
  public void encode(final @NotNull ClientboundChunkDataAndLightPacket packet, final @NotNull LimboByteBuf buf) {
    buf.writeInt(packet.coordinates().chunkX());
    buf.writeInt(packet.coordinates().chunkZ());
    packet.chunkData().write(buf);
    packet.lightData().write(buf);
  }

  @Override
  public @NotNull ClientboundChunkDataAndLightPacket decode(final @NotNull LimboByteBuf buf) throws DecoderException {
    throw new UnsupportedOperationException("Not implemented yet.");
  }
}
