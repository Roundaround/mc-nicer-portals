package me.roundaround.nicerportals;

import me.roundaround.nicerportals.config.NicerPortalsPerWorldConfig;

public final class NicerPortals {
  private NicerPortals() {}

  public static void init() {
    NicerPortalsPerWorldConfig.getInstance().init();
  }
}
