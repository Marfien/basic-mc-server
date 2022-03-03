package dev.neodym.limbo.util;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Range;

@UtilityClass
public class NettyUtil {

  private static final boolean EPOLL = Epoll.isAvailable();
  private static final boolean KQUEUE = KQueue.isAvailable();

  public static EventLoopGroup newEventLoopGroup(final @Range(from = 1, to = Integer.MAX_VALUE) int threads) {
    if (EPOLL) return new EpollEventLoopGroup(threads);

    return KQUEUE
        ? new KQueueEventLoopGroup(threads)
        : new NioEventLoopGroup(threads);
  }

  public static Class<? extends ServerSocketChannel> serverSocketChannelClass() {
    if (EPOLL) return EpollServerSocketChannel.class;

    return KQUEUE
        ? KQueueServerSocketChannel.class
        : NioServerSocketChannel.class;
  }

}
