package dev.neodym.limbo.network.protocol.codec.play.clientbound;

import com.google.common.collect.Lists;
import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.network.protocol.codec.PacketCodec;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundPlayerInfoPacket;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundPlayerInfoPacket.Action;
import dev.neodym.limbo.util.tablist.Tablist;
import io.netty.handler.codec.DecoderException;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public class ClientboundPlayerInfoPacketCodec implements PacketCodec<ClientboundPlayerInfoPacket> {


  @Override
  public void encode(final @NotNull ClientboundPlayerInfoPacket packet, final @NotNull LimboByteBuf buf) {
    buf.write(packet.action().id());

    buf.write(packet.data().size());
    packet.data().forEach(d -> packet.action().write(buf, d));
  }

  @Override
  public @NotNull ClientboundPlayerInfoPacket decode(final @NotNull LimboByteBuf buf) throws DecoderException {
    ClientboundPlayerInfoPacket.Action action = Action.byId(buf.read(int.class)).orElseThrow(() -> new AssertionError("Unknown action read.")); // latency does not be updated.
    Collection<Tablist.TablistEntry> data = Lists.newArrayList();

    final int size = buf.read(int.class);
    for (int i = 0; i < size; i++) {
      data.add(action.read(buf));
    }

    return new ClientboundPlayerInfoPacket(action, data);
  }
}
