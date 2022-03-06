package dev.neodym.limbo.network.protocol.codec.play.clientbound;

import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.network.protocol.codec.PacketCodec;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundSoundPacket;
import dev.neodym.limbo.util.SoundEvent;
import dev.neodym.limbo.util.math.Vec3D;
import io.netty.handler.codec.DecoderException;
import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.NotNull;

public class ClientboundSoundPacketCodec implements PacketCodec<ClientboundSoundPacket> {

  static final double ACTUAL_POS_FACTOR = 8.0D;

  @Override
  public void encode(final @NotNull ClientboundSoundPacket packet, final @NotNull LimboByteBuf buf) {
    buf.write(packet.event().id());
    buf.write(packet.source().ordinal());
    buf.write((int) packet.position().x() * ACTUAL_POS_FACTOR);
    buf.write((int) packet.position().y() * ACTUAL_POS_FACTOR);
    buf.write((int) packet.position().z() * ACTUAL_POS_FACTOR);
    buf.write(packet.volume());
    buf.write(packet.pitch());
  }

  @NotNull
  @Override
  public ClientboundSoundPacket decode(final @NotNull LimboByteBuf buf) throws DecoderException {
    final int id = buf.read(int.class);

    return new ClientboundSoundPacket(
        SoundEvent.byId(id).orElseThrow(() -> new DecoderException("No SoundEvent with id %d presented.".formatted(id))),
        Sound.Source.values()[buf.read(int.class)],
        new Vec3D(
            buf.read(int.class) / ACTUAL_POS_FACTOR,
            buf.read(int.class) / ACTUAL_POS_FACTOR,
            buf.read(int.class) / ACTUAL_POS_FACTOR
        ),
        buf.read(float.class),
        buf.read(float.class)
    );
  }
}
