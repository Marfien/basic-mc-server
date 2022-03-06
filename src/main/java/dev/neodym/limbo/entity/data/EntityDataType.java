package dev.neodym.limbo.entity.data;

import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.util.Direction;
import dev.neodym.limbo.util.Position;
import dev.neodym.limbo.util.math.Vec3D;
import dev.neodym.limbo.util.VillagerData;
import dev.neodym.limbo.util.VillagerData.VillagerProfession;
import dev.neodym.limbo.util.VillagerData.VillagerType;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class EntityDataType<T> {

  public static final EntityDataType<Byte> BYTE = new EntityDataType<>(0, LimboByteBuf::write, buf -> buf.read(byte.class));
  public static final EntityDataType<Integer> INT = new EntityDataType<>(1, LimboByteBuf::write, buf -> buf.read(int.class));
  public static final EntityDataType<Float> FLOAT = new EntityDataType<>(2, LimboByteBuf::write, buf -> buf.read(float.class));
  public static final EntityDataType<String> STRING = new EntityDataType<>(3, LimboByteBuf::write, buf -> buf.read(String.class));
  public static final EntityDataType<Component> CHAT = new EntityDataType<>(4, (buf, chat) -> buf.write(chat, Component.class), buf -> buf.read(Component.class));
  public static final EntityDataType<Optional<Component>> OPTIONAL_CHAT = new EntityDataType<>(5, (buf, component) -> buf.writeOpt(component, Component.class), buf -> buf.readOpt(Component.class));
  // public static final EntityDataType<Object> SLOT; // TODO
  public static final EntityDataType<Boolean> BOOLEAN = new EntityDataType<>(7, LimboByteBuf::write, buf -> buf.read(boolean.class));
  public static final EntityDataType<Vec3D> ROTATION = new EntityDataType<>(8, (buf, rot) -> {
    buf.write((float) rot.x());
    buf.write((float) rot.y());
    buf.write((float) rot.z());
  }, buf -> new Vec3D(buf.read(float.class), buf.read(float.class), buf.read(float.class)));
  public static final EntityDataType<Position> POSITION = new EntityDataType<>(9, (buf, pos) -> {
    final int x = (int) pos.x();
    final int y = (int) pos.y();
    final int z = (int) pos.z();
    buf.write((((x & (long) 0x3ffffff) << 38) | ((y & (long) 0xfff)) | ((z & (long) 0x3ffffff) << 12)));
  }, buf -> {
    final long packedPos = buf.read(long.class);
    return new Position((int) (packedPos >> 38), (int) ((packedPos << 52) >> 52), (int) ((packedPos << 26) >> 38));
  });
  public static final EntityDataType<Optional<Position>> OPTIONAL_POSITION = new EntityDataType<>(10, (buf, pos) -> {
    buf.write(pos.isPresent());

    if (pos.isEmpty()) return;

    POSITION.writer.accept(buf, pos.get());
  }, buf -> {
    if (!buf.read(boolean.class)) return Optional.empty();

    return Optional.of(POSITION.reader.apply(buf));
  });
  public static final EntityDataType<Direction> DIRECTION = new EntityDataType<>(11, (buf, dir) -> buf.write(dir.ordinal()), buf -> Direction.values()[buf.read(int.class)]);
  public static final EntityDataType<Optional<UUID>> OPTIONAL_UUID = new EntityDataType<>(12, LimboByteBuf::writeOpt, buf -> buf.readOpt(UUID.class));
  // public static final EntityDataType<Optional<Component>> OPTIONAL_BLOCK_ID; // TODO
  public static final EntityDataType<CompoundBinaryTag> NBT = new EntityDataType<>(14, (buf, nbt) -> buf.write(nbt, CompoundBinaryTag.class), buf -> buf.read(CompoundBinaryTag.class));
  // public static final EntityDataType<Particle> PARTICLE;
  public static final EntityDataType<VillagerData> VILLAGER_DATA = new EntityDataType<>(16, (buf, data) -> {
    buf.write(data.type().id());
    buf.write(data.profession().id());
    buf.write(data.level());
  }, buf -> new VillagerData(
      VillagerType.values()[buf.read(int.class)],
      VillagerProfession.values()[buf.read(int.class)],
      buf.read(int.class)
  ));
  public static final EntityDataType<Optional<Integer>> OPTIONAL_INT = new EntityDataType<>(17, LimboByteBuf::writeOpt, buf -> buf.readOpt(int.class));
  // public static final EntityDataType<Pose> POSE = new EntityDataType<>(18, (buf, pose) -> buf.write(pose.id()), buf -> Pose.values()[buf.read(int.class)]);

  private final int typeId;

  private final BiConsumer<LimboByteBuf, T> writer;
  private final Function<LimboByteBuf, T> reader;

  public int typeId() {
    return this.typeId;
  }

  public void writeTo(final @NotNull LimboByteBuf buf, final @Nullable T value) {
    buf.write(this.typeId);
    this.writer.accept(buf, value);
  }

  public T read(final @NotNull LimboByteBuf buf) {
    return this.reader.apply(buf);
  }

}
