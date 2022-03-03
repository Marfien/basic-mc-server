package dev.neodym.limbo.entity.data;

import dev.neodym.limbo.network.LimboByteBuf;
import dev.neodym.limbo.util.Position;
import java.util.Optional;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public record EntityDataAccessor<T>(
    byte fieldId,
    @NotNull EntityDataType<T> type
) {

  private EntityDataAccessor(final int fieldId, final @NotNull EntityDataType<T> type) {
    this((byte) fieldId, type);
  }

  public static final EntityDataAccessor<Byte> END = new EntityDataAccessor<>( 0xFF, EntityDataType.BYTE);

  public static final EntityDataAccessor<Byte> STATE = new EntityDataAccessor<>(0, EntityDataType.BYTE);
  public static final EntityDataAccessor<Integer> AIR_TICKS = new EntityDataAccessor<>(1, EntityDataType.INT);
  public static final EntityDataAccessor<Optional<Component>> CUSTOM_NAME = new EntityDataAccessor<>(2, EntityDataType.OPTIONAL_CHAT);
  public static final EntityDataAccessor<Boolean> CUSTOM_NAME_VISIBLE = new EntityDataAccessor<>(3, EntityDataType.BOOLEAN);
  public static final EntityDataAccessor<Boolean> IS_SILENT = new EntityDataAccessor<>(4, EntityDataType.BOOLEAN);
  public static final EntityDataAccessor<Boolean> GRAVITY = new EntityDataAccessor<>(5, EntityDataType.BOOLEAN);
  // public static final EntityDataAccessor<Pose> POSE = new EntityDataAccessor<>(6, EntityDataType.POSE);
  public static final EntityDataAccessor<Integer> FREEZ_TICKS = new EntityDataAccessor<>(7, EntityDataType.INT);


  // <editor-folder desc="Throwablbes" defaultstate="collapsed"
  // TODO thrown egg
  // TODO thrown ender pearl
  // TODO thrown experience bottle
  // TODO thrown potion
  // TODO thrown snowball
  // TODO thrown eye of ender
  public static final EntityDataAccessor<Position> FALLING_BLOCK_SPAWN_POSITION = new EntityDataAccessor<>(8, EntityDataType.POSITION);
  // </editor-folder>

  // <editor-folder desc="Area effect cloud" defaultstate="collapsed">
  public static final EntityDataAccessor<Float> AREA_EFFECT_CLOUD_RADIUS = new EntityDataAccessor<>(8, EntityDataType.FLOAT);
  public static final EntityDataAccessor<Integer> AREA_EFFECT_CLOUD_COLOR = new EntityDataAccessor<>(9, EntityDataType.INT);
  public static final EntityDataAccessor<Boolean> AREA_EFFECT_CLOUD_SIGNLE_PARTICLE = new EntityDataAccessor<>(10, EntityDataType.BOOLEAN);
  // public static final EntityDataAccessor<Particle> AREA_EFFECT_CLOUD_PRATICLE = new EntityDataAccessor<>(11, EntityDataType.PARTICLE); //TODO
  // </editor-folder>

  // TODO implement fishing hook to item entity

  // <editor-folder desc="Living Entity" defaultstate="collapsed">
  public static final EntityDataAccessor<Byte> LIVING_HAND_STATE = new EntityDataAccessor<>(8, EntityDataType.BYTE);
  public static final EntityDataAccessor<Float> LIVING_HEALTH = new EntityDataAccessor<>(9, EntityDataType.FLOAT);
  public static final EntityDataAccessor<Integer> LIVING_POTION_EFFECT_COLOR = new EntityDataAccessor<>(10, EntityDataType.INT);
  public static final EntityDataAccessor<Boolean> LIVING_EFFECT_AMBIENT = new EntityDataAccessor<>(11, EntityDataType.BOOLEAN);
  public static final EntityDataAccessor<Integer> LIVING_AMOUNT_OF_ARROWS = new EntityDataAccessor<>(12, EntityDataType.INT);
  public static final EntityDataAccessor<Integer> LIVING_AMOUNT_OF_STINGERS = new EntityDataAccessor<>(13, EntityDataType.INT);
  public static final EntityDataAccessor<Optional<Position>> LIVING_BED_BLOCK = new EntityDataAccessor<>(14, EntityDataType.OPTIONAL_POSITION);
  // </editor-folder>

  // <editor-folder desc="Player" defaultstate="collapsed">
  public static final EntityDataAccessor<Float> PLAYER_ADDITIONAL_HEARTS = new EntityDataAccessor<>(15, EntityDataType.FLOAT);
  public static final EntityDataAccessor<Integer> PLAYER_SCORE = new EntityDataAccessor<>(16, EntityDataType.INT);
  public static final EntityDataAccessor<Byte> PLAYER_SKIN_PARTS = new EntityDataAccessor<>(17, EntityDataType.BYTE);
  public static final EntityDataAccessor<Byte> PLAYER_MAIN_HAND = new EntityDataAccessor<>(18, EntityDataType.BYTE);
  public static final EntityDataAccessor<CompoundBinaryTag> PLAYER_LEFT_SHOULDER_ENTITY_DATA = new EntityDataAccessor<>(19, EntityDataType.NBT);
  public static final EntityDataAccessor<CompoundBinaryTag> PLAYER_RIGHT_SHOULDER_ENTITY_DATA = new EntityDataAccessor<>(20, EntityDataType.NBT);
  // </editor-folder>

  // TODO implement rest

  public void writeTo(final @NotNull LimboByteBuf buf, final @NotNull T value) {
    buf.writeByte(this.fieldId);
    this.type.writeTo(buf, value);
  }

  public @NotNull T read(final @NotNull LimboByteBuf buf) {
    return this.type.read(buf);
  }

}
