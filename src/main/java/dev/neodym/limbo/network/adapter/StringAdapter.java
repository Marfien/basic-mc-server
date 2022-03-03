package dev.neodym.limbo.network.adapter;

import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.server.LimboServer;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.nio.charset.StandardCharsets;
import org.jetbrains.annotations.NotNull;

public class StringAdapter implements ByteBufAdapter<String> {

  @Override
  public void write(final @NotNull String s, final @NotNull LimboByteBuf buf, final @NotNull Object...args) {
    final int maxLength = args.length == 1 ? (int) args[0] : 32767;

    final byte[] bytes = s.getBytes(StandardCharsets.UTF_8);

    if (bytes.length > maxLength) throw new EncoderException("String too big (was " + bytes.length + " bytes encoded, max " + maxLength + ")");

    buf.writeVarInt(bytes.length);
    buf.writeBytes(bytes);
  }

  @Override
  public @NotNull String read(final @NotNull LimboByteBuf buf, final @NotNull Object...args) {
    final int maxLength = args.length == 1 ? (int) args[0] : 32767;

    int byteArrayLength = buf.read(int.class);

    if (byteArrayLength > maxLength * 4) throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + byteArrayLength + " > " + maxLength * 4 + ")");
    if (byteArrayLength < 0) throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");

    final String s = buf.toString(buf.readerIndex(), byteArrayLength, StandardCharsets.UTF_8);

    buf.readerIndex(buf.readerIndex() + byteArrayLength);

    if (s.length() > maxLength) throw new DecoderException("The received string length is longer than maximum allowed (" + byteArrayLength + " > " + maxLength + ")");

    return s;
  }
}
