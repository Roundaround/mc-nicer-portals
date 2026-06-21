package me.roundaround.nicerportals.mixin;

import me.roundaround.allay.api.MixinEnv;
import me.roundaround.nicerportals.client.PortalBreakTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

// 26.2 removed LevelRenderer#tick and its ClientLevel level field; hook the client level's own tick instead.
@MixinEnv(MixinEnv.Env.CLIENT)
@Mixin(ClientLevel.class)
public abstract class LevelRendererMixin {
  @Inject(method = "tick", at = @At(value = "TAIL"))
  private void tick(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
    PortalBreakTracker.getInstance().cleanup(((ClientLevel) (Object) this).getGameTime());
  }
}
