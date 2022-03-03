package dev.neodym.limbo.network.protocol;

import com.google.common.collect.Maps;
import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.network.protocol.codec.PacketCodec;
import dev.neodym.limbo.network.protocol.packet.Packet;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

@RequiredArgsConstructor
public class PacketRegistry {

  private final Map<Class<? extends Packet>, Integer> packetIdByClass = Maps.newConcurrentMap();
  private final Map<Class<? extends Packet>, PacketCodec<?>> codecByClass = Maps.newConcurrentMap();
  private final Map<Integer, PacketCodec<?>> codecById = Maps.newConcurrentMap();

  private final @NotNull Packet.Flow direction;

  public @NotNull Packet.Flow direction() {
    return this.direction;
  }

  public void encode(final @NotNull Packet packet, final @NotNull LimboByteBuf buf) {
    buf.write(this.id(packet));

    this.codec(packet).encode(packet, buf);
  }

  public int id(final @NotNull Packet packet) {
    return this.id(packet.getClass());
  }

  public int id(final @NotNull Class<? extends Packet> packetClass) {
    final Integer id = this.packetIdByClass.get(packetClass);

    if (id == null) throw new IllegalStateException("Unregistered packet class: %s".formatted(packetClass));

    return id;
  }

  public @NotNull PacketCodec<? extends Packet> codec(final int packetId) {
    final PacketCodec<? extends Packet> codec = this.codecById.get(packetId);

    if (codec == null) throw new IllegalStateException("Unregistered packet id: 0x%s".formatted(Integer.toHexString(packetId)));

    return codec;
  }

  public @NotNull <P extends Packet> PacketCodec<P> codec(final @NotNull P packet) {
    return (PacketCodec<P>) this.codec(packet.getClass());
  }

  public @NotNull <P extends Packet> PacketCodec<P> codec(final @NotNull Class<P> packetClass) {
    final PacketCodec<P> codec = (PacketCodec < P >) this.codecByClass.get(packetClass);

    if (codec == null) throw new IllegalStateException("Unregistered packet class: %s".formatted(packetClass));

    return codec;
  }

  public Packet decode(final @NotNull LimboByteBuf buf) {
    final int packetId = buf.read(int.class);

    final PacketCodec<? extends Packet> codec = this.codec(packetId);
    return codec.decode(buf);
  }

  public <P extends Packet> void register(final @NotNull PacketRegistryEntry<P> packetRegistryEntry) {
    this.packetIdByClass.put(packetRegistryEntry.packetClass(), packetRegistryEntry.id());
    this.codecById.put(packetRegistryEntry.id(), packetRegistryEntry.codec());
    this.codecByClass.put(packetRegistryEntry.packetClass(), packetRegistryEntry.codec());
  }

  public static record PacketRegistryEntry<P extends Packet>(
      @Range(from = 0, to = Integer.MAX_VALUE) int id,
      @NotNull Class<P> packetClass,
      @NotNull PacketCodec<P> codec
  ) {}

}
