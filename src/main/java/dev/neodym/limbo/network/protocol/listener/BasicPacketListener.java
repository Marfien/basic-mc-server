package dev.neodym.limbo.network.protocol.listener;

import dev.neodym.limbo.network.PlayerConnection;
import dev.neodym.limbo.server.LimboServer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BasicPacketListener implements PacketListener {

  protected final @NotNull LimboServer server = LimboServer.get();
  protected final @NotNull PlayerConnection connection;

}
