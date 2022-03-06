package dev.neodym.limbo.world.block;

import dev.neodym.limbo.util.math.Vec3I;
import dev.neodym.limbo.world.chunk.Chunk;
import dev.neodym.limbo.world.chunk.ChunkSection;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class Block {

  private final ChunkSection section;
  private final Vec3I posInSection;
  private final Vec3I position;

  public @NotNull Vec3I position() {
    return this.position;
  }

  public @NotNull Vec3I posInSection() {
    return this.posInSection;
  }

  public @NotNull ChunkSection section() {
    return this.section;
  }

  public @NotNull Chunk chunk() {
    return this.section.chunk();
  }

}