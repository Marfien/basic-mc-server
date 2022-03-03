package dev.neodym.limbo.network.protocol.codec;

import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.network.protocol.packet.Packet;
import io.netty.handler.codec.DecoderException;
import org.jetbrains.annotations.NotNull;

public interface PacketCodec<T extends Packet> {

  void encode(final @NotNull T packet, final @NotNull LimboByteBuf buf);
  @NotNull T decode(final @NotNull LimboByteBuf buf) throws DecoderException;

}
