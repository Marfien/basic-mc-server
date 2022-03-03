package dev.neodym.limbo.exception;

import org.jetbrains.annotations.NotNull;

public class AdapterNotFoundException extends RuntimeException {

  public AdapterNotFoundException(final @NotNull Class<?> clazz) {
    super("Not adapter found for %s.".formatted(clazz));
  }

}
