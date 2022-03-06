package dev.neodym.limbo.world.block.preset;

import dev.neodym.limbo.entity.BlockBreaker;
import dev.neodym.limbo.entity.LivingEntity;
import dev.neodym.limbo.entity.Player;
import dev.neodym.limbo.item.Item;
import dev.neodym.limbo.item.ItemLike;
import dev.neodym.limbo.item.ItemStack;
import dev.neodym.limbo.server.LimboServer;
import dev.neodym.limbo.util.math.Vec3I;
import dev.neodym.limbo.util.palette.GlobalPalette;
import dev.neodym.limbo.world.block.Block;
import dev.neodym.limbo.world.block.BlockFace;
import dev.neodym.limbo.world.block.BlockMaterial;
import dev.neodym.limbo.world.block.BlockSound;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Source;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class BlockPreset implements ItemLike {

  public static final BlockPreset AIR = register(new AirBlock(Key.key("air"), BlockMaterial.AIR, BlockSound.EMPTY));

  private static final GlobalPalette<BlockPreset> PALETTE = new GlobalPalette<>();

  public static @NotNull GlobalPalette<BlockPreset> palette() {
    return PALETTE;
  }

  private static @NotNull BlockPreset register(final @NotNull BlockPreset preset) {
    preset.id = PALETTE.register(preset);

    return preset;
  }

  private final @NotNull Key key;
  private final @NotNull BlockMaterial material;
  private final @NotNull BlockSound sound;

  private int id;

  public void interactRight(final @NotNull Player player, final @NotNull Block block, final @NotNull BlockFace face, final @NotNull ItemStack item) {
    // by default nothing to do here
  }

  public void interactLeft(final @NotNull Player player, final @NotNull Block block, final @NotNull BlockFace face, final @NotNull ItemStack item) {
    LimboServer.get().world().playSound(this.sound(this.sound.hitSound()), block.position().asDouble());
  }

  public void breakBlock(final @NotNull BlockBreaker breaker, final @NotNull Block block) {
    LimboServer.get().world().playSound(this.sound(this.sound.breakSound()), block.position().asDouble());
  }

  public void breakBlock(final @NotNull Player player, final @NotNull Block block, final @NotNull BlockFace blockFace, final @NotNull ItemStack item) {
    this.breakBlock(player, block);
  }

  public boolean canPlacedAt(final @NotNull Player placer, final @NotNull Block block, final @NotNull BlockFace face) {
    // most blocks are placeable against any other block
    return true;
  }

  public void place(final @NotNull Player player, final @NotNull Vec3I position, final @NotNull ItemStack item) {
    // by default nothing to do here
  }

  public void onStep(final @NotNull LivingEntity entity, final @NotNull Block block) {
    LimboServer.get().world().playSound(this.sound(entity.fallDistance() > 2 ? this.sound.fallSound() : this.sound.stepSound()), entity.position());
  }

  protected @NotNull Sound sound(final @NotNull Key key) {
    return Sound.sound(key, Source.BLOCK, this.sound.volume(), this.sound.pitch());
  }

  @Override
  public @NotNull Item asItem() {
    return Item.byKey(this.key);
  }

  public @NotNull BlockSound sound() {
    return this.sound;
  }

  public @NotNull BlockMaterial material() {
    return this.material;
  }

  public @NotNull Key key() {
    return this.key;
  }

  public int id() {
    return this.id;
  }

}
