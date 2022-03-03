package dev.neodym.limbo.network;

import dev.neodym.limbo.auth.GameProfile;
import dev.neodym.limbo.entity.Player;
import dev.neodym.limbo.exception.PacketFloodException;
import dev.neodym.limbo.network.protocol.ConnectionState;
import dev.neodym.limbo.network.protocol.PacketContainer;
import dev.neodym.limbo.network.protocol.listener.HandshakeListener;
import dev.neodym.limbo.network.protocol.listener.LoginListener;
import dev.neodym.limbo.network.protocol.listener.PacketListener;
import dev.neodym.limbo.network.protocol.listener.PlayGameListener;
import dev.neodym.limbo.network.protocol.listener.StatusListener;
import dev.neodym.limbo.network.protocol.packet.Packet;
import dev.neodym.limbo.network.protocol.packet.login.clientbound.ClientboundLoginDisconnectPacket;
import dev.neodym.limbo.network.protocol.packet.login.clientbound.ClientboundLoginSuccessPacket;
import dev.neodym.limbo.network.protocol.packet.play.KeepAlivePacket;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundDeclareCommandsPacket;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundDisconnectPacket;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundJoinGamePacket;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundPlayerAbilitiesPacket;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundPlayerInfoPacket;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundPlayerInfoPacket.Action;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundPlayerInfoPacket.PlayerData;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundPlayerPositionPacket;
import dev.neodym.limbo.server.LimboServer;
import dev.neodym.limbo.world.dimension.DimensionType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@RequiredArgsConstructor
public class PlayerConnection extends ChannelInboundHandlerAdapter {

  private static final long PACKETS_PER_SECOND = 300;
  private static final long TIMEOUT_TIME = 30L; // in sec

  private static final LimboServer SERVER = LimboServer.get();
  private static final Logger LOGGER = SERVER.logger();

  private final @NotNull Channel channel;
  private final @NotNull PacketTracker tracker = this.new PacketTracker();

  private @NotNull ConnectionState state = ConnectionState.HANDSHAKE;
  private @NotNull Locale locale = Locale.US;
  private Player player;

  private int protocolVersion;

  private String usedHost;
  private SocketAddress address;

  private long lastReceivedKeepAlive = System.currentTimeMillis();
  private @NotNull PacketListener currentListener = this.state.createPacketListener(this);

  // <editor-folder desc="ChannelInboundHandler implementation" defaultstate="collapsed">

  @Override
  public void channelInactive(final @NotNull ChannelHandlerContext ctx) throws Exception {
    if (this.state == ConnectionState.PLAY) {
      SERVER.networkManager().removePlayer(this);

      SERVER.networkManager().players().forEach(player -> {
        player.remove();
        // remove entity
        // remove tablist
      });
    }

    super.channelInactive(ctx);
  }

  @Override
  public void channelRead(final @NotNull ChannelHandlerContext ctx, final @NotNull Object msg) {
    if (!(msg instanceof Packet packet)) throw new IllegalStateException("Unknown object received: %s".formatted(msg.getClass()));
    if (!this.tracker.track()) throw new PacketFloodException("Packet flood detected: received more then %d pps.".formatted(PACKETS_PER_SECOND));

    this.handlePacket(packet);
  }

  @Override
  public void exceptionCaught(final @NotNull ChannelHandlerContext ctx, final @NotNull Throwable cause) {
    LOGGER.error("Unhandled exception: ", cause);
  }

  // </editor-folder>

  // <editor-folder desc="Getter/Setter" defaultstate="collapsed">

  private void state(final @NotNull ConnectionState state) {
    if (!this.currentListener.isCompleted()) {
      LOGGER.warn("Listener is still not completed! Break it up.");
      return;
    }

    this.state = state;
    this.currentListener = state.createPacketListener(this);
  }

  public @NotNull ConnectionState state() {
    return this.state;
  }

  public @NotNull SocketAddress address() {
    return Objects.requireNonNullElse(this.address, this.channel.remoteAddress());
  }

  public @NotNull Player player() {
    return this.player;
  }

  public @NotNull Optional<String> usedHost() {
    return Optional.ofNullable(this.usedHost);
  }

  public @NotNull Locale locale() {
    return this.locale;
  }

  public void locale(final @NotNull Locale locale) {
    this.locale = locale;
  }

  public int protocolVersion() {
    return this.protocolVersion;
  }

  public void disconnect(final @NotNull Component reason) {
    this.sendPacketAndClose(
        switch (this.state) {
          case LOGIN -> new ClientboundLoginDisconnectPacket(reason);
          case PLAY -> new ClientboundDisconnectPacket(reason);
          default -> throw new UnsupportedOperationException("Cannot disconnect player being not connected!");
        }
    );
  }

  // </editor-folder>

  // <editor-folder desc="sending packets" defaultstate="collapsed">

