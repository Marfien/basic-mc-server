package dev.neodym.limbo.network.protocol.packet.play.clientbound;

import dev.neodym.limbo.network.protocol.packet.Packet;
import dev.neodym.limbo.world.chunk.Chunk.ChunkCoordinates;
import org.jetbrains.annotations.NotNull;

public record ClientboundChunkUnloadPacket(
    @NotNull ChunkCoordinates coordinates
    ) implements Packet {}
