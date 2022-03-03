package dev.neodym.limbo.world.block;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class BlockMaterial {

  public static final BlockMaterial AIR = new BlockMaterial.Builder().lightBlock(0).notSolid().replaceable().build();

  private final OnFireType onFireType;
  private final PushReaction pushReaction;
  private final float blastResistance;
  private final float strength;
  private final float slipperiness;
  private final int lightBlock;

  private final boolean hasGravity;
  private final boolean collider;
  private final boolean solid;
  private final boolean replaceable;

  public @NotNull OnFireType onFireType() {
    return this.onFireType;
  }

  public @NotNull PushReaction pushReaction() {
    return this.pushReaction;
  }

  public float blastResistance() {
    return this.blastResistance;
  }

  public float strength() {
    return this.strength;
  }

  public float slipperiness() {
    return this.slipperiness;
  }

  public int lightBlock() {
    return this.lightBlock;
  }

  public boolean hasGravity() {
    return this.hasGravity;
  }

  public boolean collider() {
    return this.collider;
  }

  public boolean solid() {
    return this.solid;
  }

  public boolean replaceable() {
    return this.replaceable;
  }

  public enum PushReaction {

    MOVE,
    DESTROY,
    BLOCK

  }

  public enum OnFireType {

    CATCH_FIRE,
    BURNABLE,
    IGNORED

  }

  private static class Builder {

    private OnFireType onFireType = OnFireType.IGNORED;
    private PushReaction pushReaction = PushReaction.MOVE;
    private float blastResistance = 1.0F;
    private float strength = 1.5F;
    private float slipperiness = 0.6F;
    private int lightBlock = 15;

    private boolean hasGravity = false;
    private boolean collider = false;
    private boolean solid = true;
    private boolean replaceable = false;

    public @NotNull Builder onFireType(final @NotNull OnFireType type) {
      this.onFireType = type;
      return this;
    }

    public @NotNull Builder pushReaction(final @NotNull PushReaction reaction) {
      this.pushReaction = reaction;
      return this;
    }

    public @NotNull Builder blastResistance(final float blastResistance) {
      this.blastResistance = blastResistance;
      return this;
    }

    public @NotNull Builder strength(final float strength) {
      this.strength = strength;
      return this;
    }

    public @NotNull Builder slipperiness(final float slipperiness) {
      this.slipperiness = slipperiness;
      return this;
    }

    public @NotNull Builder lightBlock(final int lightBlock) {
      this.lightBlock = lightBlock;
      return this;
    }

    public @NotNull Builder lightBlock(final float lightBlock) {
      this.lightBlock = Math.round(lightBlock * 15);
      return this;
    }

    public @NotNull Builder gravity() {
      this.hasGravity = true;
      return this;
    }

    public @NotNull Builder collider() {
      this.collider = true;
      return this;
    }

    public @NotNull Builder notSolid() {
      this.solid = false;
      return this;
    }

    public @NotNull Builder replaceable() {
      this.replaceable = true;
      return this;
    }

    public @NotNull BlockMaterial build() {
      return new BlockMaterial(
          this.onFireType,
          this.pushReaction,
          this.blastResistance,
          this.strength,
          this.slipperiness,
          this.lightBlock,
          this.hasGravity,
          this.collider,
          this.solid,
          this.replaceable
      );
    }

  }

}
