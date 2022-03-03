package dev.neodym.limbo.network;

import dev.neodym.limbo.network.pipline.PacketDecoder;
import dev.neodym.limbo.network.pipline.PacketEncoder;
import dev.neodym.limbo.network.pipline.VarIntFrameDecoder;
import dev.neodym.limbo.network.pipline.VarIntLengthEncoder;
import dev.neodym.limbo.server.LimboServer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.jetbrains.annotations.NotNull;

public class ClientChannelInitializer extends ChannelInitializer<Channel> {

  @Override
  protected void initChannel(final @NotNull Channel channel) {
    final ChannelPipeline pipeline = channel.pipeline();

    final PlayerConnection connection = new PlayerConnection(channel);

    pipeline.addLast("timeout_handler", new ReadTimeoutHandler(LimboServer.get().config().readTimeout()));
    pipeline.addLast("frame_decoder", new VarIntFrameDecoder());
    pipeline.addLast("packet_decoder", new PacketDecoder(connection));
    pipeline.addLast("frame_encoder", new VarIntLengthEncoder());
    pipeline.addLast("packet_encoder", new PacketEncoder(connection));
    pipeline.addLast("packet_handler", connection);
  }

}
