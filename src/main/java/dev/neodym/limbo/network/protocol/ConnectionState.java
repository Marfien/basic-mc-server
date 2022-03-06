package dev.neodym.limbo.network.protocol;

import dev.neodym.limbo.network.PlayerConnection;
import dev.neodym.limbo.network.protocol.PacketRegistry.PacketRegistryEntry;
import dev.neodym.limbo.network.protocol.codec.handshake.ServerboundHandshakePacketCodec;
import dev.neodym.limbo.network.protocol.codec.login.clientbound.ClientboundLoginDisconnectPacketCodec;
import dev.neodym.limbo.network.protocol.codec.login.clientbound.ClientboundLoginPluginRequestPacketCodec;
import dev.neodym.limbo.network.protocol.codec.login.clientbound.ClientboundLoginSuccessPacketCodec;
import dev.neodym.limbo.network.protocol.codec.login.serverbound.ServerboundLoginPluginResponsePacketCodec;
import dev.neodym.limbo.network.protocol.codec.login.serverbound.ServerboundLoginStartPacketCodec;
import dev.neodym.limbo.network.protocol.codec.play.KeepAlivePacketCodec;
import dev.neodym.limbo.network.protocol.codec.play.clientbound.ClientboundChunkDataAndLightPacketCodec;
import dev.neodym.limbo.network.protocol.codec.play.clientbound.ClientboundChunkUnloadPacketCodec;
import dev.neodym.limbo.network.protocol.codec.play.clientbound.ClientboundChunkViewPacketCodec;
import dev.neodym.limbo.network.protocol.codec.play.clientbound.ClientboundDeclareCommandsPacketCodec;
import dev.neodym.limbo.network.protocol.codec.play.clientbound.ClientboundEntityDataPacketCodec;
import dev.neodym.limbo.network.protocol.codec.play.clientbound.ClientboundEntitySoundEffectPacketCodec;
import dev.neodym.limbo.network.protocol.codec.play.clientbound.ClientboundJoinGamePacketCodec;
import dev.neodym.limbo.network.protocol.codec.play.clientbound.ClientboundNamedSoundEffectPacketCodec;
import dev.neodym.limbo.network.protocol.codec.play.clientbound.ClientboundPlayerAbilitiesPacketCodec;
import dev.neodym.limbo.network.protocol.codec.play.clientbound.ClientboundPlayerInfoPacketCodec;
import dev.neodym.limbo.network.protocol.codec.play.clientbound.ClientboundPlayerPositionPacketCodec;
import dev.neodym.limbo.network.protocol.codec.play.clientbound.ClientboundSoundPacketCodec;
import dev.neodym.limbo.network.protocol.codec.play.clientbound.ClientboundTablistLayoutPacketCodec;
import dev.neodym.limbo.network.protocol.codec.play.clientbound.ClientboundUpdateTimePacketCodec;
import dev.neodym.limbo.network.protocol.codec.play.serverbound.ServerboundClientSettingsPacketCodec;
import dev.neodym.limbo.network.protocol.codec.play.serverbound.ServerboundPlayerLookPacketCodec;
import dev.neodym.limbo.network.protocol.codec.play.serverbound.ServerboundPlayerPositionAndLookCodec;
import dev.neodym.limbo.network.protocol.codec.play.serverbound.ServerboundPlayerPositionPacketCodec;
import dev.neodym.limbo.network.protocol.codec.status.StatusPingPacketCodec;
import dev.neodym.limbo.network.protocol.codec.status.clientbound.ClientboundStatusResponsePacketCodec;
import dev.neodym.limbo.network.protocol.codec.status.serverbound.ServerboundStatusRequestPacketCodec;
import dev.neodym.limbo.network.protocol.listener.HandshakeListener;
import dev.neodym.limbo.network.protocol.listener.LoginListener;
import dev.neodym.limbo.network.protocol.listener.PacketListener;
import dev.neodym.limbo.network.protocol.listener.PlayGameListener;
import dev.neodym.limbo.network.protocol.listener.StatusListener;
import dev.neodym.limbo.network.protocol.packet.Packet;
import dev.neodym.limbo.network.protocol.packet.handshake.ServerboundHandshakePacket;
import dev.neodym.limbo.network.protocol.packet.login.clientbound.ClientboundLoginDisconnectPacket;
import dev.neodym.limbo.network.protocol.packet.login.clientbound.ClientboundLoginPluginRequestPacket;
import dev.neodym.limbo.network.protocol.packet.login.clientbound.ClientboundLoginSuccessPacket;
import dev.neodym.limbo.network.protocol.packet.login.serverbound.ServerboundLoginPluginResponsePacket;
import dev.neodym.limbo.network.protocol.packet.login.serverbound.ServerboundLoginStartPacket;
import dev.neodym.limbo.network.protocol.packet.play.KeepAlivePacket;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundChunkDataAndLightPacket;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundChunkUnloadPacket;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundChunkViewPacket;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundDeclareCommandsPacket;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundEntityDataPacket;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundEntitySoundEffectPacket;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundJoinGamePacket;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundNamedSoundEffectPacket;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundPlayerAbilitiesPacket;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundPlayerInfoPacket;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundPlayerPositionPacket;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundSoundPacket;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundTablistLayoutPacket;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundUpdateTimePacket;
import dev.neodym.limbo.network.protocol.packet.play.serverbound.ServerboundClientSettingsPacket;
import dev.neodym.limbo.network.protocol.packet.play.serverbound.ServerboundPlayerLookPacket;
import dev.neodym.limbo.network.protocol.packet.play.serverbound.ServerboundPlayerPositionAndLook;
import dev.neodym.limbo.network.protocol.packet.play.serverbound.ServerboundPlayerPositionPacket;
import dev.neodym.limbo.network.protocol.packet.status.ClientboundStatusResponsePacket;
import dev.neodym.limbo.network.protocol.packet.status.ServerboundStatusRequestPacket;
import dev.neodym.limbo.network.protocol.packet.status.StatusPingPacket;
import dev.neodym.limbo.server.LimboServer;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public enum ConnectionState {

  // <editor-folder desc="HANDSHAKE" defaultstate="collapsed">
  HANDSHAKE (
      HandshakeListener::new,
      new PacketRegistryEntry[]{
          new PacketRegistryEntry<>(0x00, ServerboundHandshakePacket.class, new ServerboundHandshakePacketCodec())
      },
      new PacketRegistryEntry[0]
  ),

  // </editor-folder>
  // <editor-folder desc="STATUS" defaultstate="collapsed"
  STATUS (
      StatusListener::new,
      new PacketRegistryEntry[]{
          new PacketRegistryEntry<>(0x00, ServerboundStatusRequestPacket.class, new ServerboundStatusRequestPacketCodec()),
          new PacketRegistryEntry<>(0x01, StatusPingPacket.class, new StatusPingPacketCodec())
      },
      new PacketRegistryEntry[]{
          new PacketRegistryEntry<>(0x00, ClientboundStatusResponsePacket.class, new ClientboundStatusResponsePacketCodec(LimboServer.get())),
          new PacketRegistryEntry<>(0x01, StatusPingPacket.class, new StatusPingPacketCodec())
      }
  ),
  // </editor-folder>
  // <editor-folder desc="LOGIN" defaultstate="collapsed">
  LOGIN (
      LoginListener::new,
      new PacketRegistryEntry[]{
          new PacketRegistryEntry<>(0x00, ServerboundLoginStartPacket.class, new ServerboundLoginStartPacketCodec()),
          // encrypt packet is not needed due to velocities modern player info forwarding
          new PacketRegistryEntry<>(0x02, ServerboundLoginPluginResponsePacket.class, new ServerboundLoginPluginResponsePacketCodec()),
      },
      new PacketRegistryEntry[]{
          new PacketRegistryEntry<>(0x00, ClientboundLoginDisconnectPacket.class, new ClientboundLoginDisconnectPacketCodec()),
          // encrypt packet is not needed due to velocities modern player info forwarding
          new PacketRegistryEntry<>(0x02, ClientboundLoginSuccessPacket.class, new ClientboundLoginSuccessPacketCodec()),
          // we don't use compressed packets
          new PacketRegistryEntry<>(0x04, ClientboundLoginPluginRequestPacket.class, new ClientboundLoginPluginRequestPacketCodec())
      }
  ),
  // </editor-folder>
  // <editor-folder desc="PLAY" defaultstate="collapsed">
  PLAY (
      PlayGameListener::new,
      new PacketRegistryEntry[] {
          new PacketRegistryEntry<>(0x05, ServerboundClientSettingsPacket.class, new ServerboundClientSettingsPacketCodec()),
          new PacketRegistryEntry<>(0x0F, KeepAlivePacket.class, new KeepAlivePacketCodec()),
          new PacketRegistryEntry<>(0x11, ServerboundPlayerPositionPacket.class, new ServerboundPlayerPositionPacketCodec()),
          new PacketRegistryEntry<>(0x12, ServerboundPlayerPositionAndLook.class, new ServerboundPlayerPositionAndLookCodec()),
          new PacketRegistryEntry<>(0x13, ServerboundPlayerLookPacket.class, new ServerboundPlayerLookPacketCodec())
      },
      new PacketRegistryEntry[] {
          new PacketRegistryEntry<>(0x12, ClientboundDeclareCommandsPacket.class, new ClientboundDeclareCommandsPacketCodec()),
          new PacketRegistryEntry<>(0x19, ClientboundNamedSoundEffectPacket.class, new ClientboundNamedSoundEffectPacketCodec()),
          new PacketRegistryEntry<>(0x1D, ClientboundChunkUnloadPacket.class, new ClientboundChunkUnloadPacketCodec()),
          new PacketRegistryEntry<>(0x21, KeepAlivePacket.class, new KeepAlivePacketCodec()),
          new PacketRegistryEntry<>(0x22, ClientboundChunkDataAndLightPacket.class, new ClientboundChunkDataAndLightPacketCodec()),
          new PacketRegistryEntry<>(0x26, ClientboundJoinGamePacket.class, new ClientboundJoinGamePacketCodec()),
          new PacketRegistryEntry<>(0x32, ClientboundPlayerAbilitiesPacket.class, new ClientboundPlayerAbilitiesPacketCodec()),
          new PacketRegistryEntry<>(0x36, ClientboundPlayerInfoPacket.class, new ClientboundPlayerInfoPacketCodec()),
          new PacketRegistryEntry<>(0x38, ClientboundPlayerPositionPacket.class, new ClientboundPlayerPositionPacketCodec()),
          new PacketRegistryEntry<>(0x49, ClientboundChunkViewPacket.class, new ClientboundChunkViewPacketCodec()),
          new PacketRegistryEntry<>(0x4D, ClientboundEntityDataPacket.class, new ClientboundEntityDataPacketCodec()),
          new PacketRegistryEntry<>(0x59, ClientboundUpdateTimePacket.class, new ClientboundUpdateTimePacketCodec()),
          new PacketRegistryEntry<>(0x5C, ClientboundEntitySoundEffectPacket.class, new ClientboundEntitySoundEffectPacketCodec()),
          new PacketRegistryEntry<>(0x5D, ClientboundSoundPacket.class, new ClientboundSoundPacketCodec()),
          new PacketRegistryEntry<>(0x5F, ClientboundTablistLayoutPacket.class, new ClientboundTablistLayoutPacketCodec())
      }
  );
  // </editor-folder>

  private final Function<PlayerConnection, PacketListener> createPacketListener;
  private final PacketRegistry serverBound = new PacketRegistry(Packet.Flow.SERVERBOUND);
  private final PacketRegistry clientBound = new PacketRegistry(Packet.Flow.CLIENTBOUND);

  ConnectionState(final @NotNull Function<PlayerConnection, PacketListener> createListenerFunction, final @NotNull PacketRegistryEntry<? extends Packet>[] serverBound, final @NotNull PacketRegistryEntry<? extends Packet>[] clientBound) {
    this.createPacketListener = createListenerFunction;
    Arrays.stream(serverBound).forEach(this.serverBound::register);
    Arrays.stream(clientBound).forEach(this.clientBound::register);
  }

  public @NotNull PacketRegistry serverBound() {
    return this.serverBound;
  }

  public @NotNull PacketRegistry clientBound() {
    return this.clientBound;
  }

  public @NotNull PacketListener createPacketListener(final @NotNull PlayerConnection connection) {
    return this.createPacketListener.apply(connection);
  }

  public int id() {
    return this.ordinal();
  }

  public @NotNull Optional<ConnectionState> nextState() {
    return this == PLAY
        ? Optional.empty()
        : Optional.of(values()[this.id() + 1]);
  }

}
