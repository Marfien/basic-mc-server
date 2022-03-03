package dev.neodym.limbo.network;

import com.google.common.collect.Maps;
import dev.neodym.limbo.network.adapter.BitSetAdapter;
import dev.neodym.limbo.network.adapter.BooleanAdapter;
import dev.neodym.limbo.network.adapter.ByteAdapter;
import dev.neodym.limbo.network.adapter.ByteBufAdapter;
import dev.neodym.limbo.network.adapter.ComponentAdapter;
import dev.neodym.limbo.network.adapter.CompoundBinaryTagAdapter;
import dev.neodym.limbo.network.adapter.DoubleAdapter;
import dev.neodym.limbo.network.adapter.FloatAdapter;
import dev.neodym.limbo.network.adapter.LongAdapter;
import dev.neodym.limbo.network.adapter.ShortAdapter;
import dev.neodym.limbo.network.adapter.StringAdapter;
import dev.neodym.limbo.network.adapter.UUIDAdapter;
import dev.neodym.limbo.network.adapter.VarIntAdapter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ByteProcessor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class LimboByteBuf extends ByteBuf {

  private final ByteBuf buf;
  private final Locale locale;

  public @NotNull Locale locale() {
    return locale;
  }

  public <T> void write(final @NotNull T t, final @NotNull Object...args) {
    this.write(t, (Class<T>) t.getClass(), args);
  }

  public <T> void write(final @NotNull T t, final @NotNull Class<T> clazz, final @NotNull Object...args) {
    final ByteBufAdapter<?> adapter = ADAPTERS.get(clazz);

    if (adapter == null) throw new EncoderException("Cannot find adapter for class %s.".formatted(clazz));

    ((ByteBufAdapter<T>) adapter).write(t, this, args);
  }

  public <T> void writeOpt(final @NotNull Optional<T> t, final @NotNull Object...args) {
    this.write(t.isPresent());

    if (t.isEmpty()) return;

    this.write(t.get(), args);
  }

  public <T> void writeOpt(final @NotNull Optional<T> t, final @NotNull Class<T> clazz, final @NotNull Object...args) {
    this.write(t.isPresent());

    if (t.isEmpty()) return;

    this.write(t.get(), clazz, args);
  }

  public <T> Optional<T> readOpt(final @NotNull Class<T> clazz, final @NotNull Object...args) {
    if (!this.read(boolean.class)) return Optional.empty();

    return Optional.of(this.read(clazz, args));
  }

  public <T> @NotNull T read(final @NotNull Class<T> clazz, final @NotNull Object...args) {
    final ByteBufAdapter<?> adapter = ADAPTERS.get(clazz);

    return (T) adapter.read(this, args);
  }

  public <T> T[] readArray(final @NotNull Class<T> clazz, final @NotNull IntFunction<T[]> creator, final @NotNull Object @NotNull... args) {
    final int len = this.read(Integer.class);

    final T[] array = creator.apply(len);

    for (int i = 0; i < len; i++) {
      array[i] = this.read(clazz, args);
    }

    return array;
  }

  public long[] readArray(final @NotNull Class<Long> clazz, final @NotNull Object @NotNull... args) {
    final int len = this.read(Integer.class);

    final long[] array = new long[len];

    for (int i = 0; i < len; i++) {
      array[i] = this.read(clazz, args);
    }

    return array;
  }

  public <T> void writeArray(final @NotNull T[] array, final @NotNull Object @NotNull... args) {
    this.write(array.length);

    for (final T t : array) {
      this.write(t, args);
    }
  }

  public void writeArray(final @NotNull long[] array, final @NotNull Object @NotNull... args) {
    this.write(array.length);

    for (final long t : array) {
      this.write(t, args);
    }
  }

  public void writeArray(final @NotNull byte[] array, final @NotNull Object @NotNull... args) {
    this.write(array.length);
    this.writeBytes(array);
  }

  public <T> void writeArray(final @NotNull T[] array, final @NotNull BiConsumer<T, LimboByteBuf> writer) {
    this.write(array.length);

    for (final T t : array) {
      writer.accept(t, this);
    }
  }

  public <T> T[] readArray(final @NotNull Class<T> clazz, final @NotNull IntFunction<T[]> creator, final @NotNull Function<LimboByteBuf, T> writer) {
    final int len = this.read(Integer.class);

    final T[] array = creator.apply(len);

    for (int i = 0; i < len; i++) {
      array[i] = writer.apply(this);
    }

    return array;
  }

  public synchronized <T> T readMarked(final @NotNull Class<T> clazz, final @NotNull Object...args) {
    this.markReaderIndex();

    final T result = this.read(clazz, args);

    this.resetReaderIndex();

    return result;
  }

  public @NotNull <T> LimboByteBuf readEmpty(final @NotNull Class<T> clazz, final @NotNull Object...args) {
    this.read(clazz, args);

    return this;
  }

  public byte[] toByteArray() {
    byte[] bytes = new byte[this.buf.readableBytes()];
    this.buf.readBytes(bytes);
    return bytes;
  }

  public boolean isEmpty() {
    return this.readableBytes() <= 0;
  }

  public static int varIntSize(final int value) {
    for (int i = 1; i < 5; ++i) {
      if ((value & -1 << i * 7) != 0) continue;

      return i;
    }

    return 5;
  }

  //////////////////////////////////////////////////
  public int readVarInt() {
    int i = 0;
    int maxRead = Math.min(5, buf.readableBytes());

    for (int j = 0; j < maxRead; j++) {
      int k = buf.readByte();
      i |= (k & 0x7F) << j * 7;
      if ((k & 0x80) != 128) {
        return i;
      }
    }

    buf.readBytes(maxRead);

    throw new IllegalArgumentException("Cannot read VarInt");
  }

  public void writeVarInt(int value) {
    while (true) {
      if ((value & 0xFFFFFF80) == 0) {
        buf.writeByte(value);
        return;
      }

      buf.writeByte(value & 0x7F | 0x80);
      value >>>= 7;
    }
  }

  public String readString() {
    return readString(readVarInt());
  }

  public String readString(int length) {
    String str = buf.toString(buf.readerIndex(), length, StandardCharsets.UTF_8);
    buf.skipBytes(length);
    return str;
  }

  public void writeString(CharSequence str) {
    int size = ByteBufUtil.utf8Bytes(str);
    writeVarInt(size);
    buf.writeCharSequence(str, StandardCharsets.UTF_8);
  }

  public byte[] readBytesArray() {
    int length = readVarInt();
    byte[] array = new byte[length];
    buf.readBytes(array);
    return array;
  }

  public void writeBytesArray(byte[] array) {
    writeVarInt(array.length);
    buf.writeBytes(array);
  }

  public int[] readIntArray() {
    int len = readVarInt();
    int[] array = new int[len];
    for (int i = 0; i < len; i++) {
      array[i] = readVarInt();
    }
    return array;
  }

  public UUID readUuid() {
    long msb = buf.readLong();
    long lsb = buf.readLong();
    return new UUID(msb, lsb);
  }

  public void writeUuid(UUID uuid) {
    buf.writeLong(uuid.getMostSignificantBits());
    buf.writeLong(uuid.getLeastSignificantBits());
  }

  public String[] readStringsArray() {
    int length = readVarInt();
    String[] ret = new String[length];
    for (int i = 0; i < length; i++) {
      ret[i] = readString();
    }
    return ret;
  }

  public void writeStringsArray(String[] stringArray) {
    writeVarInt(stringArray.length);
    for (String str : stringArray) {
      writeString(str);
    }
  }

  public void writeVarIntArray(int[] array) {
    writeVarInt(array.length);
    for (int i : array) {
      writeVarInt(i);
    }
  }

  public void writeLongArray(long[] array) {
    writeVarInt(array.length);
    for (long i : array) {
      writeLong(i);
    }
  }

  public void writeCompoundTagArray(CompoundBinaryTag[] compoundTags) {
    try (ByteBufOutputStream stream = new ByteBufOutputStream(buf)) {
      writeVarInt(compoundTags.length);

      for (CompoundBinaryTag tag : compoundTags) {
        BinaryTagIO.writer().write(tag, (OutputStream) stream);
      }
    } catch (IOException e) {
      throw new EncoderException("Cannot write NBT CompoundTag");
    }
  }

  public CompoundBinaryTag readCompoundTag() {
    try (ByteBufInputStream stream = new ByteBufInputStream(buf)) {
      return BinaryTagIO.reader().read((InputStream) stream);
    } catch (IOException thrown) {
      throw new DecoderException("Cannot read NBT CompoundTag");
    }
  }

  public void writeCompoundTag(CompoundBinaryTag compoundTag) {
    try (ByteBufOutputStream stream = new ByteBufOutputStream(buf)) {
      BinaryTagIO.writer().write(compoundTag, (OutputStream) stream);
    } catch (IOException e) {
      throw new EncoderException("Cannot write NBT CompoundTag");
    }
  }

  // <editor-folder desc="Delegated methods" defaultstate="collapsed">

  @Override
  public int capacity() {
    return this.buf.capacity();
  }

  @Override
  public ByteBuf capacity(int newCapacity) {
    return this.buf.capacity(newCapacity);
  }

  @Override
  public int maxCapacity() {
    return this.buf.maxCapacity();
  }

  @Override
  public ByteBufAllocator alloc() {
    return this.buf.alloc();
  }

  @Override
  @Deprecated
  public ByteOrder order() {
    return this.buf.order();
  }

  @Override
  @Deprecated
  public ByteBuf order(ByteOrder endianness) {
    return this.buf.order(endianness);
  }

  @Override
  public ByteBuf unwrap() {
    return this.buf.unwrap();
  }

  @Override
  public boolean isDirect() {
    return this.buf.isDirect();
  }

  @Override
  public boolean isReadOnly() {
    return this.buf.isReadOnly();
  }

  @Override
  public ByteBuf asReadOnly() {
    return this.buf.asReadOnly();
  }

  @Override
  public int readerIndex() {
    return this.buf.readerIndex();
  }

  @Override
  public ByteBuf readerIndex(int readerIndex) {
    return this.buf.readerIndex(readerIndex);
  }

  @Override
  public int writerIndex() {
    return this.buf.writerIndex();
  }

  @Override
  public ByteBuf writerIndex(int writerIndex) {
    return this.buf.writerIndex(writerIndex);
  }

  @Override
  public ByteBuf setIndex(int readerIndex, int writerIndex) {
    return this.buf.setIndex(readerIndex, writerIndex);
  }

  @Override
  public int readableBytes() {
    return this.buf.readableBytes();
  }

  @Override
  public int writableBytes() {
    return this.buf.writableBytes();
  }

  @Override
  public int maxWritableBytes() {
    return this.buf.maxWritableBytes();
  }

  @Override
  public int maxFastWritableBytes() {
    return this.buf.maxFastWritableBytes();
  }

  @Override
  public boolean isReadable() {
    return this.buf.isReadable();
  }

  @Override
  public boolean isReadable(int size) {
    return this.buf.isReadable(size);
  }

  @Override
  public boolean isWritable() {
    return this.buf.isWritable();
  }

  @Override
  public boolean isWritable(int size) {
    return this.buf.isWritable(size);
  }

  @Override
  public ByteBuf clear() {
    return this.buf.clear();
  }

  @Override
  public ByteBuf markReaderIndex() {
    return this.buf.markReaderIndex();
  }

  @Override
  public ByteBuf resetReaderIndex() {
    return this.buf.resetReaderIndex();
  }

  @Override
  public ByteBuf markWriterIndex() {
    return this.buf.markWriterIndex();
  }

  @Override
  public ByteBuf resetWriterIndex() {
    return this.buf.resetWriterIndex();
  }

  @Override
  public ByteBuf discardReadBytes() {
    return this.buf.discardReadBytes();
  }

  @Override
  public ByteBuf discardSomeReadBytes() {
    return this.buf.discardSomeReadBytes();
  }

  @Override
  public ByteBuf ensureWritable(int minWritableBytes) {
    return this.buf.ensureWritable(minWritableBytes);
  }

  @Override
  public int ensureWritable(int minWritableBytes, boolean force) {
    return this.buf.ensureWritable(minWritableBytes, force);
  }

  @Override
  public boolean getBoolean(int index) {
    return this.buf.getBoolean(index);
  }

  @Override
  public byte getByte(int index) {
    return this.buf.getByte(index);
  }

  @Override
  public short getUnsignedByte(int index) {
    return this.buf.getUnsignedByte(index);
  }

  @Override
  public short getShort(int index) {
    return this.buf.getShort(index);
  }

  @Override
  public short getShortLE(int index) {
    return this.buf.getShortLE(index);
  }

  @Override
  public int getUnsignedShort(int index) {
    return this.buf.getUnsignedShort(index);
  }

  @Override
  public int getUnsignedShortLE(int index) {
    return this.buf.getUnsignedShortLE(index);
  }

  @Override
  public int getMedium(int index) {
    return this.buf.getMedium(index);
  }

  @Override
  public int getMediumLE(int index) {
    return this.buf.getMediumLE(index);
  }

  @Override
  public int getUnsignedMedium(int index) {
    return this.buf.getUnsignedMedium(index);
  }

  @Override
  public int getUnsignedMediumLE(int index) {
    return this.buf.getUnsignedMediumLE(index);
  }

  @Override
  public int getInt(int index) {
    return this.buf.getInt(index);
  }

  @Override
  public int getIntLE(int index) {
    return this.buf.getIntLE(index);
  }

  @Override
  public long getUnsignedInt(int index) {
    return this.buf.getUnsignedInt(index);
  }

  @Override
  public long getUnsignedIntLE(int index) {
    return this.buf.getUnsignedIntLE(index);
  }

  @Override
  public long getLong(int index) {
    return this.buf.getLong(index);
  }

  @Override
  public long getLongLE(int index) {
    return this.buf.getLongLE(index);
  }

  @Override
  public char getChar(int index) {
    return this.buf.getChar(index);
  }

  @Override
  public float getFloat(int index) {
    return this.buf.getFloat(index);
  }

  @Override
  public float getFloatLE(int index) {
    return this.buf.getFloatLE(index);
  }

  @Override
  public double getDouble(int index) {
    return this.buf.getDouble(index);
  }

  @Override
  public double getDoubleLE(int index) {
    return this.buf.getDoubleLE(index);
  }

  @Override
  public ByteBuf getBytes(int index, ByteBuf dst) {
    return this.buf.getBytes(index, dst);
  }

  @Override
  public ByteBuf getBytes(int index, ByteBuf dst, int length) {
    return this.buf.getBytes(index, dst, length);
  }

  @Override
  public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
    return this.buf.getBytes(index, dst, dstIndex, length);
  }

  @Override
  public ByteBuf getBytes(int index, byte[] dst) {
    return this.buf.getBytes(index, dst);
  }

  @Override
  public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
    return this.buf.getBytes(index, dst, dstIndex, length);
  }

  @Override
  public ByteBuf getBytes(int index, ByteBuffer dst) {
    return this.buf.getBytes(index, dst);
  }

  @Override
  public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
    return this.buf.getBytes(index, out, length);
  }

  @Override
  public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
    return this.buf.getBytes(index, out, length);
  }

  @Override
  public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
    return this.buf.getBytes(index, out, position, length);
  }

  @Override
  public CharSequence getCharSequence(int index, int length, Charset charset) {
    return this.buf.getCharSequence(index, length, charset);
  }

  @Override
  public ByteBuf setBoolean(int index, boolean value) {
    return this.buf.setBoolean(index, value);
  }

  @Override
  public ByteBuf setByte(int index, int value) {
    return this.buf.setByte(index, value);
  }

  @Override
  public ByteBuf setShort(int index, int value) {
    return this.buf.setShort(index, value);
  }

  @Override
  public ByteBuf setShortLE(int index, int value) {
    return this.buf.setShortLE(index, value);
  }

  @Override
  public ByteBuf setMedium(int index, int value) {
    return this.buf.setMedium(index, value);
  }

  @Override
  public ByteBuf setMediumLE(int index, int value) {
    return this.buf.setMediumLE(index, value);
  }

  @Override
  public ByteBuf setInt(int index, int value) {
    return this.buf.setInt(index, value);
  }

  @Override
  public ByteBuf setIntLE(int index, int value) {
    return this.buf.setIntLE(index, value);
  }

  @Override
  public ByteBuf setLong(int index, long value) {
    return this.buf.setLong(index, value);
  }

  @Override
  public ByteBuf setLongLE(int index, long value) {
    return this.buf.setLongLE(index, value);
  }

  @Override
  public ByteBuf setChar(int index, int value) {
    return this.buf.setChar(index, value);
  }

  @Override
  public ByteBuf setFloat(int index, float value) {
    return this.buf.setFloat(index, value);
  }

  @Override
  public ByteBuf setFloatLE(int index, float value) {
    return this.buf.setFloatLE(index, value);
  }

  @Override
  public ByteBuf setDouble(int index, double value) {
    return this.buf.setDouble(index, value);
  }

  @Override
  public ByteBuf setDoubleLE(int index, double value) {
    return this.buf.setDoubleLE(index, value);
  }

  @Override
  public ByteBuf setBytes(int index, ByteBuf src) {
    return this.buf.setBytes(index, src);
  }

  @Override
  public ByteBuf setBytes(int index, ByteBuf src, int length) {
    return this.buf.setBytes(index, src, length);
  }

  @Override
  public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
    return this.buf.setBytes(index, src, srcIndex, length);
  }

  @Override
  public ByteBuf setBytes(int index, byte[] src) {
    return this.buf.setBytes(index, src);
  }

  @Override
  public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
    return this.buf.setBytes(index, src, srcIndex, length);
  }

  @Override
  public ByteBuf setBytes(int index, ByteBuffer src) {
    return this.buf.setBytes(index, src);
  }

  @Override
  public int setBytes(int index, InputStream in, int length) throws IOException {
    return this.buf.setBytes(index, in, length);
  }

  @Override
  public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
    return this.buf.setBytes(index, in, length);
  }

  @Override
  public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
    return this.buf.setBytes(index, in, position, length);
  }

  @Override
  public ByteBuf setZero(int index, int length) {
    return this.buf.setZero(index, length);
  }

  @Override
  public int setCharSequence(int index, CharSequence sequence, Charset charset) {
    return this.buf.setCharSequence(index, sequence, charset);
  }

  @Override
  public boolean readBoolean() {
    return this.buf.readBoolean();
  }

  @Override
  public byte readByte() {
    return this.buf.readByte();
  }

  @Override
  public short readUnsignedByte() {
    return this.buf.readUnsignedByte();
  }

  @Override
  public short readShort() {
    return this.buf.readShort();
  }

  @Override
  public short readShortLE() {
    return this.buf.readShortLE();
  }

  @Override
  public int readUnsignedShort() {
    return this.buf.readUnsignedShort();
  }

  @Override
  public int readUnsignedShortLE() {
    return this.buf.readUnsignedShortLE();
  }

  @Override
  public int readMedium() {
    return this.buf.readMedium();
  }

  @Override
  public int readMediumLE() {
    return this.buf.readMediumLE();
  }

  @Override
  public int readUnsignedMedium() {
    return this.buf.readUnsignedMedium();
  }

  @Override
  public int readUnsignedMediumLE() {
    return this.buf.readUnsignedMediumLE();
  }

  @Override
  public int readInt() {
    return this.buf.readInt();
  }

  @Override
  public int readIntLE() {
    return this.buf.readIntLE();
  }

  @Override
  public long readUnsignedInt() {
    return this.buf.readUnsignedInt();
  }

  @Override
  public long readUnsignedIntLE() {
    return this.buf.readUnsignedIntLE();
  }

  @Override
  public long readLong() {
    return this.buf.readLong();
  }

  @Override
  public long readLongLE() {
    return this.buf.readLongLE();
  }

  @Override
  public char readChar() {
    return this.buf.readChar();
  }

  @Override
  public float readFloat() {
    return this.buf.readFloat();
  }

  @Override
  public float readFloatLE() {
    return this.buf.readFloatLE();
  }

  @Override
  public double readDouble() {
    return this.buf.readDouble();
  }

  @Override
  public double readDoubleLE() {
    return this.buf.readDoubleLE();
  }

  @Override
  public ByteBuf readBytes(int length) {
    return this.buf.readBytes(length);
  }

  @Override
  public ByteBuf readSlice(int length) {
    return this.buf.readSlice(length);
  }

  @Override
  public ByteBuf readRetainedSlice(int length) {
    return this.buf.readRetainedSlice(length);
  }

  @Override
  public ByteBuf readBytes(ByteBuf dst) {
    return this.buf.readBytes(dst);
  }

  @Override
  public ByteBuf readBytes(ByteBuf dst, int length) {
    return this.buf.readBytes(dst, length);
  }

  @Override
  public ByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
    return this.buf.readBytes(dst, dstIndex, length);
  }

  @Override
  public ByteBuf readBytes(byte[] dst) {
    return this.buf.readBytes(dst);
  }

  @Override
  public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
    return this.buf.readBytes(dst, dstIndex, length);
  }

  @Override
  public ByteBuf readBytes(ByteBuffer dst) {
    return this.buf.readBytes(dst);
  }

  @Override
  public ByteBuf readBytes(OutputStream out, int length) throws IOException {
    return this.buf.readBytes(out, length);
  }

  @Override
  public int readBytes(GatheringByteChannel out, int length) throws IOException {
    return this.buf.readBytes(out, length);
  }

  @Override
  public CharSequence readCharSequence(int length, Charset charset) {
    return this.buf.readCharSequence(length, charset);
  }

  @Override
  public int readBytes(FileChannel out, long position, int length) throws IOException {
    return this.buf.readBytes(out, position, length);
  }

  @Override
  public ByteBuf skipBytes(int length) {
    return this.buf.skipBytes(length);
  }

  @Override
  public ByteBuf writeBoolean(boolean value) {
    return this.buf.writeBoolean(value);
  }

  @Override
  public ByteBuf writeByte(int value) {
    return this.buf.writeByte(value);
  }

  @Override
  public ByteBuf writeShort(int value) {
    return this.buf.writeShort(value);
  }

  @Override
  public ByteBuf writeShortLE(int value) {
    return this.buf.writeShortLE(value);
  }

  @Override
  public ByteBuf writeMedium(int value) {
    return this.buf.writeMedium(value);
  }

  @Override
  public ByteBuf writeMediumLE(int value) {
    return this.buf.writeMediumLE(value);
  }

  @Override
  public ByteBuf writeInt(int value) {
    return this.buf.writeInt(value);
  }

  @Override
  public ByteBuf writeIntLE(int value) {
    return this.buf.writeIntLE(value);
  }

  @Override
  public ByteBuf writeLong(long value) {
    return this.buf.writeLong(value);
  }

  @Override
  public ByteBuf writeLongLE(long value) {
    return this.buf.writeLongLE(value);
  }

  @Override
  public ByteBuf writeChar(int value) {
    return this.buf.writeChar(value);
  }

  @Override
  public ByteBuf writeFloat(float value) {
    return this.buf.writeFloat(value);
  }

  @Override
  public ByteBuf writeFloatLE(float value) {
    return this.buf.writeFloatLE(value);
  }

  @Override
  public ByteBuf writeDouble(double value) {
    return this.buf.writeDouble(value);
  }

  @Override
  public ByteBuf writeDoubleLE(double value) {
    return this.buf.writeDoubleLE(value);
  }

  @Override
  public ByteBuf writeBytes(ByteBuf src) {
    return this.buf.writeBytes(src);
  }

  @Override
  public ByteBuf writeBytes(ByteBuf src, int length) {
    return this.buf.writeBytes(src, length);
  }

  @Override
  public ByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
    return this.buf.writeBytes(src, srcIndex, length);
  }

  @Override
  public ByteBuf writeBytes(byte[] src) {
    return this.buf.writeBytes(src);
  }

  @Override
  public ByteBuf writeBytes(byte[] src, int srcIndex, int length) {
    return this.buf.writeBytes(src, srcIndex, length);
  }

  @Override
  public ByteBuf writeBytes(ByteBuffer src) {
    return this.buf.writeBytes(src);
  }

  @Override
  public int writeBytes(InputStream in, int length) throws IOException {
    return this.buf.writeBytes(in, length);
  }

  @Override
  public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
    return this.buf.writeBytes(in, length);
  }

  @Override
  public int writeBytes(FileChannel in, long position, int length) throws IOException {
    return this.buf.writeBytes(in, position, length);
  }

  @Override
  public ByteBuf writeZero(int length) {
    return this.buf.writeZero(length);
  }

  @Override
  public int writeCharSequence(CharSequence sequence, Charset charset) {
    return this.buf.writeCharSequence(sequence, charset);
  }

  @Override
  public int indexOf(int fromIndex, int toIndex, byte value) {
    return this.buf.indexOf(fromIndex, toIndex, value);
  }

  @Override
  public int bytesBefore(byte value) {
    return this.buf.bytesBefore(value);
  }

  @Override
  public int bytesBefore(int length, byte value) {
    return this.buf.bytesBefore(length, value);
  }

  @Override
  public int bytesBefore(int index, int length, byte value) {
    return this.buf.bytesBefore(index, length, value);
  }

  @Override
  public int forEachByte(ByteProcessor processor) {
    return this.buf.forEachByte(processor);
  }

  @Override
  public int forEachByte(int index, int length, ByteProcessor processor) {
    return this.buf.forEachByte(index, length, processor);
  }

  @Override
  public int forEachByteDesc(ByteProcessor processor) {
    return this.buf.forEachByteDesc(processor);
  }

  @Override
  public int forEachByteDesc(int index, int length, ByteProcessor processor) {
    return this.buf.forEachByteDesc(index, length, processor);
  }

  @Override
  public ByteBuf copy() {
    return this.buf.copy();
  }

  @Override
  public ByteBuf copy(int index, int length) {
    return this.buf.copy(index, length);
  }

  @Override
  public ByteBuf slice() {
    return this.buf.slice();
  }

  @Override
  public ByteBuf retainedSlice() {
    return this.buf.retainedSlice();
  }

  @Override
  public ByteBuf slice(int index, int length) {
    return this.buf.slice(index, length);
  }

  @Override
  public ByteBuf retainedSlice(int index, int length) {
    return this.buf.retainedSlice(index, length);
  }

  @Override
  public ByteBuf duplicate() {
    return this.buf.duplicate();
  }

  @Override
  public ByteBuf retainedDuplicate() {
    return this.buf.retainedDuplicate();
  }

  @Override
  public int nioBufferCount() {
    return this.buf.nioBufferCount();
  }

  @Override
  public ByteBuffer nioBuffer() {
    return this.buf.nioBuffer();
  }

  @Override
  public ByteBuffer nioBuffer(int index, int length) {
    return this.buf.nioBuffer(index, length);
  }

  @Override
  public ByteBuffer internalNioBuffer(int index, int length) {
    return this.buf.internalNioBuffer(index, length);
  }

  @Override
  public ByteBuffer[] nioBuffers() {
    return this.buf.nioBuffers();
  }

  @Override
  public ByteBuffer[] nioBuffers(int index, int length) {
    return this.buf.nioBuffers(index, length);
  }

  @Override
  public boolean hasArray() {
    return this.buf.hasArray();
  }

  @Override
  public byte[] array() {
    return this.buf.array();
  }

  @Override
  public int arrayOffset() {
    return this.buf.arrayOffset();
  }

  @Override
  public boolean hasMemoryAddress() {
    return this.buf.hasMemoryAddress();
  }

  @Override
  public long memoryAddress() {
    return this.buf.memoryAddress();
  }

  @Override
  public boolean isContiguous() {
    return this.buf.isContiguous();
  }

  @Override
  public String toString(Charset charset) {
    return this.buf.toString(charset);
  }

  @Override
  public String toString(int index, int length, Charset charset) {
    return this.buf.toString(index, length, charset);
  }

  @Override
  public int hashCode() {
    return this.buf.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return this.buf.equals(obj);
  }

  @Override
  public int compareTo(ByteBuf buffer) {
    return this.buf.compareTo(buffer);
  }

  @Override
  public String toString() {
    return this.buf.toString();
  }

  @Override
  public ByteBuf retain(int increment) {
    return this.buf.retain(increment);
  }

  @Override
  public ByteBuf retain() {
    return this.buf.retain();
  }

  @Override
  public ByteBuf touch() {
    return this.buf.touch();
  }

  @Override
  public ByteBuf touch(Object hint) {
    return this.buf.touch(hint);
  }

  @Override
  public int refCnt() {
    return this.buf.refCnt();
  }

  @Override
  public boolean release() {
    return this.buf.release();
  }

  @Override
  public boolean release(int decrement) {
    return this.buf.release(decrement);
  }

  // </editor-folder>

  private static final Map<Class<?>, ByteBufAdapter<?>> ADAPTERS = Maps.newConcurrentMap();

  public static <T> void register(final @NotNull Class<T> clazz, final @NotNull ByteBufAdapter<T> adapter) {
    ADAPTERS.put(clazz, adapter);
  }

  public static @NotNull LimboByteBuf create() {
    return create(Unpooled.buffer());
  }

  public static @NotNull LimboByteBuf create(final @NotNull Locale locale) {
    return create(Unpooled.buffer(), locale);
  }

  public static @NotNull LimboByteBuf create(final @NotNull ByteBuf parent) {
    return create(parent, Locale.US);
  }

  public static @NotNull LimboByteBuf create(final @NotNull ByteBuf parent, final @NotNull Locale locale) {
    return new LimboByteBuf(parent, locale);
  }


  static {
    register(Boolean.class, new BooleanAdapter());
    register(boolean.class, new BooleanAdapter());
    register(Byte.class, new ByteAdapter());
    register(byte.class, new ByteAdapter());
    register(CompoundBinaryTag.class, new CompoundBinaryTagAdapter());
    register(Double.class, new DoubleAdapter());
    register(double.class, new DoubleAdapter());
    register(Float.class, new FloatAdapter());
    register(float.class, new FloatAdapter());
    register(Integer.class, new VarIntAdapter());
    register(int.class, new VarIntAdapter());
    register(Long.class, new LongAdapter());
    register(long.class, new LongAdapter());
    register(Short.class, new ShortAdapter());
    register(short.class, new ShortAdapter());
    register(String.class, new StringAdapter());
    register(UUID.class, new UUIDAdapter());
    register(Component.class, new ComponentAdapter());
    register(BitSet.class, new BitSetAdapter());
  }
}
