package dev.neodym.limbo.network.protocol.packet.play.serverbound;

import dev.neodym.limbo.network.protocol.listener.PacketListener;
import dev.neodym.limbo.network.protocol.packet.Packet;
import dev.neodym.limbo.util.ChatVisibility;
import dev.neodym.limbo.util.EquipmentSlot;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;

public record ServerboundClientSettingsPacket(
    @NotNull Locale locale,
    byte viewDistance,
    @NotNull ChatVisibility chatVisibility,
    boolean showColorsInChat,
    short skinParts,
    @NotNull EquipmentSlot.Hand mainHand,
    boolean filterText,
    boolean visibleInTab
) implements Packet {

  @Override
  public void handle(final @NotNull PacketListener listener) {
    listener.handleClientSettings(this);
  }
}