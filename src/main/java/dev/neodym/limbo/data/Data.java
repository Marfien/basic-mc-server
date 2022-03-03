package dev.neodym.limbo.data;

import org.jetbrains.annotations.NotNull;

public interface Data {

  @NotNull DataHolder<? extends Data> holder();

}
