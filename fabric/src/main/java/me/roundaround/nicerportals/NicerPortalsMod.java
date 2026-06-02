package me.roundaround.nicerportals;

import me.roundaround.allay.api.Entrypoint;
import net.fabricmc.api.ModInitializer;

@Entrypoint(Entrypoint.MAIN)
public final class NicerPortalsMod implements ModInitializer {
  @Override
  public void onInitialize() {
    NicerPortals.init();
  }
}
