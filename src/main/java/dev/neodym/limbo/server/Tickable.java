package dev.neodym.limbo.server;

public interface Tickable {

  default void tick(final long currentTick) {
    this.tick();
  }

  default void tick() {
    throw new UnsupportedOperationException("Neither Tickable#tick(long) nor Tickable#tick() has been implemented.");
  }

}
