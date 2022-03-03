package dev.neodym.limbo.world.chunk;

import dev.neodym.javacommon.math.MathUtil;
import dev.neodym.limbo.network.protocol.packet.play.serverbound.ServerboundPlayerPositionPacket;
import dev.neodym.limbo.server.LimboServer;
import dev.neodym.limbo.world.World;
import dev.neodym.limbo.world.block.entity.BlockEntity;
import dev.neodym.limbo.world.block.preset.BlockPreset;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public class Chunk {

  public static final int CHUNK_WIDTH = 16;

  private final ChunkCoordinates coordinates;
  private final World world = LimboServer.get().world();

  private final ChunkSection[] sections = new ChunkSection[this.world.absoluteHeight() / CHUNK_WIDTH];
  private final HeightMap heightMap = new HeightMap(this);

  public Chunk(final int chunkX, final int chunkZ) {
    this(new ChunkCoordinates(chunkX, chunkZ));
  }

  public Chunk(final @NotNull Chunk.ChunkCoordinates coordinate) {
    this.coordinates = coordinate;
  }

  public @NotNull ChunkSection section(final int sectionY) {
    final int index = sectionY - Chunk.forChunk(this.world.minBuildHeight());
    return this.sections[index] == null ? this.sections[index] = ChunkSection.empty(this, sectionY) : this.sections[index];
  }

  private @NotNull Optional<ChunkSection> section0(final int sectionY) {
    final int index = sectionY - Chunk.forChunk(this.world.minBuildHeight());
    return Optional.ofNullable(this.sections[index]);
  }

  public @NotNull Chunk.ChunkCoordinates coordinates() {
    return this.coordinates;
  }

  public @NotNull BlockPreset preset(final int x, final int y, final int z) {
    final int sectionY = forChunk(y);
    return this.section0(sectionY).map(s -> s.preset(x, y - sectionY * 16, z)).orElse(BlockPreset.AIR);
  }

  void notifyBlockChange(final int x, final int y, final int z, final @NotNull BlockPreset preset, final @NotNull ChunkSection section) {
    this.heightMap.notify(x, y, z, preset);

    if (section.isEmpty()) {
      this.sections[section.sectionY() - Chunk.forChunk(this.world.minBuildHeight())] = null;
    }

    // TODO light engine
  }

  public @NotNull HeightMap heightMap() {
    return this.heightMap;
  }

  public int serializedSize() {
    return Arrays.stream(this.sections).filter(Objects::nonNull).mapToInt(ChunkSection::serializedSize).sum();
  }

  private int sectionIndex(final int y) {
    return Chunk.forChunk(y) - Chunk.forChunk(this.world.minBuildHeight());
  }

  public static record ChunkCoordinates(
      int chunkX,
      int chunkZ
  ) {

    public double distance(final int chunkX, final int chunkZ) {
      return Math.sqrt(
          MathUtil.square((double) this.chunkX - chunkX) +
          MathUtil.square((double) this.chunkZ - chunkZ)
      );
    }

    public int packed() {
      return packedXZ(this.chunkX, this.chunkZ);
    }

  }

  public static @NotNull Chunk empty(final @NotNull Chunk.ChunkCoordinates coordinate) {
    return new Chunk(coordinate);
  }

  private static int packedXZ(final int x, final int z) {
    return z * CHUNK_WIDTH + x;
  }

  public static int forChunk(final double coordinate) {
    return ((int) Math.floor(coordinate)) / 16;
  }

}
