package dev.neodym.limbo.world.chunk;

import dev.neodym.limbo.server.LimboServer;
import dev.neodym.limbo.util.BitStorage;
import dev.neodym.limbo.world.World;
import dev.neodym.limbo.world.block.preset.BlockPreset;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class HeightMap {

  // the default height map indicates the highest solid block
  private static final Predicate<BlockPreset> DEFAULT_PREDICATE = preset -> preset.material().solid();

  private final World world = LimboServer.get().world();
  private final Predicate<BlockPreset> checkOpaque = DEFAULT_PREDICATE;
  private final BitStorage data = new BitStorage((int) Math.ceil(Math.log(this.world.absoluteHeight() + 1) / Math.log(2)), 256, null);

  private final Chunk chunk;

  void notify(final int x, final int y, final int z, final @NotNull BlockPreset preset) {
    int currentHeight = this.currentHeightAt(x, z);

    // If the new block is under our current highest block, it is not relevant for us
    if (y < currentHeight - 1) return;

    final boolean checkPassed = this.checkOpaque.test(preset);

    // If the block passes the check and is above the current height, it will be set as new height
    if (checkPassed && y >= currentHeight){
      this.setHeight(x, z, y + 1);
      return;
    }

    // If the block set is not relevant for this HeightMap, and it replaces currently the highest one, the new height has to be calculated.
    if (!checkPassed && currentHeight - 1 == y) {

      for (int i = y - 1; i >= this.world.minBuildHeight(); i--) {
        if (!this.checkOpaque.test(this.chunk.preset(x, i, z))) continue;

        this.setHeight(x, z, i + 1);
        return;
      }

      // If not block below was found, set it to the lowest value
      this.setHeight(x, z, this.world.minBuildHeight());
    }
  }

  public int currentHeightAt(final int x, final int z) {
    return this.data.get(index(x, z)) + this.world.minBuildHeight();
  }

  public long[] rawData() {
    return this.data.raw();
  }

  private void setHeight(final int x, final int z, final int height) {
    this.data.set(index(x, z), height - this.world.minBuildHeight());
  }

  private static int index(final int x, final int z) {
    return x + z * 16;
  }

}
