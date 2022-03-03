package dev.neodym.limbo.network.protocol.packet.play.clientbound;

import dev.neodym.limbo.network.protocol.packet.Packet;

public record ClientboundPlayerAbilitiesPacket(
    boolean invulnerable,
    boolean flying,
    boolean canFly,
    boolean instantBuild,
    float flySpeed,
    float walkingSpeed
) implements Packet {

  private static final float DEFAULT_FLY_SPEED = 0.05F;
  private static final float DEFAULT_WALK_SPEED = 0.1F;

  public ClientboundPlayerAbilitiesPacket(final boolean invulnerable, final boolean flying, final boolean canFly, final boolean instantBuild) {
    this(invulnerable, flying, canFly, instantBuild, DEFAULT_FLY_SPEED, DEFAULT_WALK_SPEED);
  }

}
