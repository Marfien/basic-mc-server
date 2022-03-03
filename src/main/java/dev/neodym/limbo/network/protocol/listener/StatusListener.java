package dev.neodym.limbo.network.protocol.listener;

import dev.neodym.limbo.network.PlayerConnection;
import dev.neodym.limbo.network.protocol.packet.status.StatusPingPacket;
import dev.neodym.limbo.network.protocol.packet.status.ServerboundStatusRequestPacket;
import dev.neodym.limbo.network.protocol.packet.status.ClientboundStatusResponsePacket;
import java.util.Collections;
import org.jetbrains.annotations.NotNull;

public class StatusListener extends BasicPacketListener {

  public StatusListener(final @NotNull PlayerConnection connection) {
    super(connection);
  }

  @Override
  public void handleStatusPing(final @NotNull StatusPingPacket packet) {
    this.connection.sendPacketAndClose(packet);
  }

  @Override
  public void handleStatusRequest(final @NotNull ServerboundStatusRequestPacket packet) {
    super.connection.sendPacket(new ClientboundStatusResponsePacket(
        super.server.config().name(),
        super.server.protocolVersion(),
        super.server.slots(),
        super.server.networkManager().count(),
        Collections.emptyList(),
        super.server.config().description()
    ));
  }

  @Override
  public boolean isCompleted() {
    return false;
  }
}
