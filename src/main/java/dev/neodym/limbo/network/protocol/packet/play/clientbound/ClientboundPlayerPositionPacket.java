package dev.neodym.limbo.network.protocol.packet.play.clientbound;

import dev.neodym.limbo.network.protocol.packet.Packet;
import dev.neodym.limbo.util.Position;
import org.jetbrains.annotations.NotNull;

public record ClientboundPlayerPositionPacket(
    @NotNull Position position,
    byte flags,
    int teleportId,
    boolean dismountVehicle
) implements Packet {

  private static int counter = 0;

  public ClientboundPlayerPositionPacket(final @NotNull Position position, final byte flags, final boolean dismountVehicle) {
    this(position, flags, counter++, dismountVehicle);
  }

}
