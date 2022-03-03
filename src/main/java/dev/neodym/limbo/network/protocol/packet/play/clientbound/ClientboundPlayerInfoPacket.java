package dev.neodym.limbo.network.protocol.packet.play.clientbound;

import dev.neodym.limbo.auth.GameProfile;
import dev.neodym.limbo.entity.Player;
import dev.neodym.limbo.entity.data.EntityDataAccessor;
import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.network.protocol.packet.Packet;
import dev.neodym.limbo.util.GameMode;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ClientboundPlayerInfoPacket(
    @NotNull ClientboundPlayerInfoPacket.Action action,
    @NotNull Collection<PlayerData> data
) implements Packet {

  public ClientboundPlayerInfoPacket(final @NotNull ClientboundPlayerInfoPacket.Action action, final @NotNull PlayerData @NotNull...data) {
    this(
        action,
        Arrays.asList(data)
    );
  }

  @RequiredArgsConstructor
  public enum Action {

    ADD_PLAYER (0) {
      @Override
      public void write(final @NotNull LimboByteBuf buf, final @NotNull PlayerData data) {
        buf.write(data.profile.uniqueId());
        buf.write(data.profile.name());

        buf.write(data.profile.properties().size());
        data.profile.properties().forEach((name, value) -> {
          buf.write(name);
          buf.write(value);
          buf.write(false); // is signature is presented
        });

        buf.write((int) data.gameMode.id());
        buf.write(1);

        if (data.name == null) {
          buf.write(false);
          return;
        }

        buf.write(true);
        buf.write(data.name, Component.class);
      }

      @Override
      @NotNull
      public ClientboundPlayerInfoPacket.PlayerData read(final @NotNull LimboByteBuf buf) {
        final GameProfile profile = new GameProfile(buf.read(UUID.class), buf.read(String.class, 16));

        final int mapSize = buf.read(int.class);

        for (int i = 0; i < mapSize; i++) {
          profile.properties().put(buf.read(String.class), buf.read(String.class));
        }

        final GameMode mode = GameMode.byId(buf.read(int.class));
        final Component component = buf.readEmpty(int.class).read(boolean.class) ? buf.read(Component.class) : null;

        return new PlayerData(profile, mode, component);
      }
    },
    UPDATE_GAMEMODE (1) {
      @Override
      public void write(final @NotNull LimboByteBuf buf, final @NotNull PlayerData data) {
        buf.write(data.profile.uniqueId());
        buf.write((int) data.gameMode.id());
      }

      @Override
      @NotNull
      public ClientboundPlayerInfoPacket.PlayerData read(final @NotNull LimboByteBuf buf) {
        final GameProfile profile = new GameProfile(buf.read(UUID.class), "NaN");
        final GameMode gamemode = GameMode.byId(buf.read(int.class));
        return new PlayerData(profile, gamemode, null);
      }
    },
    UPDATE_NAME (3) {
      @Override
      public void write(final @NotNull LimboByteBuf buf, final @NotNull PlayerData data) {
        buf.write(data.profile.uniqueId());

        final Component component = data.name;
        if (component == null) {
          buf.write(false);
          return;
        }

        buf.write(true);
        buf.write(component, Component.class);
      }

      @Override
      @NotNull
      public PlayerData read(final @NotNull LimboByteBuf buf) {
        final GameProfile profile = new GameProfile(buf.read(UUID.class), "NaN");
        final Component component = buf.read(boolean.class) ? buf.read(Component.class) : null;

        return new PlayerData(profile, GameMode.UNDEFINED, component);
      }
    },
    REMOVE_PLAYER (4) {
      @Override
      public void write(final @NotNull LimboByteBuf buf, final @NotNull PlayerData data) {
        buf.write(data.profile.uniqueId());
      }

      @Override
      @NotNull
      public PlayerData read(final @NotNull LimboByteBuf buf) {
        return new PlayerData(new GameProfile(buf.read(UUID.class), "NaN"), GameMode.UNDEFINED, null);
      }
    };

    private final int id;

    public abstract void write(final @NotNull LimboByteBuf buf, final @NotNull PlayerData data);
    public abstract @NotNull ClientboundPlayerInfoPacket.PlayerData read(final @NotNull LimboByteBuf buf);

    public int id() {
      return this.id;
    }

    public static Optional<Action> byId(final int id) {
      return Arrays.stream(values()).filter(a -> a.id == id).findFirst();
    }

  }

  public static record PlayerData(
      @NotNull GameProfile profile,
      @NotNull GameMode gameMode,
      @Nullable Component name
  ) {

    public PlayerData(final @NotNull Player player) {
      this(
          player.profile(),
          player.gamemode(),
          player.metadata().get(EntityDataAccessor.CUSTOM_NAME).orElse(Optional.empty()).orElse(null)
      );
    }
  }

}
