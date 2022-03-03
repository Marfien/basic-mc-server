package dev.neodym.limbo.network.protocol.packet.play.clientbound;

import dev.neodym.limbo.network.protocol.packet.Packet;
import dev.neodym.limbo.util.SoundEvent;
import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.NotNull;

public record ClientboundEntitySoundEffectPacket(
    @NotNull SoundEvent event,
    @NotNull Sound.Source source,
    int entityId,
    float volume,
    float pitch
    ) implements Packet {}