  public void sendPacket(final @NotNull Packet packet) {
    if (!this.isConnected()) return;

    this.writePacket(packet);
    this.flushPackets();
  }

  public void sendPacket(final @NotNull Packet packet, final @NotNull GenericFutureListener<? extends Future<? super Void>> listener) {
    if (!this.isConnected()) return;

    if (packet instanceof PacketContainer) throw new IllegalArgumentException("PacketContainers cannot be send with a listener yet.");

    this.ensureInEventLoopGroup(() -> this.channel.writeAndFlush(packet).addListener(listener));
  }

  private void writePacket0(final @NotNull Packet packet) {
    if (packet instanceof PacketContainer container) {
      Arrays.stream(container.packets()).forEach(this::writePacket0);
      return;
    }

    this.channel.write(packet);
  }

  public void sendPacketAndClose(final @NotNull Packet packet) {
    this.sendPacket(packet, ChannelFutureListener.CLOSE);
  }

  public void writePacket(final @NotNull Packet packet) {
    this.ensureInEventLoopGroup(() -> this.writePacket0(packet));
  }

  public void flushPackets() {
    this.channel.flush();
  }

  // </editor-folder>

  public boolean isConnected() {
    return this.channel != null && this.channel.isActive() && this.channel.isOpen();
  }

  private synchronized void handlePacket(final @NotNull Packet packet) {
    packet.handle(this.currentListener);

    if (!this.currentListener.isCompleted() || !this.isConnected()) return;

    if (this.currentListener instanceof HandshakeListener handshake) {
      this.usedHost = handshake.usedHost();
      this.protocolVersion = handshake.protocolVersion();
      this.state(handshake.nextState());
      return;
    }

    if (this.currentListener instanceof StatusListener) {
      // Status listener cannot be completed
      return;
    }

    if (this.currentListener instanceof LoginListener login) {
      this.address = login.address();

      this.fireLoginSuccess(login.profile());
      return;
    }

    if (this.currentListener instanceof PlayGameListener) {
      // Play state cannot be completed.
      // Completion comes over disconnect.
      return;
    }
  }

  private void fireLoginSuccess(final @NotNull GameProfile profile) {
    assert this.player == null;

    this.player = new Player(this, profile);

    this.writePacket(new ClientboundLoginSuccessPacket(this.player.uniqueId(), this.player.profile().name()));
    this.state(ConnectionState.PLAY);
    SERVER.networkManager().addPlayer(this);

    this.writePacket(
        PacketContainer.of(
            new ClientboundJoinGamePacket(this.player.entityId(), this.player.gamemode(), DimensionType.DEFAULT, List.of(DimensionType.DEFAULT.key()), DimensionType.DEFAULT.key(), 0L),
            new ClientboundPlayerAbilitiesPacket(false, true, true, false),
            new ClientboundPlayerPositionPacket(this.player.position(), (byte) 0x00, false),
            new ClientboundDeclareCommandsPacket()
        )
    );

    final PlayerData[] data = SERVER.networkManager().players().stream().map(PlayerData::new).toArray(PlayerData[]::new);
    this.sendPacket(new ClientboundPlayerInfoPacket(Action.ADD_PLAYER, data));

    SERVER.networkManager().spreadPacket(new ClientboundPlayerInfoPacket(
        Action.ADD_PLAYER,
        new PlayerData(this.player)
    ));

    SERVER.world().initPlayer(this.player);
    this.sendKeepAlive();
  }

  private void ensureInEventLoopGroup(final @NotNull Runnable r) {
    final EventLoop loop = this.channel.eventLoop();
    if (loop.inEventLoop()) {
      r.run();
      return;
    }

    loop.execute(r);
  }

  // <editor-folder desc="KeepAlive" defaultstate="collapsed">

  public void notifyKeepAlive(final long id) {
    this.lastReceivedKeepAlive = System.currentTimeMillis();
  }

  public void checkKeepAlive() {
    if (this.lastReceivedKeepAlive + TimeUnit.SECONDS.toMillis(TIMEOUT_TIME) > System.currentTimeMillis()) return;

    this.disconnect(Component.text("No KeepAlivePacket received for more then %d seconds.".formatted(TIMEOUT_TIME)));
  }

  public void sendKeepAlive() {
    if (this.state != ConnectionState.PLAY) return;

    final long current = System.currentTimeMillis();
    this.sendPacket(new KeepAlivePacket(current));
  }

  // </editor-folder>

  private class PacketTracker {

    private long packets = 0L;
    private long lastUpdated = System.currentTimeMillis();

    private boolean track() {
      final long time = System.currentTimeMillis();

      if (time - this.lastUpdated > 1_000) { // 1 sec
        this.lastUpdated = time;
        this.packets = 0;
      }

      this.packets++;

      if (this.packets <= PACKETS_PER_SECOND) return true;

      PlayerConnection.this.disconnect(Component.text("Too many packets"));
      return false;
    }

  }

}
