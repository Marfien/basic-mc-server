package dev.neodym.limbo.network.protocol.codec.status.clientbound;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.network.protocol.codec.PacketCodec;
import dev.neodym.limbo.network.protocol.packet.status.ClientboundStatusResponsePacket;
import dev.neodym.limbo.server.LimboServer;
import io.netty.handler.codec.DecoderException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class ClientboundStatusResponsePacketCodec implements PacketCodec<ClientboundStatusResponsePacket> {

  private static final String VERSION_KEY = "version";
  private static final String NAME_KEY = "name";
  private static final String PROTOCOL_KEY = "protocol";
  private static final String PLAYERS_KEY = "players";
  private static final String SLOTS_KEY = "max";
  private static final String ONLINE_KEY = "online";
  private static final String SAMPLE_KEY = "sample";
  private static final String DESCRIPTION_KEY = "description";

  private final LimboServer server;

  @Override
  public void encode(final @NotNull ClientboundStatusResponsePacket packet, final @NotNull LimboByteBuf buf) {
    final JsonObject object = new JsonObject();

    final JsonObject version = new JsonObject();
    version.addProperty(NAME_KEY, packet.name());
    version.addProperty(PROTOCOL_KEY, packet.protocol());

    object.add(VERSION_KEY, version);

    final JsonObject players = new JsonObject();
    final JsonArray sample = new JsonArray();
    packet.sample().forEach(sample::add);

    players.addProperty(SLOTS_KEY, packet.slots());
    players.addProperty(ONLINE_KEY, packet.online());
    players.add(SAMPLE_KEY, sample);

    object.add(PLAYERS_KEY, players);

    object.add(DESCRIPTION_KEY, this.server.gson().toJsonTree(packet.description()));

    buf.write(object.toString());
  }

  @Override
  public @NotNull ClientboundStatusResponsePacket decode(final @NotNull LimboByteBuf buf) throws DecoderException {
    final JsonObject object = this.server.gson().fromJson(buf.read(String.class), JsonObject.class);

    final JsonObject version = object.getAsJsonObject(VERSION_KEY);
    final String name = version.get(NAME_KEY).getAsString();
    final int protocol = version.get(PROTOCOL_KEY).getAsInt();

    final JsonObject players = object.getAsJsonObject(PLAYERS_KEY);
    final List<String> sample = Lists.newArrayList();

    object.getAsJsonArray(SAMPLE_KEY).forEach(s -> sample.add(s.getAsString()));
    final int slots = players.get(SLOTS_KEY).getAsInt();
    final int online = players.get(ONLINE_KEY).getAsInt();

    final Component description = this.server.gson().fromJson(object.get(DESCRIPTION_KEY), Component.class);

    return new ClientboundStatusResponsePacket(
        name,
        protocol,
        slots,
        online,
        sample,
        description
    );
  }
}
