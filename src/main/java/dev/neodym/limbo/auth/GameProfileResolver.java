package dev.neodym.limbo.auth;

import com.google.common.net.HttpHeaders;
import com.google.gson.JsonObject;
import dev.neodym.limbo.server.LimboServer;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;

public class GameProfileResolver {

  private static final String MOJANG_BASE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s";

  // redis cache
  // spring service

  // check when name and uuid changes

  private final HttpClient client = HttpClients.custom().build();

  public void fillGameProfile(final @NotNull GameProfile profile) {
    throw new UnsupportedOperationException("Not implemented yet.");
  }

  public @NotNull GameProfile create(final @NotNull UUID playerId) throws IOException {
    final HttpUriRequest request = RequestBuilder.get()
        .setUri(MOJANG_BASE_URL.formatted(playerId.toString().replace("-", ""))) // .setUri(this.baseUrl + "/profile/%s/")
        .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
        .build();

    final HttpResponse response = this.client.execute(request);
    return LimboServer.get()
        .gson()
        .fromJson(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8), GameProfile.class);
  }

  public boolean cached(final @NotNull UUID playerId) {
    throw new UnsupportedOperationException("Not implemented yet.");
  }

}
