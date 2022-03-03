package dev.neodym.limbo.network.protocol.packet.play.clientbound;

import dev.neodym.limbo.entity.Entity;
import dev.neodym.limbo.entity.data.EntityData;
import dev.neodym.limbo.entity.data.EntityData.EntityDataEntry;
import dev.neodym.limbo.network.protocol.packet.Packet;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public record ClientboundEntityDataPacket(
    int entityId,
    @NotNull Collection<EntityDataEntry<?>> data
) implements Packet {

  public ClientboundEntityDataPacket(final @NotNull EntityData data, final boolean forceUpdateAll) {
    this(
        data.entity().entityId(),
        forceUpdateAll ? data.all() : data.allChanged()
    );
  }

  public ClientboundEntityDataPacket(final @NotNull Entity entity) {
    this(
        entity.metadata(),
        false
    );
  }
}
