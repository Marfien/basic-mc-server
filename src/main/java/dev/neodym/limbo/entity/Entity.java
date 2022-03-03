package dev.neodym.limbo.entity;

import com.google.common.collect.Lists;
import dev.neodym.limbo.entity.data.EntityData;
import dev.neodym.limbo.util.Position;
import dev.neodym.limbo.util.Vec3D;
import dev.neodym.limbo.world.chunk.Chunk;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Entity {

  protected static final Logger LOGGER = LoggerFactory.getLogger(Entity.class.getSimpleName());

  private static final AtomicInteger ENTITY_COUNTER = new AtomicInteger();

  private final int entityId = ENTITY_COUNTER.getAndIncrement();
  private final UUID uniqueId;
  private final EntityData metadata = new EntityData(this);
  private final List<Entity> passengers = Lists.newArrayList();

  private @Nullable Entity vihicle;
  private final @NotNull Position position = new Position(0.0D, 64.0D, 0.0D);

  private boolean valid = true;
  private Vec3D velocity = new Vec3D();
  private float fallDistance;
  private long ticksLived;
  private boolean onGround = false;

  protected Entity(final @NotNull UUID uniqueId) {
    this.uniqueId = uniqueId;
  }

  protected Entity() {
    this.uniqueId = new UUID(0L, this.entityId);
  }

  public @NotNull UUID uniqueId() {
    return this.uniqueId;
  }

  public int entityId() {
    return this.entityId;
  }

  public void remove() {
    this.valid = false;
  }

  public @NotNull EntityData metadata() {
    return this.metadata;
  }

  public @NotNull Position position() {
    return this.position;
  }

  public void onGround(final boolean onGround) {
    this.onGround = onGround;
  }

  public boolean onGround() {
    return this.onGround;
  }

  @Internal
  public void position(final @NotNull Position position) {
    this.position(position.x(), position().y(), position().z(), position.yaw(), position.pitch());
  }

  @Internal
  public void position(final @NotNull Vec3D position) {
    this.position(position.x(), position().y(), position().z());
  }

  @Internal
  public void position(final double x, final double y, final double z) {
    this.position(x, y, z, this.position.yaw(), this.position.pitch());
  }

  @Internal
  public void position(final double x, final double y, final double z, final float yaw, final float pitch) {
    this.position.x(x);
    this.position.y(y);
    this.position.z(z);
    this.position.yaw(yaw);
    this.position.pitch(pitch);
  }

  @Internal
  public void rotation(final float yaw, final float pitch) {
    this.position.yaw(yaw);
    this.position.pitch(pitch);
  }

  public int chunkX() {
    return this.sectionX();
  }

  public int chunkZ() {
    return this.sectionZ();
  }

  public int sectionX() {
    return Chunk.forChunk(this.position.x());
  }

  public int sectionY() {
    return Chunk.forChunk(this.position.y());
  }

  public int sectionZ() {
    return Chunk.forChunk(this.position.z());
  }

  public float fallDistance() {
    return this.fallDistance;
  }
}
