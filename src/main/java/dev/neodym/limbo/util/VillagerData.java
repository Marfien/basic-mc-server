package dev.neodym.limbo.util;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public record VillagerData(
    @NotNull VillagerType type,
    @NotNull VillagerProfession profession,
    @Range(from = 0, to = Integer.MAX_VALUE) int level
) {


  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public enum VillagerProfession {

    NONE (Key.key("none")),
    ARMORER (Key.key("armorer")),
    BUTCHER (Key.key("butcher")),
    CARTOGRAPHER (Key.key("cartographer")),
    CLERIC (Key.key("cleric")),
    FARMER (Key.key("farmer")),
    FISHERMAN (Key.key("fisherman")),
    FLETCHER (Key.key("fletcher")),
    LEATHERWORKER (Key.key("leatherworker")),
    LIBRARIAN (Key.key("librarian")),
    MASON (Key.key("mason")),
    NITWIT (Key.key("nitwirt")),
    SHEPHERD (Key.key("shepherd")),
    TOOLSMITH (Key.key("toolsmith")),
    WEAPONSMITH (Key.key("weaponsmith"));

    private final @NotNull Key key;

    public @NotNull Key key() {
      return this.key;
    }

    public int id() {
      return this.ordinal();
    }

  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public enum VillagerType {

    DESERT (Key.key("desert")),
    JUNGLE (Key.key("jungle")),
    PLAINS (Key.key("plains")),
    SAVANNA (Key.key("savanna")),
    SNOW (Key.key("snow")),
    SWAMP (Key.key("swamp")),
    TAIGA (Key.key("taiga"));

    private final Key key;

    public @NotNull Key key() {
      return this.key;
    }

    public @NotNull int id() {
      return this.ordinal();
    }

  }
}
