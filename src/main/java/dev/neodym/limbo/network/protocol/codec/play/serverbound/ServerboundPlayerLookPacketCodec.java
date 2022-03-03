package dev.neodym.limbo.network.protocol.codec.play.serverbound;

import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.network.protocol.codec.PacketCodec;
import dev.neodym.limbo.network.protocol.packet.play.serverbound.ServerboundPlayerLookPacket;
import io.netty.handler.codec.DecoderException;
import org.jetbrains.annotations.NotNull;

public class ServerboundPlayerLookPacketCodec implements PacketCodec<ServerboundPlayerLookPacket> {


  @Override
  public void encode(final @NotNull ServerboundPlayerLookPacket packet, final @NotNull LimboByteBuf buf) {
    buf.write(packet.yaw());
    buf.write(packet.pitch());
    buf.write(packet.onGround());
  }

  @Override
  public @NotNull ServerboundPlayerLookPacket decode(final @NotNull LimboByteBuf buf) throws DecoderException {
    return new ServerboundPlayerLookPacket(
        buf.read(float.class),
        buf.read(float.class),
        buf.read(boolean.class)
    );
  }
}
