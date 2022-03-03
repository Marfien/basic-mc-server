package dev.neodym.limbo.network.protocol.codec.play.clientbound;

import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.network.protocol.codec.PacketCodec;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundPlayerPositionPacket;
import dev.neodym.limbo.util.Position;
import io.netty.handler.codec.DecoderException;
import org.jetbrains.annotations.NotNull;

public class ClientboundPlayerPositionPacketCodec implements PacketCodec<ClientboundPlayerPositionPacket> {


  @Override
  public void encode(final @NotNull ClientboundPlayerPositionPacket packet, final @NotNull LimboByteBuf buf) {
    buf.write(packet.position().x());
    buf.write(packet.position().y());
    buf.write(packet.position().z());
    buf.write(packet.position().yaw());
    buf.write(packet.position().pitch());
    buf.write(packet.flags());
    buf.write(packet.teleportId());
    buf.write(packet.dismountVehicle());
  }

  @Override
  public @NotNull ClientboundPlayerPositionPacket decode(final @NotNull LimboByteBuf buf) throws DecoderException {
    return new ClientboundPlayerPositionPacket(
        new Position(
            buf.read(double.class),
            buf.read(double.class),
            buf.read(double.class),
            buf.read(float.class),
            buf.read(float.class)
        ),
        buf.read(byte.class),
        buf.read(int.class),
        buf.read(boolean.class)
    );
  }
}
