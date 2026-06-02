package me.roundaround.nicerportals.neoforge;

import me.roundaround.nicerportals.NicerPortals;
import me.roundaround.nicerportals.client.NicerPortalsClient;
import me.roundaround.nicerportals.config.NicerPortalsConfig;
import me.roundaround.nicerportals.config.NicerPortalsPerWorldConfig;
import me.roundaround.nicerportals.generated.Constants;
import me.roundaround.trove.client.gui.screen.ConfigScreen;
import me.roundaround.trove.neoforge.TroveNeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod("nicerportals")
public final class NicerPortalsNeoForgeMod {
  public NicerPortalsNeoForgeMod(IEventBus modBus, ModContainer container) {
    TroveNeoForge.bootstrap(modBus, container);
    NicerPortals.init();

    modBus.addListener(FMLClientSetupEvent.class, event -> NicerPortalsClient.initClient());

    container.registerExtensionPoint(IConfigScreenFactory.class,
        (modContainer, parent) -> new ConfigScreen(
            parent,
            Constants.MOD_ID,
            NicerPortalsConfig.getInstance(),
            NicerPortalsPerWorldConfig.getInstance()));
  }
}
