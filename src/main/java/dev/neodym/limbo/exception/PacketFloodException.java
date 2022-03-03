package dev.neodym.limbo.exception;

import org.jetbrains.annotations.NotNull;

public class PacketFloodException extends RuntimeException {

  public PacketFloodException(final @NotNull String message) {
    super(message);
  }
}
