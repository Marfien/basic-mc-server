package dev.neodym.limbo.world.dimension;

import dev.neodym.limbo.util.palette.CodecGlobalPalette;
import dev.neodym.limbo.util.palette.KeyedBinaryTagLike;
import java.util.OptionalLong;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import org.jetbrains.annotations.NotNull;

@ToString
@EqualsAndHashCode
public final class DimensionType implements KeyedBinaryTagLike {

  private static final CodecGlobalPalette<DimensionType> PALETTE = new CodecGlobalPalette<>(Key.key("dimension_type"));

  public static final DimensionType OVERWORLD = new DimensionType(Key.key("overworld"), Key.key("infiniburn_overworld"), Key.key("overworld"), false, 384, 384, true, -64, true, OptionalLong.empty(), 1.0D, false, true, false, 0.0F, true, false);
  public static final DimensionType OVERWORLD_CAVES = new DimensionType(Key.key("overworld_caves"), Key.key("infiniburn_overworld"), Key.key("overworld"), false, 384, 384, true, -64, true, OptionalLong.empty(), 1.0D, false, true, true, 0.0F, true, false);
  public static final DimensionType NEATHER = new DimensionType(Key.key("the_nether"), Key.key("infiniburn_nether"), Key.key("the_nether"), true, 128, 256, false, 0, false, OptionalLong.of(18_000L), 8.0D, true, true, false, 0.10000000149011612F, false, true);
  public static final DimensionType THE_END = new DimensionType(Key.key("the_end"), Key.key("infiniburn_end"), Key.key("the_end"), false, 256, 256, false, 0, false, OptionalLong.of(6_000L), 1.0D, false, false, false, 0.0F, true, false);

  public static final DimensionType DEFAULT = OVERWORLD;

  public static @NotNull CodecGlobalPalette<DimensionType> palette() {
      return PALETTE;
  }

  private final Key key;
  private final Key infiniburn;
  private final Key effects;
  private final boolean ultrawarm;
  private final int logicalHeight;
  private final int height;
  private final boolean natural;
  private final int minY;
  private final boolean bedWorks;
  private final OptionalLong fixedTime;
  private final double coordinateScale;
  private final boolean piglinSafe;
  private final boolean hasSkylight;
  private final boolean hasCeiling;
  private final float ambientLight;
  private final boolean hasRaids;
  private final boolean respawnAnchorWorks;

  private final int id;
  private final CompoundBinaryTag asCompound;

  private DimensionType(
      @NotNull Key key,
      @NotNull Key infiniburn,
      @NotNull Key effects,
      boolean ultrawarm,
      int logicalHeight,
      int height,
      boolean natural,
      int minY,
      boolean bedWorks,
      @NotNull OptionalLong fixedTime,
      double coordinateScale,
      boolean piglinSafe,
      boolean hasSkylight,
      boolean hasCeiling,
      float ambientLight,
      boolean hasRaids,
      boolean respawnAnchorWorks
  ) {
    this.key = key;
    this.infiniburn = infiniburn;
    this.effects = effects;
    this.ultrawarm = ultrawarm;
    this.logicalHeight = logicalHeight;
    this.height = height;
    this.natural = natural;
    this.minY = minY;
    this.bedWorks = bedWorks;
    this.fixedTime = fixedTime;
    this.coordinateScale = coordinateScale;
    this.piglinSafe = piglinSafe;
    this.hasSkylight = hasSkylight;
    this.hasCeiling = hasCeiling;
    this.ambientLight = ambientLight;
    this.hasRaids = hasRaids;
    this.respawnAnchorWorks = respawnAnchorWorks;

    this.id = PALETTE.register(this);
    this.asCompound = createCompound(this);
  }

  @Override
  public @NotNull CompoundBinaryTag asBinaryTag() {
    return this.asCompound;
  }

  public @NotNull Key infiniburn() {
    return this.infiniburn;
  }

  public @NotNull Key effects() {
    return this.effects;
  }

  public boolean ultrawarm() {
    return this.ultrawarm;
  }

  public int logicalHeight() {
    return this.logicalHeight;
  }

  public int height() {
    return this.height;
  }

  public boolean natural() {
    return this.natural;
  }

  public int minY() {
    return this.minY;
  }

  public boolean bedWorks() {
    return this.bedWorks;
  }

  public double coordinateScale() {
    return this.coordinateScale;
  }

  public boolean piglinSafe() {
    return this.piglinSafe;
  }

  public boolean hasSkylight() {
    return this.hasSkylight;
  }

  public boolean hasCeiling() {
    return this.hasCeiling;
  }

  public float ambientLight() {
    return this.ambientLight;
  }

  public boolean hasRaids() {
    return this.hasRaids;
  }

  public boolean respawnAnchorWorks() {
    return this.respawnAnchorWorks;
  }

  @Override
  public @NotNull Key key() {
    return this.key;
  }

  public int id() {
    return this.id;
  }

  public @NotNull OptionalLong fixedTime() {
    return this.fixedTime;
  }

  private static @NotNull CompoundBinaryTag createCompound(final @NotNull DimensionType dimensionType) {
    final CompoundBinaryTag elementTag = CompoundBinaryTag.builder()
        .putString("infiniburn", dimensionType.infiniburn().asString())
        .putString("effects", dimensionType.effects().asString())
        .putBoolean("ultrawarm", dimensionType.ultrawarm())
        .putInt("logical_height", dimensionType.logicalHeight())
        .putInt("height", dimensionType.height())
        .putBoolean("natural", dimensionType.natural())
        .putInt("min_y", dimensionType.minY())
        .putBoolean("bed_works", dimensionType.bedWorks())
        .putDouble("coordinate_scale", dimensionType.coordinateScale())
        .putBoolean("piglin_safe", dimensionType.piglinSafe())
        .putBoolean("has_skylight", dimensionType.hasSkylight())
        .putBoolean("has_ceiling", dimensionType.hasSkylight())
        .putFloat("ambient_light", dimensionType.ambientLight())
        .putBoolean("has_raids", dimensionType.hasSkylight())
        .putBoolean("respawn_anchor_works", dimensionType.respawnAnchorWorks())
        .build();

    dimensionType.fixedTime().ifPresent(l -> elementTag.putLong("fixed_time", l));

    return CompoundBinaryTag.builder()
        .putString("name", dimensionType.key().asString())
        .putInt("id", dimensionType.id())
        .put("element", elementTag)
        .build();
  }

}
