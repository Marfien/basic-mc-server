package dev.neodym.limbo.network.protocol.codec.play;

import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.network.protocol.codec.PacketCodec;
import dev.neodym.limbo.network.protocol.packet.play.KeepAlivePacket;
import io.netty.handler.codec.DecoderException;
import org.jetbrains.annotations.NotNull;

public class KeepAlivePacketCodec implements PacketCodec<KeepAlivePacket> {


  @Override
  public void encode(final @NotNull KeepAlivePacket packet, final @NotNull LimboByteBuf buf) {
    buf.write(packet.id());
  }

  @Override
  public @NotNull KeepAlivePacket decode(final @NotNull LimboByteBuf buf) throws DecoderException {
    return new KeepAlivePacket(buf.read(long.class));
  }
}
