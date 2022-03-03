package dev.neodym.limbo.util.palette;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.BinaryTagLike;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class CodecGlobalPalette<T extends KeyedBinaryTagLike> extends KeyedGlobalPalette<T> implements BinaryTagLike {

  private final @NotNull Key type;

  public @NotNull Key type() {
    return this.type;
  }

  @Override
  public @NotNull BinaryTag asBinaryTag() {
    return CompoundBinaryTag.builder()
        .putString("type", this.type.asString())
        .put(
            "value",
            ListBinaryTag.from(
                super.values()
                    .stream()
                    .map(BinaryTagLike::asBinaryTag)
                    .collect(Collectors.toList())))
        .build();
  }

  public @NotNull CompoundBinaryTag insert(final @NotNull CompoundBinaryTag parent) {
    return parent.put(this.type.asString(), this.asBinaryTag());
  }

}
