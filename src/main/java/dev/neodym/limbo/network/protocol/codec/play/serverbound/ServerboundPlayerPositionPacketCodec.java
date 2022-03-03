package dev.neodym.limbo.network.protocol.codec.play.serverbound;

import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.network.protocol.codec.PacketCodec;
import dev.neodym.limbo.network.protocol.packet.play.serverbound.ServerboundPlayerPositionPacket;
import io.netty.handler.codec.DecoderException;
import org.jetbrains.annotations.NotNull;

public class ServerboundPlayerPositionPacketCodec implements PacketCodec<ServerboundPlayerPositionPacket> {


  @Override
  public void encode(final @NotNull ServerboundPlayerPositionPacket packet, final @NotNull LimboByteBuf buf) {
    buf.write(packet.x());
    buf.write(packet.y());
    buf.write(packet.z());
    buf.write(packet.onGround());
  }

  @Override
  public @NotNull ServerboundPlayerPositionPacket decode(final @NotNull LimboByteBuf buf) throws DecoderException {
    return new ServerboundPlayerPositionPacket(
        buf.read(double.class),
        buf.read(double.class),
        buf.read(double.class),
        buf.read(boolean.class)
    );
  }
}
