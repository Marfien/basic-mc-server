package dev.neodym.limbo.util;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.key.Key;

@UtilityClass
public class PluginMessageChannel {

  public static final Key VELOCITY_INFO = Key.key("velocity", "player_info");
  public static final Key MINECRAFT_BRAND = Key.key(Key.MINECRAFT_NAMESPACE, "brand");

}
