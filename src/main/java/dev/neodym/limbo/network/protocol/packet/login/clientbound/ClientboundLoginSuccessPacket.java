package dev.neodym.limbo.network.protocol.packet.login.clientbound;

import dev.neodym.limbo.network.protocol.packet.Packet;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public record ClientboundLoginSuccessPacket(
    @NotNull UUID uniqueId,
    @NotNull String username
) implements Packet {}
