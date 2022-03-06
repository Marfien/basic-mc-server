package dev.neodym.limbo.network.protocol.listener;

import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.network.PlayerConnection;
import dev.neodym.limbo.network.protocol.packet.login.clientbound.ClientboundLoginPluginRequestPacket;
import dev.neodym.limbo.network.protocol.packet.login.serverbound.ServerboundLoginPluginResponsePacket;
import dev.neodym.limbo.network.protocol.packet.login.serverbound.ServerboundLoginStartPacket;
import dev.neodym.limbo.server.LimboServer;
import dev.neodym.limbo.auth.GameProfile;
import dev.neodym.limbo.util.PluginMessageChannel;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

public class LoginListener extends BasicPacketListener {

  private static final LimboServer SERVER = LimboServer.get();

  private int velocityLoginId = -1;
  private SocketAddress address;
  private GameProfile profile;

  public LoginListener(final @NotNull PlayerConnection connection) {
    super(connection);
  }

  public SocketAddress address() {
    return this.address;
  }

  public GameProfile profile() {
    return this.profile;
  }

  @Override
  public void handleLoginPluginResponse(final @NotNull ServerboundLoginPluginResponsePacket packet) {
    if (packet.messageId() != this.velocityLoginId) return;

    final LimboByteBuf data = packet.data();
    if (!packet.succeeded() || data.isEmpty()) {
      super.connection.disconnect(Component.text("You have to connect through a proxy."));
      return;
    }

    if (!checkVelocitySecret(data)) {
      super.connection.disconnect(Component.text("Cannot verify forwarded player info"));
      return;
    }

    this.address = new InetSocketAddress(data.read(String.class), ((InetSocketAddress) super.connection.address()).getPort());
    try {
      this.profile = SERVER.profileResolver().create(data.read(UUID.class)); // username will be given by GameProfileResolver
    } catch (final Exception e) {
      this.connection.disconnect(Component.text("Cannot authenticate with Mojang."));
    }
  }

  @Override
  public void handleLoginStart(final @NotNull ServerboundLoginStartPacket packet) {
    if (super.connection.protocolVersion() != SERVER.protocolVersion()) {
      super.connection.disconnect(Component.text(
          """
              Unsupported version!
              Server: %d
              You: %d
              """.formatted(SERVER.protocolVersion(), super.connection.protocolVersion())));
      return;
    }

    if (SERVER.networkManager().count() >= SERVER.slots()) {
      super.connection.disconnect(Component.text("This Limbo is already full!").color(TextColor.color(234, 54, 132)));
      return;
    }

    final ClientboundLoginPluginRequestPacket requestLoginPacket = new ClientboundLoginPluginRequestPacket(PluginMessageChannel.VELOCITY_INFO);
    this.velocityLoginId = requestLoginPacket.messageId();
    super.connection.sendPacket(requestLoginPacket);
  }

  @Override
  public boolean isCompleted() {
    return this.address != null
        && this.profile != null;
  }

  private static boolean checkVelocitySecret(final @NotNull LimboByteBuf byteBuf) {
    final byte[] signature = new byte[32];
    byteBuf.readBytes(signature);

    final byte[] data = new byte[byteBuf.readableBytes()];
    byteBuf.getBytes(byteBuf.readerIndex(), data);

    try {
      final Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(SERVER.config().velocitySecret(), "HmacSHA256"));

      final byte[] mySignature = mac.doFinal(data);
      if (!MessageDigest.isEqual(signature, mySignature)) return false;
    } catch (final InvalidKeyException | NoSuchAlgorithmException e) {
      throw new AssertionError(e);
    }

    final int version = byteBuf.read(int.class);

    if (version == 1)  return true;

    throw new IllegalStateException("Unsupported forwarding version " + version + ", wanted " + '\001');
  }
}
