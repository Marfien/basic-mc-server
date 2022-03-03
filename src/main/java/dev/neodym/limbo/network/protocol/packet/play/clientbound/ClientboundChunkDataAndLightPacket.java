package dev.neodym.limbo.network.protocol.packet.play.clientbound;

import com.google.common.collect.Lists;
import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.network.protocol.packet.Packet;
import dev.neodym.limbo.world.chunk.Chunk;
import dev.neodym.limbo.world.chunk.Chunk.ChunkCoordinates;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import org.jetbrains.annotations.NotNull;

public record ClientboundChunkDataAndLightPacket(
    ChunkCoordinates coordinates,
    ChunkData chunkData,
    LightData lightData
) implements Packet {

  public ClientboundChunkDataAndLightPacket(final @NotNull Chunk chunk) {
    this(chunk.coordinates(), new ChunkData(chunk), new LightData()); // TODO
  }

  private static class ChunkData {

    private final CompoundBinaryTag heightmaps;
    private final byte[] chunkData;
    private final Collection<BlockEntityInfo> blockData;
    // TODO are extra packets needed?

    public ChunkData(final @NotNull Chunk chunk) {
      // HeightMap
      this.heightmaps = CompoundBinaryTag.builder().putLongArray("MOTION_BLOCKING", chunk.heightMap().rawData()).build();

      // TODO ChunkData
      this.chunkData = new byte[0];
      this.blockData = Collections.emptyList();

      // TODO blockdata
    }

    private void write(final @NotNull LimboByteBuf buf) {
      buf.write(this.heightmaps, CompoundBinaryTag.class);
      buf.writeArray(this.chunkData);
      buf.writeArray(this.blockData.toArray(BlockEntityInfo[]::new), BlockEntityInfo::write);
    }

    private static class BlockEntityInfo {

      private void write(final @NotNull LimboByteBuf buf) {
        // TODO
      }

    }

  }

  @RequiredArgsConstructor
  public static class LightData {

    private final BitSet skyYMask;
    private final BitSet blockYMask;
    private final BitSet emptySkyYMask;
    private final BitSet emptyBlockYMask;
    private final List<byte[]> skyUpdates;
    private final List<byte[]> blockUpdates;
    private final boolean trustEdges;

    public LightData() {
      this(new BitSet(), new BitSet(), new BitSet(), new BitSet(), Lists.newArrayList(), Lists.newArrayList(), true);

      // TODO calculate light
    }

    private void write(final @NotNull LimboByteBuf buf) {
      buf.write(this.skyYMask);
      buf.write(this.blockYMask);
      buf.write(this.emptySkyYMask);
      buf.write(this.emptyBlockYMask);
      buf.writeArray(this.skyUpdates.toArray(byte[][]::new), (array, byteBuf) -> byteBuf.writeArray(array));
      buf.writeArray(this.blockUpdates.toArray(byte[][]::new), (array, byteBuf) -> byteBuf.writeArray(array));
      buf.write(this.trustEdges);
    }

  }

}
