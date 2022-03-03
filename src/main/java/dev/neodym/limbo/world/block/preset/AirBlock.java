package dev.neodym.limbo.world.block.preset;

import dev.neodym.limbo.entity.BlockBreaker;
import dev.neodym.limbo.entity.LivingEntity;
import dev.neodym.limbo.entity.Player;
import dev.neodym.limbo.item.ItemStack;
import dev.neodym.limbo.world.block.Block;
import dev.neodym.limbo.world.block.BlockFace;
import dev.neodym.limbo.world.block.BlockMaterial;
import dev.neodym.limbo.world.block.BlockSound;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public class AirBlock extends BlockPreset {

  public AirBlock(final @NotNull Key key, final @NotNull BlockMaterial material, final @NotNull BlockSound sound) {
    super(key, material, sound);
  }

  @Override
  public void interactLeft(@NotNull Player player, @NotNull Block block, @NotNull BlockFace face, @NotNull ItemStack item) {
    // Air cannot be destroyed and no sound should be played.
  }

  @Override
  public void breakBlock(@NotNull BlockBreaker breaker, @NotNull Block block) {
    // Air is cannot be broken and no sound should be played.
  }

  @Override
  public void onStep(@NotNull LivingEntity entity, @NotNull Block block) {
    // No sound should be played when walking on air
  }
}
