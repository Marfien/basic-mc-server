package dev.neodym.limbo.network.protocol.codec.status;

import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.network.protocol.codec.PacketCodec;
import dev.neodym.limbo.network.protocol.packet.status.StatusPingPacket;
import io.netty.handler.codec.DecoderException;
import org.jetbrains.annotations.NotNull;

public class StatusPingPacketCodec implements PacketCodec<StatusPingPacket> {

  @Override
  public void encode(final @NotNull StatusPingPacket packet, final @NotNull LimboByteBuf buf) {
    buf.write(packet.randomId());
  }

  @Override
  public @NotNull StatusPingPacket decode(final @NotNull LimboByteBuf buf) throws DecoderException {
    return new StatusPingPacket(buf.read(long.class));
  }
}
