package dev.neodym.limbo.network.protocol.listener;

import dev.neodym.limbo.entity.Player;
import dev.neodym.limbo.entity.data.EntityDataAccessor;
import dev.neodym.limbo.network.PlayerConnection;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundEntityDataPacket;
import dev.neodym.limbo.network.protocol.packet.play.KeepAlivePacket;
import dev.neodym.limbo.network.protocol.packet.play.serverbound.ServerboundClientSettingsPacket;
import dev.neodym.limbo.network.protocol.packet.play.serverbound.ServerboundPlayerLookPacket;
import dev.neodym.limbo.network.protocol.packet.play.serverbound.ServerboundPlayerPositionAndLook;
import dev.neodym.limbo.network.protocol.packet.play.serverbound.ServerboundPlayerPositionPacket;
import dev.neodym.limbo.server.LimboServer;
import dev.neodym.limbo.util.Position;
import org.jetbrains.annotations.NotNull;

public class PlayGameListener extends BasicPacketListener {

  private static final LimboServer SERVER = LimboServer.get();

  private final @NotNull Player player;

  public PlayGameListener(final @NotNull PlayerConnection connection) {
    super(connection);
    this.player = connection.player();
  }

  @Override
  public void handleKeepAlive(final @NotNull KeepAlivePacket packet) {
    super.connection.notifyKeepAlive(packet.id());
  }

  @Override
  public void handleClientSettings(final @NotNull ServerboundClientSettingsPacket packet) {
    this.player.metadata().set(EntityDataAccessor.PLAYER_SKIN_PARTS, (byte) packet.skinParts());
    SERVER.networkManager().spreadPacket(new ClientboundEntityDataPacket(this.player));

    this.player.viewDistance(packet.viewDistance());
    super.connection.locale(packet.locale());
  }

  @Override
  public void handlePlayerLook(final @NotNull ServerboundPlayerLookPacket packet) {
    this.player.rotation(packet.yaw(), packet.pitch());
    this.player.onGround(packet.onGround());
  }

  @Override
  public void handlePlayerPositionAndLookPacket(final @NotNull ServerboundPlayerPositionAndLook packet) {
    this.player.position(packet.position());
    this.player.onGround(packet.onGround());
  }

  @Override
  public void handlePlayerPositionPacket(final @NotNull ServerboundPlayerPositionPacket packet) {
    this.player.position(packet.x(), packet.y(), packet.z());
    this.player.onGround(packet.onGround());
  }

  @Override
  public boolean isCompleted() {
    return false;
  }
}
