package dev.neodym.limbo.network.protocol.packet.play.clientbound;

import dev.neodym.limbo.network.protocol.packet.Packet;
import dev.neodym.limbo.util.Vec3I;
import dev.neodym.limbo.world.block.preset.BlockPreset;
import org.jetbrains.annotations.NotNull;

public record ClientboundBlockChangePacket(
    @NotNull Vec3I position,
    @NotNull BlockPreset preset
) implements Packet {}
