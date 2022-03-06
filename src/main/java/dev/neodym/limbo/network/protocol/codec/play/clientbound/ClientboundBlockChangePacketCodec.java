package dev.neodym.limbo.network.protocol.codec.play.clientbound;

import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.network.protocol.codec.PacketCodec;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundBlockChangePacket;
import dev.neodym.limbo.util.math.Vec3I;
import dev.neodym.limbo.world.block.preset.BlockPreset;
import io.netty.handler.codec.DecoderException;
import org.jetbrains.annotations.NotNull;

public class ClientboundBlockChangePacketCodec implements PacketCodec<ClientboundBlockChangePacket> {

  @Override
  public void encode(final @NotNull ClientboundBlockChangePacket packet, final @NotNull LimboByteBuf buf) {
    buf.write(packet.position().asLong());
    buf.write(packet.preset().id());
  }

  @NotNull
  @Override
  public ClientboundBlockChangePacket decode(final @NotNull LimboByteBuf buf) throws DecoderException {
    final Vec3I pos = Vec3I.fromLong(buf.read(long.class));
    final int blockId = buf.read(int.class);
    return new ClientboundBlockChangePacket(
        pos,
        BlockPreset.palette().byId(blockId).orElseThrow(() -> new DecoderException("No block with id %d registered.".formatted(blockId)))
    );
  }
}
