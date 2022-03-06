package dev.neodym.limbo.world.biom;

import dev.neodym.limbo.util.palette.CodecGlobalPalette;
import dev.neodym.limbo.util.palette.KeyedBinaryTagLike;
import java.awt.Color;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import org.jetbrains.annotations.NotNull;

public final class Biome implements KeyedBinaryTagLike {

  private static final CodecGlobalPalette<Biome> PALETTE = new CodecGlobalPalette<>(Key.key("worldgen/biome"));

  public static final Biome THE_VOID = new Biome(Key.key("the_void"), Category.NONE, Precipitation.NONE, 0.5F, 0.5F, new BiomEffects(new Color(329011), new Color(4159204), new Color(12638463), new Color(8103167), new MoodSound(Key.key("ambient.cave"), 2, 8, 6_000)));
  public static final Biome PLAINS = new Biome(Key.key("plains"), Category.PLAINS, Precipitation.RAIN, 0.4000000059604645F, 0.800000011920929F, new BiomEffects(new Color(329011), new Color(4159204), new Color(12638463), new Color(7907327), new MoodSound(Key.key("ambient.cave"), 2, 8, 6_000)));

  public static @NotNull CodecGlobalPalette<Biome> palette() {
    return PALETTE;
  }

  private final @NotNull Key key;
  private final @NotNull Category category;
  private final @NotNull Precipitation precipitation;
  private final float downfall;
  private final float temperature;
  private final BiomEffects effects;

  private final int id;
  private final CompoundBinaryTag asCompound;

  private Biome(
      @NotNull Key key,
      @NotNull Category category,
      @NotNull Precipitation precipitation,
      float downfall,
      float temperature,
      @NotNull BiomEffects effects
  ) {
    this.key = key;
    this.id = PALETTE.register(this);
    this.category = category;
    this.precipitation = precipitation;
    this.downfall = downfall;
    this.temperature = temperature;
    this.effects = effects;

    this.asCompound = createComponent(this);
  }

  private static @NotNull CompoundBinaryTag createComponent(final @NotNull Biome biome) {
    return CompoundBinaryTag.builder()
        .putString("name", biome.key().asString())
        .putInt("id", biome.id())
        .put("element", CompoundBinaryTag.builder()
            .putString("category", biome.category().asName())
            .putString("precipitation", biome.precipitation().asName())
            .putFloat("downfall", biome.downfall())
            .putFloat("temperature", biome.temperature())
            .put("effects", biome.effects().asCompound())
            .build())
        .build();
  }

  public @NotNull Key key() {
    return this.key;
  }

  public int id() {
    return this.id;
  }

  public @NotNull Category category() {
    return this.category;
  }

  public @NotNull Precipitation precipitation() {
    return this.precipitation;
  }

  public float downfall() {
    return this.downfall;
  }

  public float temperature() {
    return this.temperature;
  }

  public @NotNull BiomEffects effects() {
    return this.effects;
  }

  @Override
  public @NotNull CompoundBinaryTag asBinaryTag() {
    return this.asCompound;
  }

  @RequiredArgsConstructor
  public enum Precipitation {

    NONE ("none"),
    RAIN ("rain"),
    SNOW ("snow");

    private final String name;

    public @NotNull String asName() {
      return this.name;
    }

  }

  @RequiredArgsConstructor
  public enum Category {

    NONE("none"),
    PLAINS("plains"),
    ICY("icy"),
    DESERT("desert"),
    SWAMP("swamp"),
    FOREST("forest"),
    TAIGA("taiga"),
    SAVANNA("savanna"),
    EXTREME_HILLS("extreme_hills"),
    JUNGLE("jungle"),
    MESA("mesa"),
    MOUNTAIN("mountain"),
    RIVER("river"),
    BEACH("beach"),
    OCEAN("ocean"),
    MUSHROOM("mushroom"),
    UNDERGROUND("underground"),
    NETHER("nether"),
    THE_END("the_end");

    private final String name;

    public @NotNull String asName() {
      return this.name;
    }

  }

  public record BiomEffects(
      Color waterFogColor,
      Color waterColor,
      Color fogColor,
      Color skyColor,
      @NotNull MoodSound moodSound
  ) {

    private @NotNull CompoundBinaryTag asCompound() {
      return CompoundBinaryTag.builder()
          .putInt("water_fog_color", this.waterFogColor.getRGB())
          .putInt("water_color", this.waterColor.getRGB())
          .putInt("fog_color", this.fogColor.getRGB())
          .putInt("sky_color", this.skyColor.getRGB())
          .put("mood_sound", this.moodSound.asCompound())
          .build();
    }

  }

  public record MoodSound(
      @NotNull Key key,
      double offset,
      int blockSearchExtend,
      int tickDelay
  ) implements Keyed {

    private @NotNull CompoundBinaryTag asCompound() {
      return CompoundBinaryTag.builder()
          .putString("sound", this.key.asString())
          .putDouble("offset", this.offset)
          .putInt("block_search_extend", this.blockSearchExtend)
          .putInt("tick_delay", this.tickDelay)
          .build();
    }

  }

}
