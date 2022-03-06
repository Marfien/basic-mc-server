package dev.neodym.limbo.config;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.neodym.limbo.util.Position;
import java.lang.reflect.Type;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public record LimboConfig(
    @NotNull SocketAddress address,
    @Range(from = 0, to = Integer.MAX_VALUE) int slots,
    byte[] velocitySecret,
    @Range(from = 0, to = Integer.MAX_VALUE) int readTimeout,
    @NotNull Component description,
    @NotNull Position position,
    @NotNull String name,
    @Range(from = 2, to = Integer.MAX_VALUE) int viewDistance
) {

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final LimboConfig that = (LimboConfig) o;

    return this.slots == that.slots
        && this.readTimeout == that.readTimeout
        && this.address.equals(that.address)
        && Arrays.equals(this.velocitySecret, that.velocitySecret)
        && this.description.equals(that.description)
        && this.position.equals(that.position);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(this.address, this.slots, this.readTimeout, this.description, this.position);
    result = 31 * result + Arrays.hashCode(this.velocitySecret);
    return result;
  }

  @Override
  public String toString() {
    return "LimboConfig{" +
        "address=" + this.address +
        ", slots=" + this.slots +
        ", velocitySecret=" + Arrays.toString(this.velocitySecret) +
        ", readTimeout=" + this.readTimeout +
        ", description=" + this.description +
        ", position=" + this.position +
        '}';
  }

  public static class LimboConfigDeserializer implements JsonDeserializer<LimboConfig> {

    @Override
    public LimboConfig deserialize(final @NotNull JsonElement element, final @NotNull Type type, final @NotNull JsonDeserializationContext ctx)
        throws JsonParseException {
      if (!(element instanceof JsonObject object)) {
        throw new JsonParseException("Invalid config.");
      }

      return new LimboConfig(
          ctx.deserialize(object.get("address"), SocketAddress.class),
          object.get("slots").getAsInt(),
          object.get("velocitySecret").getAsString().getBytes(StandardCharsets.UTF_8),
          object.get("readTimeout").getAsInt(),
          ctx.deserialize(object.getAsJsonObject("ping").get("description"), Component.class),
          ctx.deserialize(object.get("spawn"), Position.class),
          object.get("name").getAsString(),
          object.get("viewDistance").getAsInt()
      );
    }
  }

}
