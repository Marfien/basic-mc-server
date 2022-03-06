package dev.neodym.limbo.util.tablist;

import com.google.common.collect.Maps;
import dev.neodym.limbo.auth.GameProfile;
import dev.neodym.limbo.entity.Player;
import dev.neodym.limbo.entity.data.EntityDataAccessor;
import dev.neodym.limbo.network.NetworkManager;
import dev.neodym.limbo.network.PlayerConnection;
import dev.neodym.limbo.network.protocol.packet.Packet;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundPlayerInfoPacket;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundPlayerInfoPacket.Action;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundTablistLayoutPacket;
import dev.neodym.limbo.server.LimboServer;
import dev.neodym.limbo.util.GameMode;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

@RequiredArgsConstructor
public class GlobalTablist implements Tablist {

  private final @NotNull NetworkManager manager;

  private @NotNull Component header = Component.empty();
  private @NotNull Component footer = Component.empty();

  private final Map<UUID, GlobalTablistEntry> entries = Maps.newConcurrentMap();

  @Override
  public void header(final @NotNull Component component) {
    this.header = component;

    this.spreadPacket(() -> new ClientboundTablistLayoutPacket(this.header, this.footer));
  }

  @Override
  public void footer(final @NotNull Component component) {
    this.footer = component;

    this.spreadPacket(() -> new ClientboundTablistLayoutPacket(this.header, this.footer));
  }
  @Override
  public @NotNull Component header() {
    return this.header;
  }

  @Override
  public @NotNull Component footer() {
    return this.footer;
  }

  @Override
  public void updateGameMode(final @NotNull UUID uniqueId, final @NotNull GameMode mode) {
    final GlobalTablistEntry entry = (GlobalTablistEntry) this.entrySave(uniqueId);
    entry.gamemode(mode);

    this.spreadPacket(() -> new ClientboundPlayerInfoPacket(Action.UPDATE_GAMEMODE, entry));
  }

  @Override
  public void updatePing(final @NotNull UUID uniqueId, final @Range(from = 0, to = Integer.MAX_VALUE) int ping) {
    final GlobalTablistEntry entry = (GlobalTablistEntry) this.entrySave(uniqueId);
    entry.ping(ping);

    this.spreadPacket(() -> new ClientboundPlayerInfoPacket(Action.UPDATE_PING, entry));
  }

  @Override
  public void updateCustomName(final @NotNull UUID uniqueId, final @NotNull Component component) {
    final GlobalTablistEntry entry = (GlobalTablistEntry) this.entrySave(uniqueId);
    entry.customName(component);

    this.spreadPacket(() -> new ClientboundPlayerInfoPacket(Action.UPDATE_CUSTOM_NAME, entry));
  }

  @Override
  public void clearCustomName(@NotNull UUID uniqueID) {
    final GlobalTablistEntry entry = (GlobalTablistEntry) this.entrySave(uniqueID);
    entry.customName(null);

    this.spreadPacket(() -> new ClientboundPlayerInfoPacket(Action.UPDATE_CUSTOM_NAME, entry));
  }

  @Override
  public synchronized void addPlayer(final @NotNull Player player) {
    if (this.entries.containsKey(player.uniqueId())) throw new IllegalStateException("An entry with the id \"%s\" is already registered.");
    final GlobalTablistEntry entry = new GlobalTablistEntry(player);

    this.entries.put(player.uniqueId(), entry);

    this.spreadPacket(() -> new ClientboundPlayerInfoPacket(Action.ADD_PLAYER, entry));
  }

  @Override
  public void removeEntry(final @NotNull UUID uniqueId) {
    final GlobalTablistEntry entry = this.entries.remove(uniqueId);
    this.spreadPacket(() -> new ClientboundPlayerInfoPacket(Action.ADD_PLAYER, entry));
  }

  @Override
  public @NotNull Optional<TablistEntry> entry(final @NotNull UUID uniqueId) {
    return Optional.ofNullable(this.entries.get(uniqueId));
  }

  @Override
  public boolean hasPlayer(final @NotNull UUID uniqueId) {
    return this.entries.containsKey(uniqueId);
  }

  @Override
  public @NotNull Iterator<TablistEntry> iterator() {
    return this.entries.values().stream().map(TablistEntry.class::cast).iterator();
  }

  @Internal
  public void initPlayer(final @NotNull Player player) {
    final PlayerConnection connection = player.connection();

    connection.sendPacket(new ClientboundTablistLayoutPacket(this.header, this.footer));
    connection.sendPacket(new ClientboundPlayerInfoPacket(Action.ADD_PLAYER, this.entries.values().toArray(TablistEntry[]::new)));
  }

  /**
   * Used, if we do not want to spread packets to everyone.
   * Currently, every online player is affected.
   */
  private void spreadPacket(final @NotNull Supplier<Packet> packetSupplier) {
    this.manager.spreadPacket(packetSupplier);
  }

  private static class GlobalTablistEntry implements TablistEntry {

    private final GameProfile profile;

    private @NotNull GameMode gamemode;
    private @Nullable Component customName;
    private int ping = 0;

    public GlobalTablistEntry(final @NotNull Player player) {
      this.profile = player.profile();
      this.gamemode = player.gamemode();
      this.customName = player.metadata().get(EntityDataAccessor.CUSTOM_NAME).orElse(Optional.empty()).orElse(null);
    }

    public GlobalTablistEntry(final @NotNull GameProfile profile, final @NotNull GameMode gamemode) {
      this.profile = profile;
      this.gamemode = gamemode;
    }

    @Override
    public @NotNull GameProfile profile() {
      return this.profile;
    }

    @Override
    public @NotNull Optional<Component> customName() {
      return Optional.ofNullable(this.customName);
    }

    @Override
    public @NotNull GameMode gamemode() {
      return this.gamemode;
    }

    @Override
    public int ping() {
      return this.ping;
    }

    private void customName(final @Nullable Component component) {
      this.customName = component;
    }

    private void gamemode(final @NotNull GameMode mode) {
      this.gamemode = mode;
    }

    private void ping(final @Range(from = 0, to = Integer.MAX_VALUE) int ping) {
      this.ping = ping;
    }

  }

}
