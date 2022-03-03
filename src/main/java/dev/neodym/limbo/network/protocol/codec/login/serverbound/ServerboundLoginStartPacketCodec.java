package dev.neodym.limbo.network.protocol.codec.login.serverbound;

import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.network.protocol.codec.PacketCodec;
import dev.neodym.limbo.network.protocol.packet.login.serverbound.ServerboundLoginStartPacket;
import io.netty.handler.codec.DecoderException;
import org.jetbrains.annotations.NotNull;

public class ServerboundLoginStartPacketCodec implements PacketCodec<ServerboundLoginStartPacket> {

  @Override
  public void encode(final @NotNull ServerboundLoginStartPacket packet, final @NotNull LimboByteBuf buf) {
    buf.write(packet.username());
  }

  @Override
  public @NotNull ServerboundLoginStartPacket decode(final @NotNull LimboByteBuf buf) throws DecoderException {
    return new ServerboundLoginStartPacket(buf.read(String.class));
  }
}
