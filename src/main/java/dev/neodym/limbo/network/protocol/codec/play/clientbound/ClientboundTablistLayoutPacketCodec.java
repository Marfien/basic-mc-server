package dev.neodym.limbo.network.protocol.codec.play.clientbound;

import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.network.protocol.codec.PacketCodec;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundTablistLayoutPacket;
import io.netty.handler.codec.DecoderException;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class ClientboundTablistLayoutPacketCodec implements PacketCodec<ClientboundTablistLayoutPacket> {

  @Override
  public void encode(final @NotNull ClientboundTablistLayoutPacket packet, final @NotNull LimboByteBuf buf) {
    buf.write(packet.header(), Component.class);
    buf.write(packet.footer(), Component.class);
  }

  @Override
  public @NotNull ClientboundTablistLayoutPacket decode(final @NotNull LimboByteBuf buf) throws DecoderException {
    return new ClientboundTablistLayoutPacket(
        buf.read(Component.class),
        buf.read(Component.class)
    );
  }
}
