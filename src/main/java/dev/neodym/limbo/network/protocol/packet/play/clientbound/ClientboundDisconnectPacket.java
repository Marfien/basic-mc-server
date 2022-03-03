package dev.neodym.limbo.network.protocol.packet.play.clientbound;

import dev.neodym.limbo.network.protocol.packet.Packet;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public record ClientboundDisconnectPacket(
    @NotNull Component component
) implements Packet {}
