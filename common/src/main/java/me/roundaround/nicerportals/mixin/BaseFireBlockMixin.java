package me.roundaround.nicerportals.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.roundaround.nicerportals.config.NicerPortalsPerWorldConfig;
import me.roundaround.nicerportals.tags.BlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// isPortal gates the expensive PortalShape scan on a cheap adjacent-frame check, which diverges
// by loader: Fabric runs vanilla BlockState#is(OBSIDIAN); NeoForge/Forge binpatch isPortal to call
// BlockState#isPortalFrame instead. Wrap whichever call the running jar has — require = 0 lets the
// non-matching variant no-op rather than crash the (otherwise dedicated-server) injection check.
@Mixin(BaseFireBlock.class)
public abstract class BaseFireBlockMixin {
  @WrapOperation(
      method = "isPortal",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"),
      require = 0
  )
  private static boolean nicerportals$frameViaVanillaIs(BlockState instance, Object block, Operation<Boolean> original) {
    if (!NicerPortalsPerWorldConfig.getInstance().portalFrameTag.getValue()) {
      return original.call(instance, block);
    }
    return instance.is(BlockTags.PORTAL_FRAME);
  }

  @WrapOperation(
      method = "isPortal",
      at = @At(
          value = "INVOKE",
          target = "Lnet/minecraft/world/level/block/state/BlockState;isPortalFrame(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z"
      ),
      require = 0
  )
  private static boolean nicerportals$frameViaLoaderHook(
      BlockState instance, BlockGetter level, BlockPos pos, Operation<Boolean> original) {
    if (!NicerPortalsPerWorldConfig.getInstance().portalFrameTag.getValue()) {
      return original.call(instance, level, pos);
    }
    return instance.is(BlockTags.PORTAL_FRAME);
  }
}
