package dev.neodym.limbo.network.protocol.packet.play.clientbound;

import dev.neodym.limbo.network.protocol.packet.Packet;
import dev.neodym.limbo.util.Vec3D;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.NotNull;

public record ClientboundNamedSoundEffectPacket(
    @NotNull Key name,
    @NotNull Sound.Source source,
    @NotNull Vec3D position,
    float volume,
    float pitch
) implements Packet {

  public ClientboundNamedSoundEffectPacket(final @NotNull Sound sound, final @NotNull Vec3D position) {
    this(
        sound.name(),
        sound.source(),
        position,
        sound.volume(),
        sound.pitch()
    );
  }
}
