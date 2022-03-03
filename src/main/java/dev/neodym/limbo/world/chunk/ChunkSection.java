package dev.neodym.limbo.world.chunk;

import dev.neodym.limbo.util.Vec3I;
import dev.neodym.limbo.world.block.Block;
import dev.neodym.limbo.world.block.entity.BlockEntity;
import dev.neodym.limbo.world.block.preset.BlockPreset;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class ChunkSection {

  private final Chunk chunk;
  private final int sectionY;

  private final BlockEntity[] blocks = new BlockEntity[coordinatesToIndex(15, 15, 15)];
  private int airCount = this.blocks.length;

  public @NotNull Block blockAt(final int x, final int y, final int z) {
    return new Block(
        this,
        new Vec3I(x, y, z),
        new Vec3I(
            x + (this.chunk.coordinates().chunkX() * Chunk.CHUNK_WIDTH),
            y + (this.sectionY * Chunk.CHUNK_WIDTH),
            z + (this.chunk.coordinates().chunkZ() * Chunk.CHUNK_WIDTH)
        )
    );
  }

  public @NotNull BlockPreset preset(final int x, final int y, final int z) {
    final BlockEntity entity = this.blocks[coordinatesToIndex(x, y, z)];
    return entity == null ? BlockPreset.AIR : entity.preset();
  }

  public synchronized void preset(final int x, final int y, final int z, final @NotNull BlockPreset preset) {
    final Optional<BlockEntity> optionalEntity = Optional.ofNullable(this.blocks[coordinatesToIndex(x, y, z)]);

    if (optionalEntity.isEmpty() && preset == BlockPreset.AIR) return;

    if (preset == BlockPreset.AIR) {
      this.set0(x, y, z, null);
    } else {
      final BlockEntity entity = optionalEntity.orElseGet(() -> this.putIfAbsent(x, y, z));
      entity.preset(preset);
    }

    this.chunk.notifyBlockChange(x, y + Chunk.CHUNK_WIDTH * this.sectionY, z, preset, this);
  }

  private @NotNull BlockEntity putIfAbsent(final int x, final int y, final int z) {
    final int index = coordinatesToIndex(x, y, z);
    final BlockEntity current = this.blocks[index];

    if (current != null) return current;

    final BlockEntity created = new BlockEntity(new Vec3I(x, y, z));
    this.set0(created);
    return created;
  }

  private void set0(final @NotNull BlockEntity entity) {
    final Vec3I position = entity.position();
    this.set0(position.x(), position.y(), position.z(), entity);
  }

  private void set0(final int x, final int y, final int z, final @Nullable BlockEntity entity) {
    final BlockEntity current = this.blocks[coordinatesToIndex(x, y, z)];

    if (current == null && entity == null) return;

    if (entity == null && current != null) {
      this.airCount++;
    }

    if (entity != null && current == null) {
      this.airCount--;
    }

    this.blocks[coordinatesToIndex(x, y, z)] = entity;
  }

  public int airCount() {
    return this.airCount;
  }

  public boolean isEmpty() {
    return this.airCount == this.blocks.length;
  }

  public boolean isFull() {
    return this.airCount == 0;
  }

  public int sectionY() {
    return this.sectionY;
  }

  public @NotNull Chunk chunk() {
    return this.chunk;
  }

  public int serializedSize() {
    return -1; // TODO
  }

  public static @NotNull ChunkSection empty(final @NotNull Chunk chunk, final int sectionY) {
    return new ChunkSection(chunk, sectionY);
  }

  private static int coordinatesToIndex(final int x, final int y, final int z) {
    if (x < 0 || z < 0 || y < 0 || x >= Chunk.CHUNK_WIDTH || z >= Chunk.CHUNK_WIDTH || y >= Chunk.CHUNK_WIDTH)
      throw new IndexOutOfBoundsException("Coordinates (x=%d,y=%d,z=%d) are invalid.".formatted(x, y, z));

    return (x * Chunk.CHUNK_WIDTH + z) * Chunk.CHUNK_WIDTH + y;
  }

  private static int coordinatesToIndex(final @NotNull Vec3I pos) {
    return coordinatesToIndex(pos.x(), pos.y(), pos.z());
  }
}
