package dev.neodym.limbo.network.pipline;

import dev.neodym.limbo.exception.PacketEncodeException;
import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.network.PlayerConnection;
import dev.neodym.limbo.network.protocol.PacketRegistry;
import dev.neodym.limbo.network.protocol.packet.Packet;
import dev.neodym.limbo.server.LimboServer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class PacketEncoder extends MessageToByteEncoder<Packet> {

  private final @NotNull PlayerConnection connection;

  @Override
  protected void encode(final @NotNull ChannelHandlerContext ctx, final @NotNull Packet packet, final @NotNull ByteBuf byteBuf) {
    final LimboByteBuf limboByteBuf = LimboByteBuf.create(byteBuf, this.connection.locale());
    final PacketRegistry registry = this.connection.state().clientBound();

    final int packetId = registry.id(packet);
    try {
      registry.encode(packet, limboByteBuf);

      LimboServer.get().logger().info("Sending packet {}[0x{}] ({} bytes) to {}.", packet.getClass().getSimpleName(), Integer.toHexString(packetId), limboByteBuf.readableBytes(), ctx.channel().remoteAddress());
    } catch (final PacketEncodeException e) {
      throw e;
    } catch (final Throwable e) {
      throw new PacketEncodeException(packet, e);
    }
  }
}
