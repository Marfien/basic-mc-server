package dev.neodym.limbo.world.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dev.neodym.limbo.entity.Player;
import dev.neodym.limbo.network.protocol.PacketContainer;
import dev.neodym.limbo.network.protocol.packet.Packet;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundChunkDataAndLightPacket;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundChunkUnloadPacket;
import dev.neodym.limbo.server.LimboServer;
import dev.neodym.limbo.world.chunk.Chunk.ChunkCoordinates;
import java.util.Collection;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class ChunkMap {

  private final LimboServer server = LimboServer.get();
  private final Player holder;

  private final Map<ChunkCoordinates, Chunk> knownChunks = Maps.newConcurrentMap();

  public @NotNull Player holder() {
    return this.holder;
  }

  public void resend() {
    this.holder.connection().sendPacket(
        PacketContainer.of(
            this.knownChunks
                .values()
                .stream()
                .map(ClientboundChunkDataAndLightPacket::new)
                .toArray(Packet[]::new)
        )
    );
  }

  @Internal
  public void moveTo(final int chunkX, final int chunkZ) {
    if (!this.holder.connection().isConnected()) return;

    final Collection<ChunkCoordinates> nowKnownChunks = Lists.newArrayList();
    final int effectiveViewDistance = this.effectiveViewDistance();

    if (true) {
      // look for every chunk should now be loaded.
      for (int x = chunkX - effectiveViewDistance; x <= chunkX + effectiveViewDistance; x++) {
        for (int z = chunkZ - effectiveViewDistance; z <= chunkZ + effectiveViewDistance; z++) {
          final ChunkCoordinates coordinates = new ChunkCoordinates(x, z);

          nowKnownChunks.add(coordinates);
        }
      }
    } else {
      // TODO create a nice cycle instad of a rectangle.
      for (float h = 0.5F; h < effectiveViewDistance; h++) {
        final int s = (int) Math.round(Math.sqrt(h * h - effectiveViewDistance * effectiveViewDistance));
      }
    }

    // TODO: Check why the stream-api sucks

    // unload those, that aren't any more presented in nowKnownChunks, but in this.knownChunks.
    final Collection<ChunkCoordinates> unloading = Lists.newArrayList();
    for (final ChunkCoordinates coordinates : this.knownChunks.keySet()) {
      // If the value is known by both, nothing should happen
      if (nowKnownChunks.contains(coordinates)) continue;

      // If the value is presented in this.knownChunks, but not in nowKnownChunks, it has to be unloaded .
      unloading.add(coordinates);
      this.knownChunks.remove(coordinates);
    }

    this.holder.connection().sendPacket(
        PacketContainer.of(unloading.stream().map(ClientboundChunkUnloadPacket::new).toArray(Packet[]::new))
    );

    // load those, that aren't presented in this.knownChunks, but in nowKnownChunks
    final Collection<Chunk> loading = Lists.newArrayList();
    for (final ChunkCoordinates nowKnownChunk : nowKnownChunks) {
      // If the value is known by both, nothing should happen
      if (this.knownChunks.containsKey(nowKnownChunk)) continue;

      // If the value is unknown by this.knownChunks, the chunk has to be loaded.
      final Chunk chunk = this.server.world().chunkAt(nowKnownChunk);
      loading.add(chunk);
      this.knownChunks.put(nowKnownChunk, chunk);
    }

    // Flush changes to the client
    this.holder.connection().sendPacket(
        PacketContainer.of(loading.stream().map(ClientboundChunkDataAndLightPacket::new).toArray(Packet[]::new))
    );
  }

  public int effectiveViewDistance() {
    return Math.min(this.server.config().viewDistance(), this.holder.viewDistance() + 1);
  }

  public boolean isLoaded(final @NotNull ChunkCoordinates coordinates) {
    return this.knownChunks.containsKey(coordinates);
  }

}
