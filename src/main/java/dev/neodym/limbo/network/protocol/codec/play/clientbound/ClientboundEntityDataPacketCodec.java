package dev.neodym.limbo.network.protocol.codec.play.clientbound;

import dev.neodym.limbo.entity.data.EntityData;
import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.network.protocol.codec.PacketCodec;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundEntityDataPacket;
import io.netty.handler.codec.DecoderException;
import org.jetbrains.annotations.NotNull;

public class ClientboundEntityDataPacketCodec implements PacketCodec<ClientboundEntityDataPacket> {


  @Override
  public void encode(final @NotNull ClientboundEntityDataPacket packet, final @NotNull LimboByteBuf buf) {
    buf.write(packet.entityId());
    EntityData.writeTo(packet.data(), buf);
  }

  @NotNull
  @Override
  public ClientboundEntityDataPacket decode(final @NotNull LimboByteBuf buf) throws DecoderException {
    return new ClientboundEntityDataPacket(
        buf.read(int.class),
        EntityData.read(buf)
    );
  }
}
