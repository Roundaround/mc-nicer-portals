package me.roundaround.nicerportals.client;

import me.roundaround.nicerportals.config.NicerPortalsConfig;

public final class NicerPortalsClient {
  private NicerPortalsClient() {}

  public static void initClient() {
    NicerPortalsConfig.getInstance().init();
  }
}
