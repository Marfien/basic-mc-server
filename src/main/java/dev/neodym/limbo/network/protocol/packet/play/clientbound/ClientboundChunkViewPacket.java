package dev.neodym.limbo.network.protocol.packet.play.clientbound;

import dev.neodym.limbo.network.protocol.packet.Packet;

public record ClientboundChunkViewPacket(
    int chunkX,
    int chunkY
) implements Packet {}
