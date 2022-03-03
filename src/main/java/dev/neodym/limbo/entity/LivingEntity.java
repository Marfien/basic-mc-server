package dev.neodym.limbo.entity;

import com.google.common.util.concurrent.AtomicDouble;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.jetbrains.annotations.NotNull;

public abstract class LivingEntity extends Entity {

  private final int maxHealth;
  private final AtomicDouble health;
  private final AtomicInteger reamingGodModeTicks = new AtomicInteger();
  private final EntityEffects effects = new EntityEffects();

  protected LivingEntity(final @NotNull UUID uniqueId, final int maxHealth) {
    super(uniqueId);
    this.maxHealth = maxHealth;
    this.health = new AtomicDouble(maxHealth);
  }

}
