package me.roundaround.nicerportals.forge;

import me.roundaround.nicerportals.NicerPortals;
import me.roundaround.nicerportals.client.NicerPortalsClient;
import me.roundaround.nicerportals.config.NicerPortalsConfig;
import me.roundaround.nicerportals.config.NicerPortalsPerWorldConfig;
import me.roundaround.nicerportals.generated.Constants;
import me.roundaround.trove.client.gui.screen.ConfigScreen;
import me.roundaround.trove.forge.TroveForge;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("nicerportals")
public final class NicerPortalsForgeMod {
  public NicerPortalsForgeMod(FMLJavaModLoadingContext context) {
    TroveForge.bootstrap(context);
    NicerPortals.init();

    FMLClientSetupEvent.getBus(context.getModBusGroup())
        .addListener(event -> NicerPortalsClient.initClient());

    context.getContainer().registerExtensionPoint(
        ConfigScreenHandler.ConfigScreenFactory.class,
        () -> new ConfigScreenHandler.ConfigScreenFactory((mc, parent) -> new ConfigScreen(
            parent,
            Constants.MOD_ID,
            NicerPortalsConfig.getInstance(),
            NicerPortalsPerWorldConfig.getInstance())));
  }
}
