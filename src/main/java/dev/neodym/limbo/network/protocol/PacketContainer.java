package dev.neodym.limbo.network.protocol;

import com.google.common.collect.ObjectArrays;
import dev.neodym.limbo.network.protocol.packet.Packet;
import java.util.Collection;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * Container for packets. Used to send multiply packets at once
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class PacketContainer implements Packet {

  private final Packet[] packets;

  public @NotNull Packet[] packets() {
    return this.packets;
  }

  public static @NotNull PacketContainer empty() {
    return new PacketContainer(new Packet[0]);
  }

  public static @NotNull PacketContainer of(final @NotNull Packet @NotNull...packets) {
    return new PacketContainer(packets);
  }

  public static @NotNull PacketContainer of(final @NotNull PacketContainer @NotNull...containers) {
    Packet[] packets = new Packet[0];

    for (final PacketContainer container : containers) {
      packets = ObjectArrays.concat(packets, container.packets(), Packet.class);
    }

    return new PacketContainer(packets);
  }

  public static @NotNull PacketContainer of(final @NotNull Collection<Packet> packets) {
    return new PacketContainer(packets.toArray(Packet[]::new));
  }

}
