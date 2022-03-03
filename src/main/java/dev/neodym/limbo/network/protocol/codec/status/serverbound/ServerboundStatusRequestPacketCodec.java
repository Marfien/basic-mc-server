package dev.neodym.limbo.network.protocol.codec.status.serverbound;

import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.network.protocol.codec.PacketCodec;
import dev.neodym.limbo.network.protocol.packet.status.ServerboundStatusRequestPacket;
import io.netty.handler.codec.DecoderException;
import org.jetbrains.annotations.NotNull;

public class ServerboundStatusRequestPacketCodec implements PacketCodec<ServerboundStatusRequestPacket> {

  @Override
  public void encode(final @NotNull ServerboundStatusRequestPacket packet, final @NotNull LimboByteBuf buf) {
    // Buf dose not contain any data.
  }

  @Override
  public @NotNull ServerboundStatusRequestPacket decode(final @NotNull LimboByteBuf buf) throws DecoderException {
    return new ServerboundStatusRequestPacket();
  }
}
