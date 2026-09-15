package me.roundaround.nicerportals.mixin;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PortalShape.class)
public interface PortalShapeAccessor {
  @Invoker("isEmpty")
  static boolean isValidStateInsidePortal(BlockState state) {
    throw new AssertionError();
  }
}
