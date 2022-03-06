package dev.neodym.limbo.network.protocol.packet.play.clientbound;

import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.network.protocol.packet.Packet;
import dev.neodym.limbo.util.tablist.Tablist;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public record ClientboundPlayerInfoPacket(
    @NotNull ClientboundPlayerInfoPacket.Action action,
    @NotNull Collection<Tablist.TablistEntry> data
) implements Packet {

  public ClientboundPlayerInfoPacket(final @NotNull ClientboundPlayerInfoPacket.Action action, final @NotNull Tablist.TablistEntry @NotNull...data) {
    this(
        action,
        Arrays.asList(data)
    );
  }

  public enum Action {

    ADD_PLAYER {
      @Override
      public void write(final @NotNull LimboByteBuf buf, final @NotNull Tablist.TablistEntry data) {
        buf.write(data.uniqueId());
        buf.write(data.profile().name());

        buf.write(data.profile().properties().size());
        data.profile().properties().forEach((name, value) -> {
          buf.write(name);
          buf.write(value);
          buf.write(false); // is signature presented
        });

        buf.write((int) data.gamemode().id());
        buf.write(data.ping());

        buf.writeOpt(data.customName(), Component.class);
      }

      /*
      @Override
      @NotNull
      public Tablist.TablistEntry read(final @NotNull LimboByteBuf buf) {
        final GameProfile profile = new GameProfile(buf.read(UUID.class), buf.read(String.class, 16));

        final int mapSize = buf.read(int.class);

        for (int i = 0; i < mapSize; i++) {
          profile.properties().put(buf.read(String.class), buf.read(String.class));
        }

        final GameMode mode = GameMode.byId(buf.read(int.class));
        final Component component = buf.readEmpty(int.class).read(boolean.class) ? buf.read(Component.class) : null;

        return new (profile, mode, component);
      }
       */
    },
    UPDATE_GAMEMODE {
      @Override
      public void write(final @NotNull LimboByteBuf buf, final @NotNull Tablist.TablistEntry data) {
        buf.write(data.uniqueId());
        buf.write((int) data.gamemode().id());
      }

      /*
      @Override
      @NotNull
      public ClientboundPlayerInfoPacket.PlayerData read(final @NotNull LimboByteBuf buf) {
        final GameProfile profile = new GameProfile(buf.read(UUID.class), "NaN");
        final GameMode gamemode = GameMode.byId(buf.read(int.class));
        return new PlayerData(profile, gamemode, null);
      }
       */
    },
    UPDATE_PING {
      @Override
      public void write(final @NotNull LimboByteBuf buf, final @NotNull Tablist.TablistEntry data) {
        buf.write(data.uniqueId());
        buf.write(data.ping());
      }
    },
    UPDATE_CUSTOM_NAME {
      @Override
      public void write(final @NotNull LimboByteBuf buf, final @NotNull Tablist.TablistEntry data) {
        buf.write(data.uniqueId());
        buf.writeOpt(data.customName(), Component.class);
      }

      /*
      @Override
      @NotNull
      public PlayerData read(final @NotNull LimboByteBuf buf) {
        final GameProfile profile = new GameProfile(buf.read(UUID.class), "NaN");
        final Component component = buf.read(boolean.class) ? buf.read(Component.class) : null;

        return new PlayerData(profile, GameMode.UNDEFINED, component);
      }
       */
    },
    REMOVE_PLAYER {
      @Override
      public void write(final @NotNull LimboByteBuf buf, final @NotNull Tablist.TablistEntry data) {
        buf.write(data.uniqueId());
      }

      /*
      @Override
      @NotNull
      public PlayerData read(final @NotNull LimboByteBuf buf) {
        return new PlayerData(new GameProfile(buf.read(UUID.class), "NaN"), GameMode.UNDEFINED, null);
      }
       */
    };

    private final int id = this.ordinal();

    public abstract void write(final @NotNull LimboByteBuf buf, final @NotNull Tablist.TablistEntry data);

    public @NotNull Tablist.TablistEntry read(final @NotNull LimboByteBuf buf) {
      throw new UnsupportedOperationException("Not implemented yet.");
    }

    public int id() {
      return this.id;
    }

    public static Optional<Action> byId(final int id) {
      return Arrays.stream(values()).filter(a -> a.id == id).findFirst();
    }

  }

}
