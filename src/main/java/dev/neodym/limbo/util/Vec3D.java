package dev.neodym.limbo.util;

import dev.neodym.javacommon.math.MathUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

@NoArgsConstructor
@AllArgsConstructor
public class Vec3D {

  private double x = 0.0D;
  private double y = 0.0D;
  private double z = 0.0D;

  public double x() {
    return this.x;
  }

  public double y() {
    return this.y;
  }

  public double z() {
    return this.z;
  }

  public @NotNull Vec3D x(final double x) {
    this.x = x;
    return this;
  }

  public @NotNull Vec3D y(final double y) {
    this.y = y;
    return this;
  }

  public @NotNull Vec3D z(final double z) {
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

  public @NotNull Vec3D subtract(final @NotNull Vec3D other) {
    this.x -= other.x;
    this.y -= other.y;
    this.z -= other.z;

    return this;
  }

  public @NotNull Vec3D add(final @NotNull Vec3D other) {
    this.x += other.x;
    this.y += other.y;
    this.z += other.z;

    return this;
  }

  public double distanceTo(final @NotNull Vec3D other) {
    return Math.sqrt(
        Math.abs(
            MathUtil.square(this.x - other.x)
                + MathUtil.square(this.y - other.y)
                + MathUtil.square(this.z - other.z)
        )
    );
  }

}
