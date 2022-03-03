package dev.neodym.limbo.network.protocol.codec.handshake;

import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.network.protocol.ConnectionState;
import dev.neodym.limbo.network.protocol.codec.PacketCodec;
import dev.neodym.limbo.network.protocol.packet.handshake.ServerboundHandshakePacket;
import io.netty.handler.codec.DecoderException;
import org.jetbrains.annotations.NotNull;

public class ServerboundHandshakePacketCodec implements PacketCodec<ServerboundHandshakePacket> {

  @Override
  public void encode(final @NotNull ServerboundHandshakePacket packet, final @NotNull LimboByteBuf buf) {
    buf.write(packet.protocolVersion());
    buf.write(packet.host());
    buf.write(packet.port());
    buf.write(packet.nextState().ordinal());
  }

  @Override
  public @NotNull ServerboundHandshakePacket decode(final @NotNull LimboByteBuf buf) throws DecoderException {
    return new ServerboundHandshakePacket(
        buf.read(int.class),
        buf.read(String.class),
        buf.readUnsignedShort(),
        ConnectionState.values()[buf.read(int.class)]
    );
  }
}
