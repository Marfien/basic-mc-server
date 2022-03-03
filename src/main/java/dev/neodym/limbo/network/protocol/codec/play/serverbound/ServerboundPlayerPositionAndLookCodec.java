package dev.neodym.limbo.network.protocol.codec.play.serverbound;

import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.network.protocol.codec.PacketCodec;
import dev.neodym.limbo.network.protocol.packet.play.serverbound.ServerboundPlayerPositionAndLook;
import dev.neodym.limbo.util.Position;
import io.netty.handler.codec.DecoderException;
import org.jetbrains.annotations.NotNull;

public class ServerboundPlayerPositionAndLookCodec implements PacketCodec<ServerboundPlayerPositionAndLook> {


  @Override
  public void encode(final @NotNull ServerboundPlayerPositionAndLook packet, final @NotNull LimboByteBuf buf) {
    final Position position = packet.position();

    buf.write(position.x());
    buf.write(position.y());
    buf.write(position.z());
    buf.write(position.yaw());
    buf.write(position.pitch());
    buf.write(packet.onGround());
  }

  @Override
  public @NotNull ServerboundPlayerPositionAndLook decode(final @NotNull LimboByteBuf buf) throws DecoderException {
    return new ServerboundPlayerPositionAndLook(
        new Position(
            buf.read(double.class),
            buf.read(double.class),
            buf.read(double.class),
            buf.read(float.class),
            buf.read(float.class)
        ),
        buf.read(boolean.class)
    );
  }
}
