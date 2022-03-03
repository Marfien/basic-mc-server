package dev.neodym.limbo.network.protocol.codec.play.clientbound;

import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.network.protocol.codec.PacketCodec;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundNamedSoundEffectPacket;
import dev.neodym.limbo.util.Vec3D;
import io.netty.handler.codec.DecoderException;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.NotNull;

public class ClientboundNamedSoundEffectPacketCodec implements PacketCodec<ClientboundNamedSoundEffectPacket> {

  @Override
  public void encode(final @NotNull ClientboundNamedSoundEffectPacket packet, final @NotNull LimboByteBuf buf) {
    buf.write(packet.name().asString());
    buf.write(packet.source().ordinal());
    buf.write((int) packet.position().x() / ClientboundSoundPacketCodec.ACTUAL_POS_FACTOR);
    buf.write((int) packet.position().y() / ClientboundSoundPacketCodec.ACTUAL_POS_FACTOR);
    buf.write((int) packet.position().z() / ClientboundSoundPacketCodec.ACTUAL_POS_FACTOR);
    buf.write(packet.volume());
    buf.write(packet.pitch());
  }

  @Override
  public @NotNull ClientboundNamedSoundEffectPacket decode(final @NotNull LimboByteBuf buf) throws DecoderException {
    return new ClientboundNamedSoundEffectPacket(
        Key.key(buf.read(String.class)),
        Sound.Source.values()[buf.read(int.class)],
        new Vec3D(
            buf.read(double.class) * ClientboundSoundPacketCodec.ACTUAL_POS_FACTOR,
            buf.read(double.class) * ClientboundSoundPacketCodec.ACTUAL_POS_FACTOR,
            buf.read(double.class) * ClientboundSoundPacketCodec.ACTUAL_POS_FACTOR
        ),
        buf.read(float.class),
        buf.read(float.class)
    );
  }
}
