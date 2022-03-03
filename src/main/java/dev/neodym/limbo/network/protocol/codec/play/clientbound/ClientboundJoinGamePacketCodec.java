package dev.neodym.limbo.network.protocol.codec.play.clientbound;

import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.network.protocol.codec.PacketCodec;
import dev.neodym.limbo.network.protocol.packet.play.clientbound.ClientboundJoinGamePacket;
import dev.neodym.limbo.util.GameMode;
import dev.neodym.limbo.world.biom.Biome;
import dev.neodym.limbo.world.dimension.DimensionType;
import io.netty.handler.codec.DecoderException;
import java.util.Arrays;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.ArrayBinaryTag;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.BinaryTagLike;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;
import org.jetbrains.annotations.NotNull;

public class ClientboundJoinGamePacketCodec implements PacketCodec<ClientboundJoinGamePacket> {

  private static final CompoundBinaryTag CODEC = Biome.palette().insert(DimensionType.palette().insert(CompoundBinaryTag.builder().build()));

  @Override
  public void encode(final @NotNull ClientboundJoinGamePacket packet, final @NotNull LimboByteBuf buf) {
    buf.writeInt(packet.entityId());
    buf.write(packet.isHardcore());
    buf.write(packet.gamemode().id());
    buf.write(packet.previousGamemode().id());
    buf.writeArray(packet.worldNames().stream().map(Key::asString).toArray(String[]::new));
    buf.write(CODEC, CompoundBinaryTag.class);
    buf.write(packet.dimensionType().asBinaryTag(), CompoundBinaryTag.class);
    buf.write(packet.dimension().asString());
    buf.write(packet.hashedSeed());
    buf.write(-1); // unused
    buf.write(packet.viewDistance());
    buf.write(packet.viewDistance());
    buf.write(packet.reducedDebugInfo());
    buf.write(packet.enableRespawnScreen());
    buf.write(packet.isDebugWorld());
    buf.write(packet.isFlatWorld());
  }

  @Override
  public @NotNull ClientboundJoinGamePacket decode(final @NotNull LimboByteBuf buf) throws DecoderException {
    return new ClientboundJoinGamePacket(
        buf.readInt(),
        buf.read(boolean.class),
        GameMode.byId(buf.read(int.class)),
        GameMode.byId(buf.read(int.class)),
        Arrays.stream(buf.readArray(String.class, String[]::new)).map(Key::key).toList(),
        DimensionType.palette().byId(buf.readEmpty(CompoundBinaryTag.class).read(CompoundBinaryTag.class).getInt("id")).orElseThrow(() -> new DecoderException("Invalid DimensionType")),
        Key.key(buf.read(String.class)),
        buf.read(long.class),
        buf.readEmpty(int.class).read(int.class),
        buf.readEmpty(int.class).read(boolean.class),
        buf.read(boolean.class),
        buf.read(boolean.class),
        buf.read(boolean.class)
    );
  }
}
