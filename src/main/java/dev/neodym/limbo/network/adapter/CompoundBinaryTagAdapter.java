package dev.neodym.limbo.network.adapter;

import dev.neodym.limbo.network.LimboByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import org.jetbrains.annotations.NotNull;

public class CompoundBinaryTagAdapter implements ByteBufAdapter<CompoundBinaryTag> {

  @Override
  public void write(final @NotNull CompoundBinaryTag compoundTag, final @NotNull LimboByteBuf buf, final @NotNull Object... args) {
    try (final ByteBufOutputStream stream = new ByteBufOutputStream(buf)) {
      BinaryTagIO.writer().write(compoundTag, (OutputStream) stream);
    } catch (final IOException ignored) {
      throw new EncoderException("Cannot write NBT CompoundTag");
    }
  }

  @Override
  public @NotNull CompoundBinaryTag read(final @NotNull LimboByteBuf buf, final @NotNull Object... args) {
    try (ByteBufInputStream stream = new ByteBufInputStream(buf)) {
      return BinaryTagIO.reader().read((InputStream) stream);
    } catch (IOException thrown) {
      throw new DecoderException("Cannot read NBT CompoundTag");
    }
  }
}
