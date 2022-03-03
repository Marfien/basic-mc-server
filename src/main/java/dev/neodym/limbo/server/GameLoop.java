package dev.neodym.limbo.server;

import dev.neodym.limbo.network.PlayerConnection;
import dev.neodym.limbo.world.World;
import java.util.TimerTask;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

public class GameLoop extends TimerTask {

  private final @NotNull LimboServer server;
  private final @NotNull World world;

  public GameLoop(final @NotNull LimboServer server) {
    this.server = server;
    this.world = server.world();
  }

  @Override
  public void run() {
    this.checkLastKeepAlive();

    // this.world.tickBlocks(); TODO
    // this.world.tickEntities(); TODO
  }

  private void checkLastKeepAlive() {
    this.server.networkManager().connections().forEach(PlayerConnection::checkKeepAlive);
  }
}
