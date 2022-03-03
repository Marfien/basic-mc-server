package dev.neodym.limbo.config;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import org.jetbrains.annotations.NotNull;

public class SocketAddressDeserializer implements JsonDeserializer<SocketAddress> {

  @Override
  public InetSocketAddress deserialize(final @NotNull JsonElement element, final @NotNull Type type, final @NotNull JsonDeserializationContext ctx) throws JsonParseException {
    if (!(element instanceof JsonObject object)) throw new JsonParseException("Invalid config.");

    final String ip = object.get("ip").getAsString();
    final int port = object.get("port").getAsInt();

    return ip.isEmpty() ? new InetSocketAddress(port) : new InetSocketAddress(ip, port);
  }
}
