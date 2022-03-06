package dev.neodym.limbo.entity;

import dev.neodym.limbo.auth.GameProfile;
import dev.neodym.limbo.network.PlayerConnection;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundChunkDataAndLightPacket;
import dev.neodym.limbo.server.LimboServer;
import dev.neodym.limbo.util.GameMode;
import dev.neodym.limbo.world.block.Block;
import dev.neodym.limbo.world.chunk.Chunk.ChunkCoordinates;
import dev.neodym.limbo.world.chunk.ChunkMap;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.pointer.Pointers;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.TitlePart;
import org.jetbrains.annotations.NotNull;

public class Player extends LivingEntity implements Audience, BlockBreaker {

  private final ChunkMap chunkMap = new ChunkMap(this);

  private final @NotNull PlayerConnection connection;
  private final @NotNull GameProfile profile;

  private @NotNull GameMode gamemode = GameMode.ADVENTURE;
  private int viewDistance = 2;

  public Player(final @NotNull PlayerConnection connection, final GameProfile profile) {
    super(profile.uniqueId(), 20);
    this.profile = profile;
    this.connection = connection;
  }

  public @NotNull GameProfile profile() {
    return this.profile;
  }

  public @NotNull PlayerConnection connection() {
    return this.connection;
  }

  public int viewDistance() {
    return this.viewDistance;
  }

  public void viewDistance(final int viewDistance) {
    if (this.viewDistance == viewDistance) return;

    this.viewDistance = viewDistance;
    this.updateChunks();
  }

  public int effectiveViewDistance() {
    return this.chunkMap.effectiveViewDistance();
  }

  public boolean isChunkLoaded(final @NotNull ChunkCoordinates coordinates) {
    return this.chunkMap.isLoaded(coordinates);
  }

  public void reloadChunks() {
    this.chunkMap.resend();
  }

  @Override
  public void breakBlock(final @NotNull Block block) {
    // TODO
  }

  @Override
  public void position(final double x, final double y, final double z, final float yaw, final float pitch) {
    final int chunkX = this.chunkX();
    final int chunkZ = this.chunkZ();

    super.position(x, y, z, yaw, pitch);

    final int newChunkX = super.chunkX();
    final int newChunkZ = super.chunkZ();

    if (chunkX == newChunkX && chunkZ == newChunkZ) return;
    this.updateChunks();
  }

  private void updateChunks() {
    this.chunkMap.moveTo(super.chunkX(), super.chunkZ());
  }

  @Override
  public void sendMessage(final @NotNull Identity source, final @NotNull Component message, final @NotNull MessageType type) {
    Audience.super.sendMessage(source, message, type);
  }

  @Override
  public void sendActionBar(final @NotNull Component message) {
    Audience.super.sendActionBar(message);
  }

  @Override
  public void sendPlayerListHeaderAndFooter(final @NotNull Component header, final @NotNull Component footer) {
    Audience.super.sendPlayerListHeaderAndFooter(header, footer);
  }

  @Override
  public <T> void sendTitlePart(final @NotNull TitlePart<T> part, final @NotNull T value) {
    Audience.super.sendTitlePart(part, value);
  }

  @Override
  public void clearTitle() {
    Audience.super.clearTitle();
  }

  @Override
  public void resetTitle() {
    Audience.super.resetTitle();
  }

  @Override
  public void showBossBar(final @NotNull BossBar bar) {
    Audience.super.showBossBar(bar);
  }

  @Override
  public void hideBossBar(final @NotNull BossBar bar) {
    Audience.super.hideBossBar(bar);
  }

  @Override
  public void playSound(final @NotNull Sound sound) {
    Audience.super.playSound(sound);
  }

  @Override
  public void playSound(final @NotNull Sound sound, final double x, final double y, final double z) {
    Audience.super.playSound(sound, x, y, z);
  }

  @Override
  public void playSound(final @NotNull Sound sound, final @NotNull Sound.Emitter emitter) {
    Audience.super.playSound(sound, emitter);
  }

  @Override
  public void stopSound(final @NotNull SoundStop stop) {
    Audience.super.stopSound(stop);
  }

  @Override
  public void openBook(final @NotNull Book book) {
    Audience.super.openBook(book);
  }

  @Override
  public @NotNull Pointers pointers() {
    throw new UnsupportedOperationException("Pointers are not supported on limbo!");
  }

  public void gamemode(final @NotNull GameMode gamemode) {
    if (gamemode == this.gamemode) return;

    LimboServer.get().globalTablist().updateGameMode(super.uniqueId(), gamemode);
    // TODO gamestate change packet
    this.gamemode = gamemode;
  }

  public @NotNull GameMode gamemode() {
    return this.gamemode;
  }

}
