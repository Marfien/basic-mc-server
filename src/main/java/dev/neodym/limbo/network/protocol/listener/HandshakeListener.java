package dev.neodym.limbo.network.protocol.listener;

import dev.neodym.limbo.network.PlayerConnection;
import dev.neodym.limbo.network.protocol.ConnectionState;
import dev.neodym.limbo.network.protocol.packet.handshake.ServerboundHandshakePacket;
import dev.neodym.limbo.server.LimboServer;
import org.jetbrains.annotations.NotNull;

public class HandshakeListener extends BasicPacketListener {

  private int protocolVersion = -1;
  private String usedHost;
  private ConnectionState nextState;

  public HandshakeListener(final @NotNull PlayerConnection connection) {
    super(connection);
  }

  public int protocolVersion() {
    return this.protocolVersion;
  }

  public @NotNull String usedHost() {
    return this.usedHost;
  }

  public @NotNull ConnectionState nextState() {
    return this.nextState;
  }

  @Override
  public boolean isCompleted() {
    return this.protocolVersion != -1
        && this.usedHost != null
        && this.nextState != null;
  }

  @Override
  public void handleHandshake(final @NotNull ServerboundHandshakePacket packet) {
    this.nextState = packet.nextState();
    this.protocolVersion = packet.protocolVersion();
    this.usedHost = packet.host();

    LimboServer.get().logger().debug("Handshake from {}.", super.connection.address());
  }
}
