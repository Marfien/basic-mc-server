package dev.neodym.limbo.network.adapter;

import dev.neodym.limbo.network.LimboByteBuf;
import org.jetbrains.annotations.NotNull;

public interface ByteBufAdapter<T> {

  void write(final @NotNull T t, final @NotNull LimboByteBuf buf, final @NotNull Object...args);

  @NotNull T read(final @NotNull LimboByteBuf buf, final @NotNull Object...args);

}
