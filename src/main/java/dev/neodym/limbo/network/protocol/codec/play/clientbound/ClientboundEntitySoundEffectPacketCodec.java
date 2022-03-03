package dev.neodym.limbo.network.protocol.codec.play.clientbound;

import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.network.protocol.codec.PacketCodec;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundEntitySoundEffectPacket;
import dev.neodym.limbo.util.SoundEvent;
import io.netty.handler.codec.DecoderException;
import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.NotNull;

public class ClientboundEntitySoundEffectPacketCodec implements PacketCodec<ClientboundEntitySoundEffectPacket> {

  @Override
  public void encode(final @NotNull ClientboundEntitySoundEffectPacket packet, final @NotNull LimboByteBuf buf) {
    buf.write(packet.event().id());
    buf.write(packet.source().ordinal());
    buf.write(packet.entityId());
    buf.write(packet.volume());
    buf.write(packet.pitch());
  }

  @Override
  public @NotNull ClientboundEntitySoundEffectPacket decode(final @NotNull LimboByteBuf buf) throws DecoderException {
    final int soundId = buf.read(int.class);

    return new ClientboundEntitySoundEffectPacket(
        SoundEvent.byId(soundId).orElseThrow(() -> new DecoderException("No SoundEvent with id %d registered.".formatted(soundId))),
        Sound.Source.values()[buf.read(int.class)],
        buf.read(int.class),
        buf.read(float.class),
        buf.read(float.class)
    );
  }
}
