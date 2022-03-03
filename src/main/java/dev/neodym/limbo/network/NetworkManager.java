package dev.neodym.limbo.network;

import com.google.common.collect.Maps;
import dev.neodym.limbo.entity.Player;
import dev.neodym.limbo.network.protocol.packet.Packet;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public class NetworkManager {

  private final Map<UUID, PlayerConnection> connectedPlayers = Maps.newConcurrentMap();

  void addPlayer(final @NotNull PlayerConnection connection) {
    this.connectedPlayers.put(connection.player().uniqueId(), connection);
  }

  void removePlayer(final @NotNull PlayerConnection connection) {
    this.connectedPlayers.remove(connection.player().uniqueId());
  }

  public @Range(from = 0, to = Integer.MAX_VALUE) int count() {
    return this.connectedPlayers.size();
  }

  public Collection<PlayerConnection> connections() {
    return this.connectedPlayers.values();
  }

  public Collection<Player> players() {
    return this.connectedPlayers.values().stream().map(PlayerConnection::player).toList();
  }

  public void spreadPacket(final @NotNull Packet packet) {
    this.connections().forEach(connection -> connection.sendPacket(packet));
  }

  public void spreadPacket(final @NotNull Supplier<Packet> packet) {
    if (this.connectedPlayers.isEmpty()) return;
    this.spreadPacket(packet.get());
  }

}
