package dev.neodym.limbo.auth;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public class GameProfile {

  private final @NotNull UUID uniqueId;
  private final @NotNull String name;

  private final Map<String, String> properties = Maps.newConcurrentMap();

  public GameProfile(final @NotNull UUID uniqueId, final @NotNull String name) {
    this.uniqueId = uniqueId;
    this.name = name;
  }

  public @NotNull UUID uniqueId() {
    return this.uniqueId;
  }

  public @NotNull String name() {
    return this.name;
  }

  public @NotNull Map<String, String> properties() {
    return this.properties;
  }

  public static class GameProfileDeserializer implements JsonDeserializer<GameProfile> {

    private static final String REPLACE_REGEX = "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)";

    @Override
    public GameProfile deserialize(final @NotNull JsonElement jsonElement, final @NotNull Type type, final @NotNull JsonDeserializationContext ctx) throws JsonParseException {
      final JsonObject object = jsonElement.getAsJsonObject();

      final UUID uniqueId = UUID.fromString(object.get("id").getAsString().replaceFirst(REPLACE_REGEX, "$1-$2-$3-$4-$5"));
      final String name = object.get("name").getAsString();

      final GameProfile profile = new GameProfile(uniqueId, name);

      object.getAsJsonArray("properties").forEach(element -> {
        if (element.isJsonObject()) return;

        final JsonObject property = element.getAsJsonObject();
        final String propertyName = property.get("name").getAsString();
        final String value = property.get("value").getAsString();

        profile.properties.put(propertyName, value);
      });

      return profile;
    }
  }

}
