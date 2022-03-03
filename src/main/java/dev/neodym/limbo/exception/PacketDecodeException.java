package dev.neodym.limbo.exception;

import dev.neodym.limbo.network.protocol.packet.Packet;
import org.jetbrains.annotations.NotNull;

public class PacketDecodeException extends RuntimeException {

  public PacketDecodeException(final @NotNull Packet packet, final @NotNull Throwable cause) {
    super("Error during decoding of packet %s: ".formatted(packet.getClass().getSimpleName()), cause);
  }

  public PacketDecodeException(final @NotNull String message) {
    super(message);
  }

  public PacketDecodeException(final @NotNull String message, final @NotNull Throwable cause) {
    super(message, cause);
  }

  public PacketDecodeException(final @NotNull Throwable cause) {
    super("Error during packet decoding: ", cause);
  }
}
