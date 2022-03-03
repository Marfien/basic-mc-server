package dev.neodym.limbo.data;

import org.jetbrains.annotations.NotNull;

public interface DataHolder<T extends Data> {

  @NotNull Data data();

}
