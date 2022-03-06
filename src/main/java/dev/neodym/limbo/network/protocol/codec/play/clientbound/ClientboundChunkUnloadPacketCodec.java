package dev.neodym.limbo.network.protocol.codec.play.clientbound;

import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.network.protocol.codec.PacketCodec;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundChunkUnloadPacket;
import dev.neodym.limbo.world.chunk.Chunk;
import io.netty.handler.codec.DecoderException;
import org.jetbrains.annotations.NotNull;

public class ClientboundChunkUnloadPacketCodec implements PacketCodec<ClientboundChunkUnloadPacket> {

  @Override
  public void encode(final @NotNull ClientboundChunkUnloadPacket packet, final @NotNull LimboByteBuf buf) {
    buf.writeInt(packet.coordinates().chunkX());
    buf.writeInt(packet.coordinates().chunkZ());
  }

  @Override
  public @NotNull ClientboundChunkUnloadPacket decode(final @NotNull LimboByteBuf buf) throws DecoderException {
    return new ClientboundChunkUnloadPacket(
        new Chunk.ChunkCoordinates(
            buf.readInt(),
            buf.readInt()
        )
    );
  }
}
