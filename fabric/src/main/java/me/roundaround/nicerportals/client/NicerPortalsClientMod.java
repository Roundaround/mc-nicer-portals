package me.roundaround.nicerportals.client;

import me.roundaround.allay.api.Entrypoint;
import net.fabricmc.api.ClientModInitializer;

@Entrypoint(Entrypoint.CLIENT)
public class NicerPortalsClientMod implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    NicerPortalsClient.initClient();
  }
}
