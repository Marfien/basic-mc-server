package dev.neodym.limbo.util;

import dev.neodym.javacommon.math.MathUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

@NoArgsConstructor
@AllArgsConstructor
public class Vec3I {

  private int x = 0;
  private int y = 0;
  private int z = 0;

  public int x() {
    return this.x;
  }

  public int y() {
    return this.y;
  }

  public int z() {
    return this.z;
  }

  public @NotNull Vec3I x(final int x) {
    this.x = x;
    return this;
  }

  public @NotNull Vec3I y(final int y) {
    this.y = y;
    return this;
  }

  public @NotNull Vec3I z(final int z) {
    this.z = z;
    return this;
  }

  public @Range(from = 0, to = Long.MAX_VALUE) double length() {
    return Math.sqrt(
        Math.abs(
            MathUtil.square(this.x)
                + MathUtil.square(this.y)
                + MathUtil.square(this.z)
        )
    );
  }

  public @NotNull Vec3I subtract(final @NotNull Vec3I other) {
    this.x -= other.x;
    this.y -= other.y;
    this.z -= other.z;

    return this;
  }

  public @NotNull Vec3I add(final @NotNull Vec3I other) {
    this.x += other.x;
    this.y += other.y;
    this.z += other.z;

    return this;
  }

  public double distanceTo(final @NotNull Vec3I other) {
    return Math.sqrt(
        Math.abs(
            MathUtil.square(this.x - other.x)
                + MathUtil.square(this.y - other.y)
                + MathUtil.square(this.z - other.z)
        )
    );
  }

  public @NotNull Vec3D asDouble() {
    return new Vec3D(this.x, this.y, this.z);
  }

  public long asLong() {
    return (((long) this.x & (long) 67108863) << 38) | ((long) this.y & (long) 4095) | (((long) this.z & (long) 67108863) << 12);
  }

  public static @NotNull Vec3I fromLong(final long l) {
    return new Vec3I((int) (l >> 38), (int) ((l << 52) >> 52), (int) ((l << 26) >> 38));
  }
}
