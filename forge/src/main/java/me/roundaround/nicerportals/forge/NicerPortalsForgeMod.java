package me.roundaround.nicerportals.forge;

import me.roundaround.nicerportals.NicerPortals;
import me.roundaround.trove.forge.TroveForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod("nicerportals")
public final class NicerPortalsForgeMod {
  public NicerPortalsForgeMod(FMLJavaModLoadingContext context) {
    TroveForge.bootstrap(context);
    NicerPortals.init();

    // Client setup lives in NicerPortalsForgeClient (separate class, not inline) so the dedicated server never links its client classes.
    if (FMLEnvironment.dist.isClient()) {
      NicerPortalsForgeClient.init(context);
    }
  }
}
