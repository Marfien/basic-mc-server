package dev.neodym.limbo.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
public class Position extends Vec3D {

  private float yaw = 0.0F;
  private float pitch = 0.0F;

  public Position(final double x, final double y, final double z) {
    super(x, y, z);
  }

  public Position(final double x, final double y, final double z, final float yaw, final float pitch) {
    super(x, y, z);
    this.yaw = yaw;
    this.pitch = pitch;
  }

  public float yaw() {
    return this.yaw;
  }

  public float pitch() {
    return this.pitch;
  }

  public @NotNull Position yaw(final float yaw) {
    this.yaw = yaw;
    return this;
  }

  public @NotNull Position pitch(final float pitch) {
    this.pitch = pitch;
    return this;
  }

  public static class PositionTypeAdapter implements JsonDeserializer<Position> {

    @Override
    public Position deserialize(final @NotNull JsonElement jsonElement, final @NotNull Type type, final @NotNull JsonDeserializationContext ctx) throws JsonParseException {
      final JsonObject object = jsonElement.getAsJsonObject();

      return new Position(
          object.get("x").getAsDouble(),
          object.get("y").getAsDouble(),
          object.get("z").getAsDouble(),
          object.has("yaw") ? object.get("yaw").getAsFloat() : 0.0F,
          object.has("pitch") ? object.get("pitch").getAsFloat() : 0.0F
      );
    }
  }

}
