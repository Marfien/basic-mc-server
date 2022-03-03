package dev.neodym.limbo.network.adapter;

import dev.neodym.limbo.network.LimboByteBuf;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public class UUIDAdapter implements ByteBufAdapter<UUID> {

  @Override
  public void write(final @NotNull UUID uuid, final @NotNull LimboByteBuf buf, final @NotNull Object... args) {
    buf.write(uuid.getMostSignificantBits());
    buf.write(uuid.getLeastSignificantBits());
  }

  @Override
  public @NotNull UUID read(final @NotNull LimboByteBuf buf, final @NotNull Object... args) {
    return new UUID(
        buf.read(long.class),
        buf.read(long.class)
    );
  }
}
