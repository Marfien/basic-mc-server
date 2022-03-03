package dev.neodym.limbo;

import dev.neodym.limbo.server.LimboServer;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public class LimboBootstrap {

  public static void main(final @NotNull String[] args) throws IOException {
    new LimboServer().start();
  }

}
