package dev.neodym.limbo.network.protocol.packet.status;

import dev.neodym.limbo.network.protocol.packet.Packet;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public record ClientboundStatusResponsePacket(
    @NotNull String name,
    @Range(from = 0, to = Integer.MAX_VALUE) int protocol,
    @Range(from = 0, to = Integer.MAX_VALUE) int slots,
    @Range(from = 0, to = Integer.MAX_VALUE) int online,
    @NotNull List<String> sample,
    @NotNull Component description
) implements Packet {}
