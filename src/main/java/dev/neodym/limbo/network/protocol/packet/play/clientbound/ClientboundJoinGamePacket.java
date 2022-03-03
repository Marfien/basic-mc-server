package dev.neodym.limbo.network.protocol.packet.play.clientbound;

import dev.neodym.limbo.network.protocol.packet.Packet;
import dev.neodym.limbo.util.GameMode;
import dev.neodym.limbo.world.dimension.DimensionType;
import java.util.List;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public record ClientboundJoinGamePacket(
    int entityId,
    boolean isHardcore,
    @NotNull GameMode gamemode,
    @NotNull GameMode previousGamemode,
    @NotNull List<Key> worldNames,
    @NotNull DimensionType dimensionType,
    @NotNull Key dimension,
    long hashedSeed,
    @Range(from = 2, to = Integer.MAX_VALUE) int viewDistance,
    boolean reducedDebugInfo,
    boolean enableRespawnScreen,
    boolean isDebugWorld,
    boolean isFlatWorld
) implements Packet {

  public ClientboundJoinGamePacket(final int entityId, final @NotNull GameMode gamemode, final @NotNull DimensionType dimensionType, final @NotNull List<Key> worldNames, final @NotNull Key dimension, final long hashedSeed) {
    this(
        entityId,
        false,
        gamemode,
        GameMode.UNDEFINED,
        worldNames,
        dimensionType,
        dimension,
        hashedSeed,
        8,
        true,
        false,
        false,
        true
    );
  }
}
