package dev.neodym.limbo.world;

import com.google.common.collect.Maps;
import dev.neodym.limbo.entity.Entity;
import dev.neodym.limbo.entity.Player;
import dev.neodym.limbo.network.protocol.packet.Packet;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundUpdateTimePacket;
import dev.neodym.limbo.server.LimboServer;
import dev.neodym.limbo.util.SoundEvent;
import dev.neodym.limbo.util.Vec3D;
import dev.neodym.limbo.util.Vec3I;
import dev.neodym.limbo.world.block.Block;
import dev.neodym.limbo.world.chunk.Chunk;
import dev.neodym.limbo.world.chunk.Chunk.ChunkCoordinates;
import dev.neodym.limbo.world.chunk.ChunkSection;
import dev.neodym.limbo.world.dimension.DimensionType;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public class World {

  private final LimboServer server = LimboServer.get();
  private final @NotNull Path path;

  private final Map<Integer, Entity> entities = Maps.newConcurrentMap();
  private final Map<Integer, Chunk> chunks = Maps.newConcurrentMap();

  private final DimensionType dimension;

  private boolean enableDaylightCycle = false;
  private long dayTime = 0L;

  public World(final @NotNull Path path) {
    this(path, DimensionType.DEFAULT);
  }

  public World(final @NotNull Path path, final @NotNull DimensionType dimension) {
    this.path = path;
    this.dimension = dimension;
  }

  public void load() {

  }

  public void initPlayer(final @NotNull Player player) {
    player.connection().sendPacket(this.getTimePacket());
    player.reloadChunks();
  }

  public @NotNull Collection<Chunk> chunks() {
    return this.chunks.values();
  }

  public long dayTime() {
    return this.dayTime;
  }

  public void dayTime(final @Range(from = 0, to = Long.MAX_VALUE) long dayTime) {
    this.dayTime = dayTime;

    this.server.networkManager().spreadPacket(this::getTimePacket);
  }

  public @NotNull DimensionType dimension() {
    return this.dimension;
  }
  
  public int minBuildHeight() {
    return this.dimension.minY();
  }

  public int maxBuildHeight() {
    return this.absoluteHeight() - Math.abs(this.minBuildHeight());
  }

  public int absoluteHeight() {
    return this.dimension.height();
  }

  public @NotNull Packet getTimePacket() {
    final long timeToSend = this.enableDaylightCycle ? dayTime : -dayTime;
    return new ClientboundUpdateTimePacket(
        System.currentTimeMillis(),
        !this.enableDaylightCycle && timeToSend == 0
            ? -1
            : timeToSend
    );
  }

  public boolean daylightCycle() {
    return this.enableDaylightCycle;
  }

  public void daylightCycle(final boolean daylightCycle) {
    this.enableDaylightCycle = daylightCycle;
    this.dayTime(this.dayTime);
  }

  public @NotNull Block blockAt(final @NotNull Vec3I position) {
    return this.blockAt(position.x(), position.y(), position.z());
  }

  public @NotNull Block blockAt(final int x, final int y, final int z) {
    final int chunkX = Chunk.forChunk(x);
    final int chunkZ = Chunk.forChunk(z);
    final int section = Chunk.forChunk(y);

    return this.sectionAt(chunkX, section, chunkZ).blockAt(x - (chunkX * 16), y - (section * 16), z - (chunkZ * 16));
  }

  public @NotNull Chunk chunkAt(final int chunkX, final int chunkZ) {
    return this.chunkAt(new ChunkCoordinates(chunkX, chunkZ));
  }

  public @NotNull Chunk chunkAt(final double x, final double z) {
    return this.chunkAt(Chunk.forChunk(x), Chunk.forChunk(z));
  }

  public @NotNull ChunkSection sectionAt(final int chunkX, final int chunkY, final int chunkZ) {
    return this.chunkAt(chunkX, chunkZ).section(chunkY);
  }

  public @NotNull ChunkSection sectionAt(final double x, final double y, final double z) {
    return this.chunkAt(x, z).section(Chunk.forChunk(y));
  }

  public @NotNull ChunkSection sectionAt(final Vec3D position) {
    return this.sectionAt(position.x(), position.y(), position.z());
  }

  public @NotNull ChunkSection sectionAt(final Vec3I position) {
    return this.sectionAt(position.x(), position.y(), position.z());
  }

  public @NotNull Chunk chunkAt(final @NotNull Chunk.ChunkCoordinates coordinate) {
    return this.chunks.computeIfAbsent(coordinate.packed(), ignored -> Chunk.empty(coordinate));
  }

  public void playSound(final @NotNull Sound sound, final @NotNull Vec3D position) {
    LimboServer.get().networkManager().spreadPacket(() -> SoundEvent.packet(sound, position));
  }

  public void playSound(final @NotNull Sound sound, final double x, final double y, final double z) {
    this.playSound(sound, new Vec3D(x, y, z));
  }
}
