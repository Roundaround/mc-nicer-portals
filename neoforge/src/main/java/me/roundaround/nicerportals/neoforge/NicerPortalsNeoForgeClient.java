package me.roundaround.nicerportals.neoforge;

import me.roundaround.nicerportals.client.NicerPortalsClient;
import me.roundaround.nicerportals.config.NicerPortalsConfig;
import me.roundaround.nicerportals.config.NicerPortalsPerWorldConfig;
import me.roundaround.nicerportals.generated.Constants;
import me.roundaround.trove.client.gui.screen.ConfigScreen;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// Separate class (not an inline Dist gate in the @Mod ctor) so the dedicated server never links its client classes.
public final class NicerPortalsNeoForgeClient {
  private NicerPortalsNeoForgeClient() {
  }

  public static void init(IEventBus modBus, ModContainer container) {
    modBus.addListener(FMLClientSetupEvent.class, event -> NicerPortalsClient.initClient());

    container.registerExtensionPoint(IConfigScreenFactory.class,
        (modContainer, parent) -> new ConfigScreen(
            parent,
            Constants.MOD_ID,
            NicerPortalsConfig.getInstance(),
            NicerPortalsPerWorldConfig.getInstance()));
  }
}
