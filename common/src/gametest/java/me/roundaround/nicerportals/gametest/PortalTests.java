package me.roundaround.nicerportals.gametest;

import me.roundaround.nicerportals.config.NicerPortalsPerWorldConfig;
import me.roundaround.trove.config.option.BooleanConfigOption;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

/**
 * Shared scaffolding for the Nicer Portals client gametests. The mod's features all
 * key off building a portal frame, lighting it, and reading the resulting blocks, so
 * the per-feature classes share a flat stone pad, a standard 4x5 frame builder (the
 * frame block is a parameter so the crying-obsidian test reuses it), an ignite-and-wait
 * primitive, and a per-world config toggle that restores itself on cleanup.
 *
 * <p>Geometry: the frame stands in the X/Y plane at constant {@link #Z}, a 2-wide x
 * 3-tall air interior inside an obsidian ring, sitting on the pad at y63. Igniting at
 * {@link #INTERIOR_BOTTOM} fills the interior with nether portal blocks.
 */
final class PortalTests {
  static final int Z = 2;
  /** Lower-left interior cell: where the fire goes and the first portal block appears. */
  static final BlockPos INTERIOR_BOTTOM = new BlockPos(1, 65, Z);

  private PortalTests() {
  }

  /** A flat smooth-stone pad under the build so the portal sits on real geometry. */
  static void platform(ClientWorld world) {
    world.fill(new BlockPos(-3, 63, -3), new BlockPos(6, 63, 6), "minecraft:smooth_stone");
  }

  /** Build the classic 4x5 frame (2x3 air interior) from {@code frameBlockId} in the X/Y plane at {@link #Z}. */
  static void standardFrame(ClientWorld world, String frameBlockId) {
    world.fill(new BlockPos(0, 64, Z), new BlockPos(3, 64, Z), frameBlockId);
    world.fill(new BlockPos(0, 68, Z), new BlockPos(3, 68, Z), frameBlockId);
    world.fill(new BlockPos(0, 64, Z), new BlockPos(0, 68, Z), frameBlockId);
    world.fill(new BlockPos(3, 64, Z), new BlockPos(3, 68, Z), frameBlockId);
    world.fill(new BlockPos(1, 65, Z), new BlockPos(2, 67, Z), "minecraft:air");
  }

  /**
   * Drop fire at {@code ignite} (a headless flint_and_steel raycast is face-flaky, but the
   * fire's onPlace still drives the mod-hooked PortalShape path) and block until a nether
   * portal block appears at {@code expectPortal}.
   */
  static void igniteAndAwait(ClientTestContext context, ClientWorld world, BlockPos ignite, BlockPos expectPortal) {
    world.runCommand("setblock " + ignite.getX() + " " + ignite.getY() + " " + ignite.getZ() + " minecraft:fire");
    world.settle();
    context.waitFor(
        (mc) -> mc.level != null && mc.level.getBlockState(expectPortal).is(Blocks.NETHER_PORTAL),
        60
    );
  }

  /**
   * Light the portal the way a player does: flint &amp; steel onto the top face of the frame block
   * directly under {@code interiorBottom}, dropping fire into the interior cell. This runs the real
   * item-use path — {@code FlintAndSteelItem#useOn} → {@code setBlock(.., 11)} → fire {@code onPlace}
   * → portal scan — so the mod's ignition hooks fire on a genuine block update, not a {@code setblock}.
   * The hit is built with an explicit UP face (a headless look-raycast is face-flaky and tends to
   * strike the player-facing side). Blocks until a nether portal block appears at {@code interiorBottom}.
   */
  static void igniteWithFlintAndSteel(ClientTestContext context, ClientWorld world, BlockPos interiorBottom) {
    BlockPos frameBelow = interiorBottom.below();
    world.setMainHandItem("minecraft:flint_and_steel");
    context.waitTicks(2);
    world.lookAt(frameBelow);
    context.runOnClient((mc) -> {
      Vec3 hit = new Vec3(frameBelow.getX() + 0.5, frameBelow.getY() + 1.0, frameBelow.getZ() + 0.5);
      BlockHitResult result = new BlockHitResult(hit, Direction.UP, frameBelow, false);
      mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, result);
      mc.player.swing(InteractionHand.MAIN_HAND);
    });
    world.settle();
    context.waitFor(
        (mc) -> mc.level != null && mc.level.getBlockState(interiorBottom).is(Blocks.NETHER_PORTAL),
        60
    );
  }

  /** Set a per-world boolean config option for the test, restoring the prior committed value on cleanup. */
  static void withWorldBoolean(
      ClientTestContext context, Function<NicerPortalsPerWorldConfig, BooleanConfigOption> option, boolean value
  ) {
    context.runOnClient(mc -> {
      BooleanConfigOption opt = option.apply(NicerPortalsPerWorldConfig.getInstance());
      boolean previous = opt.getValue();
      opt.setValue(value);
      opt.commit();
      context.onCleanup(() -> context.runOnClient(m -> {
        BooleanConfigOption restore = option.apply(NicerPortalsPerWorldConfig.getInstance());
        restore.setValue(previous);
        restore.commit();
      }));
    });
  }
}
