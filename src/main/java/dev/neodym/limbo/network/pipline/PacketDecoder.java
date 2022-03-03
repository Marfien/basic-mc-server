package dev.neodym.limbo.network.pipline;

import dev.neodym.limbo.exception.PacketDecodeException;
import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.network.PlayerConnection;
import dev.neodym.limbo.network.protocol.packet.Packet;
import dev.neodym.limbo.server.LimboServer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class PacketDecoder extends ByteToMessageDecoder {

  private final @NotNull PlayerConnection connection;

  @Override
  protected void decode(final @NotNull ChannelHandlerContext ctx, final @NotNull ByteBuf byteBuf, final @NotNull List<Object> list) {
    if (!this.connection.isConnected() || byteBuf.readableBytes() <= 0) return;

    final LimboByteBuf limboByteBuf = LimboByteBuf.create(byteBuf, this.connection.locale());

    try {
      final Packet packet = this.connection.state().serverBound().decode(limboByteBuf);

      ctx.fireChannelRead(packet);
    } catch (final PacketDecodeException e) {
      throw e;
    } catch (final IllegalStateException e) {
      LimboServer.get().logger().debug(e.getMessage());
    } catch (final Exception e) {
      throw new PacketDecodeException(e);
    }
  }

}
