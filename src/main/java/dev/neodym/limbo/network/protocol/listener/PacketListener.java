package dev.neodym.limbo.network.protocol.listener;

import dev.neodym.limbo.network.protocol.packet.Packet;
import dev.neodym.limbo.network.protocol.packet.handshake.ServerboundHandshakePacket;
import dev.neodym.limbo.network.protocol.packet.login.serverbound.ServerboundLoginPluginResponsePacket;
import dev.neodym.limbo.network.protocol.packet.login.serverbound.ServerboundLoginStartPacket;
import dev.neodym.limbo.network.protocol.packet.play.KeepAlivePacket;
import dev.neodym.limbo.network.protocol.packet.play.serverbound.ServerboundClientSettingsPacket;
import dev.neodym.limbo.network.protocol.packet.play.serverbound.ServerboundPlayerLookPacket;
import dev.neodym.limbo.network.protocol.packet.play.serverbound.ServerboundPlayerPositionAndLook;
import dev.neodym.limbo.network.protocol.packet.play.serverbound.ServerboundPlayerPositionPacket;
import dev.neodym.limbo.network.protocol.packet.status.ServerboundStatusRequestPacket;
import dev.neodym.limbo.network.protocol.packet.status.StatusPingPacket;
import org.jetbrains.annotations.NotNull;

public interface PacketListener {

  // <editor-fold defaultstate="collapsed" desc="Handshake">

  default void handleHandshake(final @NotNull ServerboundHandshakePacket packet) {
    this.handleDefault(packet);
  }

  // </editor-folder>

  // <editor-folder defaultstate="collapsed" desc="Login">

  default void handleLoginPluginResponse(final @NotNull ServerboundLoginPluginResponsePacket packet) {
    this.handleDefault(packet);
  }

  default void handleLoginStart(final @NotNull ServerboundLoginStartPacket packet) {
    this.handleDefault(packet);
  }

  // </editor-folder>

  // <editor-folder defaultstate="collapsed" desc="Status">

  default void handleStatusPing(final @NotNull StatusPingPacket packet) {
    this.handleDefault(packet);
  }

  default void handleStatusRequest(final @NotNull ServerboundStatusRequestPacket packet) {
    this.handleDefault(packet);
  }

  // </editor-folder>

  // <editor-folder defaultstate="collapsed" desc="Play">

  default void handleKeepAlive(final @NotNull KeepAlivePacket packet) {
    this.handleDefault(packet);
  }

  default void handleClientSettings(final @NotNull ServerboundClientSettingsPacket packet) {
    this.handleDefault(packet);
  }

  default void handlePlayerLook(final @NotNull ServerboundPlayerLookPacket packet) {
    this.handleDefault(packet);
  }

  default void handlePlayerPositionAndLookPacket(final @NotNull ServerboundPlayerPositionAndLook packet) {
    this.handleDefault(packet);
  }

  default void handlePlayerPositionPacket(final @NotNull ServerboundPlayerPositionPacket packet) {
    this.handleDefault(packet);
  }

  // </editor-folder>

  boolean isCompleted();

  default void handleDefault(final @NotNull Packet packet) {
    throw new UnsupportedOperationException("Unhandled packet: %s".formatted(packet.getClass()));
  }

}
