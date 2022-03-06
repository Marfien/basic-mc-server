package dev.neodym.limbo.util.tablist;

import dev.neodym.limbo.auth.GameProfile;
import dev.neodym.limbo.entity.Player;
import dev.neodym.limbo.util.GameMode;
import dev.neodym.limbo.util.tablist.Tablist.TablistEntry;
import java.util.Optional;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public interface Tablist extends Iterable<TablistEntry> {

  void header(final @NotNull Component component);
  void footer(final @NotNull Component component);

  default void clearHeader() {
    this.header(Component.empty());
  }

  default void clearFooter() {
    this.footer(Component.empty());
  }

  @NotNull Component header();
  @NotNull Component footer();

  default void clearHeaderAndFooter() {
    this.clearFooter();
    this.clearHeader();
  }

  void updateGameMode(final @NotNull UUID uniqueId, final @NotNull GameMode mode);
  void updatePing(final @NotNull UUID uniqueId, final @Range(from = 0, to = Integer.MAX_VALUE) int ping);

  void updateCustomName(final @NotNull UUID uniqueId, final @NotNull Component component);
  void clearCustomName(final @NotNull UUID uniqueID);

  void addPlayer(final @NotNull Player player);
  void removeEntry(final @NotNull UUID uniqueId);

  @NotNull Optional<TablistEntry> entry(final @NotNull UUID uniqueId);

  default @NotNull TablistEntry entrySave(final @NotNull UUID uniqueId) {
    final Optional<TablistEntry> entry = this.entry(uniqueId);

    if (entry.isEmpty()) throw new IllegalArgumentException("No entry with id \"%s\" registered.".formatted(uniqueId));

    return entry.get();
  }

  boolean hasPlayer(final @NotNull UUID uniqueId);

  interface TablistEntry {

    default @NotNull UUID uniqueId() {
      return this.profile().uniqueId();
    }

    @NotNull GameProfile profile();
    @NotNull Optional<Component> customName();
    @NotNull GameMode gamemode();

    int ping();

  }

}
