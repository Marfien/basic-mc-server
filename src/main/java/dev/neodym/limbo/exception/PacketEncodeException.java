package dev.neodym.limbo.exception;

import dev.neodym.limbo.network.protocol.packet.Packet;
import org.jetbrains.annotations.NotNull;

public class PacketEncodeException extends RuntimeException {

  public PacketEncodeException(final @NotNull Packet packet, final @NotNull Throwable cause) {
    super("Error during encoding of packet %s: ".formatted(packet.getClass().getSimpleName()), cause);
  }
}
