package dev.neodym.limbo.network.protocol.packet.play.clientbound;

import dev.neodym.limbo.network.protocol.packet.Packet;
import dev.neodym.limbo.util.SoundEvent;
import dev.neodym.limbo.util.Vec3D;
import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.NotNull;

public record ClientboundSoundPacket(
    @NotNull SoundEvent event,
    @NotNull Sound.Source source,
    @NotNull Vec3D position,
    float volume,
    float pitch
) implements Packet {}
