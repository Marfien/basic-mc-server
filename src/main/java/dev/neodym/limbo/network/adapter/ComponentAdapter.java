package dev.neodym.limbo.network.adapter;

import dev.neodym.limbo.network.LimboByteBuf;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
import org.jetbrains.annotations.NotNull;

public class ComponentAdapter implements ByteBufAdapter<Component> {

  private static final @NotNull GsonComponentSerializer SERIALIZER = GsonComponentSerializer.gson();

  @Override
  public void write(final @NotNull Component component, final @NotNull LimboByteBuf buf, final @NotNull Object... args) {
    buf.write(
        SERIALIZER.serialize(
            GlobalTranslator.render(component, buf.locale())
        ), 262144
    );
  }

  @Override
  public @NotNull Component read(final @NotNull LimboByteBuf buf, final @NotNull Object... args) {
    return SERIALIZER.deserialize(buf.read(String.class, 262144));
  }
}
