package dev.neodym.limbo.network.protocol.codec.play.clientbound;

import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.network.protocol.codec.PacketCodec;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundPlayerAbilitiesPacket;
import io.netty.handler.codec.DecoderException;
import org.jetbrains.annotations.NotNull;

public class ClientboundPlayerAbilitiesPacketCodec implements PacketCodec<ClientboundPlayerAbilitiesPacket> {

  @Override
  public void encode(final @NotNull ClientboundPlayerAbilitiesPacket packet, final @NotNull LimboByteBuf buf) {
    byte abilities = 0;

    if (packet.invulnerable()) abilities |= 1;
    if (packet.flying()) abilities |= 2;
    if (packet.canFly()) abilities |= 3;
    if (packet.instantBuild()) abilities |= 4;

    buf.write(abilities);
    buf.write(packet.flySpeed());
    buf.write(packet.walkingSpeed());
  }

  @Override
  public @NotNull ClientboundPlayerAbilitiesPacket decode(final @NotNull LimboByteBuf buf) throws DecoderException {
    return new ClientboundPlayerAbilitiesPacket(
        (buf.readMarked(byte.class) & 1) != 0,
        (buf.readMarked(byte.class) & 2) != 0,
        (buf.readMarked(byte.class) & 3) != 0,
        (buf.readMarked(byte.class) & 4) != 0,
        buf.read(float.class),
        buf.read(float.class)
    );
  }
}
