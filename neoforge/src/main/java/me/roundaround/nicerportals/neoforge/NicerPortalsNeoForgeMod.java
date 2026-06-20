package me.roundaround.nicerportals.neoforge;

import me.roundaround.nicerportals.NicerPortals;
import me.roundaround.trove.neoforge.TroveNeoForge;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod("nicerportals")
public final class NicerPortalsNeoForgeMod {
  public NicerPortalsNeoForgeMod(IEventBus modBus, ModContainer container) {
    TroveNeoForge.bootstrap(modBus, container);
    NicerPortals.init();

    // Client setup lives in NicerPortalsNeoForgeClient (separate class, not inline) so the dedicated server never links its client classes.
    if (FMLEnvironment.getDist() == Dist.CLIENT) {
      NicerPortalsNeoForgeClient.init(modBus, container);
    }
  }
}
