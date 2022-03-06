package dev.neodym.limbo.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.neodym.limbo.auth.GameProfile;
import dev.neodym.limbo.auth.GameProfileResolver;
import dev.neodym.limbo.config.LimboConfig;
import dev.neodym.limbo.config.SocketAddressDeserializer;
import dev.neodym.limbo.network.ClientChannelInitializer;
import dev.neodym.limbo.network.NetworkManager;
import dev.neodym.limbo.network.PlayerConnection;
import dev.neodym.limbo.util.NettyUtil;
import dev.neodym.limbo.util.Position;
import dev.neodym.limbo.util.Position.PositionTypeAdapter;
import dev.neodym.limbo.util.tablist.GlobalTablist;
import dev.neodym.limbo.util.tablist.Tablist;
import dev.neodym.limbo.world.World;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.util.ResourceLeakDetector;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LimboServer {

  private static final Path CONFIG_FILE = Path.of(".", "config.json");

  private static LimboServer instance;

  public static @NotNull LimboServer get() {
    if (instance == null) throw new AssertionError("LimboServer has not been initialized yet.");

    return instance;
  }

  private final @NotNull Gson gson = GsonComponentSerializer.gson().populator().apply(new GsonBuilder())
      .registerTypeAdapter(SocketAddress.class, new SocketAddressDeserializer())
      .registerTypeAdapter(Position.class, new PositionTypeAdapter())
      .registerTypeAdapter(LimboConfig.class, new LimboConfig.LimboConfigDeserializer())
      .registerTypeAdapter(GameProfile.class, new GameProfile.GameProfileDeserializer())
      .create();

  private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
  private final int protocolVersion = 757;
  private final NetworkManager networkManager = new NetworkManager();
  private final GameProfileResolver profileResolver = new GameProfileResolver();
  private final Tablist globalTablist = new GlobalTablist(this.networkManager);

  private final EventLoopGroup bossGroup;
  private final EventLoopGroup workerGroup;
  private final World world;

  private final LimboConfig config;
  private ScheduledFuture<?> keepAliveTask;
  private ScheduledFuture<?> gameloop;

  public LimboServer() throws IOException {
    instance = this;

    this.bossGroup = NettyUtil.newEventLoopGroup(1);
    this.workerGroup = NettyUtil.newEventLoopGroup(Runtime.getRuntime().availableProcessors());
    this.world = new World(Path.of("worlds", "world.world"));

    this.config = this.loadConfig();
  }

  // <editor-folder desc="Server ops" defaultstate="collapsed">

  public void start() {
    this.logger.info("Starting server...");
    final long startTime = System.currentTimeMillis();

    this.loadWorld();

    ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);

    new ServerBootstrap()
        .group(this.bossGroup, this.workerGroup)
        .channel(NettyUtil.serverSocketChannelClass())
        .childHandler(new ClientChannelInitializer())
        .childOption(ChannelOption.TCP_NODELAY, true)
        .localAddress(this.config.address())
        .bind()
        .syncUninterruptibly();

    this.keepAliveTask = this.workerGroup.scheduleWithFixedDelay(this::broadcastKeepAlive, 1, 5, TimeUnit.SECONDS);
    this.gameloop = this.workerGroup.scheduleWithFixedDelay(new GameLoop(this), 1, 50, TimeUnit.MILLISECONDS); // 20 TPS
    Runtime.getRuntime().addShutdownHook(new Thread(this::stop, "Shutdown"));

    final String time = NumberFormat.getInstance().format((System.currentTimeMillis() - startTime) / 1_000D);

    this.logger.info("Binding address to {}.", this.config.address());
    this.logger.info("Done! Server started in {} seconds.", time);
  }

  public void stop() {
    this.networkManager.connections().forEach(connection -> connection.disconnect(Component.text("Server shutting down...")));

    this.keepAliveTask.cancel(true);
    this.gameloop.cancel(true);

    this.bossGroup.shutdownGracefully();
    this.workerGroup.shutdownGracefully();
  }

  // </editor-folder>

  // <editor-folder desc="Getters" defaultstate="collapsed">

  public @NotNull GameProfileResolver profileResolver() {
    return this.profileResolver;
  }

  public @NotNull NetworkManager networkManager() {
    return this.networkManager;
  }

  public @Range(from = 1, to = Integer.MAX_VALUE) int slots() {
    return this.config.slots();
  }

  public @NotNull LimboConfig config() {
    return this.config;
  }

  public @NotNull Logger logger() {
    return this.logger;
  }

  public @NotNull Gson gson() {
    return this.gson;
  }

  public int protocolVersion() {
    return this.protocolVersion;
  }

  public @NotNull World world() {
    return this.world;
  }

  public @NotNull Tablist globalTablist() {
    return this.globalTablist;
  }

  // </editor-folder>

  // <editor-folder desc="Utility methods" defaultstate="collapsed"

  private void broadcastKeepAlive() {
    this.networkManager.connections().forEach(PlayerConnection::sendKeepAlive);
  }

  private @NotNull LimboConfig loadConfig() throws IOException {
    final File file = CONFIG_FILE.toFile();

    try {
      return this.gson.fromJson(new FileReader(file), LimboConfig.class);
    } catch (final FileNotFoundException e) {
      final InputStream stream = this.getClass().getResourceAsStream("/config.json");
      assert stream != null;

      Files.copy(stream, CONFIG_FILE);

      return this.loadConfig();
    }
  }

  private void loadWorld() {
    this.logger.info("Loading world...");
    final long current = System.currentTimeMillis();

    this.world.load();

    final String time = NumberFormat.getInstance().format((System.currentTimeMillis() - current) / 1_000D);
    this.logger.info("World loaded in {} seconds.", time);
  }

  // </editor-folder>

}
