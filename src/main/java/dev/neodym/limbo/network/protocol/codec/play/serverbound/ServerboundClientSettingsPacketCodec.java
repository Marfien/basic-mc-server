package dev.neodym.limbo.network.protocol.codec.play.serverbound;

import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.network.protocol.codec.PacketCodec;
import dev.neodym.limbo.network.protocol.packet.play.serverbound.ServerboundClientSettingsPacket;
import dev.neodym.limbo.util.ChatVisibility;
import dev.neodym.limbo.util.EquipmentSlot;
import io.netty.handler.codec.DecoderException;
import java.util.Locale;
import java.util.Objects;
import net.kyori.adventure.translation.Translator;
import org.jetbrains.annotations.NotNull;

public class ServerboundClientSettingsPacketCodec implements PacketCodec<ServerboundClientSettingsPacket> {

  @Override
  public void encode(final @NotNull ServerboundClientSettingsPacket packet, final @NotNull LimboByteBuf buf) {
    buf.write(packet.locale().toLanguageTag());
    buf.write(packet.viewDistance());
    buf.write(packet.chatVisibility().ordinal());
    buf.write(packet.showColorsInChat());
    buf.write((byte) packet.skinParts());
    buf.write(packet.mainHand().ordinal());
    buf.write(packet.filterText());
    buf.write(packet.visibleInTab());
  }

  @Override
  public @NotNull ServerboundClientSettingsPacket decode(@NotNull LimboByteBuf buf) throws DecoderException {
    return new ServerboundClientSettingsPacket(
        Objects.requireNonNullElse(Translator.parseLocale(buf.read(String.class, 16)), Locale.US),
        buf.read(byte.class),
        ChatVisibility.values()[buf.read(int.class)],
        buf.read(boolean.class),
        buf.readUnsignedByte(),
        EquipmentSlot.Hand.values()[buf.read(int.class)],
        buf.read(boolean.class),
        buf.read(boolean.class)
    );
  }
}
