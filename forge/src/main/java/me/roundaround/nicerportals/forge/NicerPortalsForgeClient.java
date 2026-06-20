package me.roundaround.nicerportals.forge;

import me.roundaround.nicerportals.client.NicerPortalsClient;
import me.roundaround.nicerportals.config.NicerPortalsConfig;
import me.roundaround.nicerportals.config.NicerPortalsPerWorldConfig;
import me.roundaround.nicerportals.generated.Constants;
import me.roundaround.trove.client.gui.screen.ConfigScreen;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// Separate class (not an inline Dist gate in the @Mod ctor) so the dedicated server never links its client classes.
public final class NicerPortalsForgeClient {
  private NicerPortalsForgeClient() {
  }

  public static void init(FMLJavaModLoadingContext context) {
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
