package dev.neodym.limbo.network.protocol.packet;

import dev.neodym.limbo.network.protocol.listener.PacketListener;
import org.jetbrains.annotations.NotNull;

public interface Packet {

  default void handle(final @NotNull PacketListener listener) {
    listener.handleDefault(this);
  }

  enum Flow {

    CLIENTBOUND,
    SERVERBOUND

  }

}
