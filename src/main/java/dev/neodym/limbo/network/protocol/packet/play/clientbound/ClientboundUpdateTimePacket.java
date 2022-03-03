package dev.neodym.limbo.network.protocol.packet.play.clientbound;

import dev.neodym.limbo.network.protocol.packet.Packet;

public record ClientboundUpdateTimePacket(
    long worldAge,
    long dayTime
) implements Packet {}
