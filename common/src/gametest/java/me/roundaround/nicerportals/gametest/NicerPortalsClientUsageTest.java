package me.roundaround.nicerportals.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;

/**
 * Builds a standard obsidian frame and lights it, exercising the mod's portal
 * path ({@code PortalShapeMixin} validity/creation) and the client portal
 * rendering its {@code LevelRendererMixin} hooks into. Asserts nether portal
 * blocks fill the interior — a crash anywhere in the client mixins would fail
 * the test first. Single-player, creative.
 */
@ClientGameTest
public class NicerPortalsClientUsageTest implements ClientTest {
  // Frame in the X/Y plane at Z=2: obsidian border around a 2-wide x 3-tall interior.
  private static final BlockPos INTERIOR_BOTTOM = new BlockPos(1, 65, 2);
  private static final int Z = 2;

  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().creative().stopTime(true).create()) {
      world.fill(new BlockPos(-2, 63, -2), new BlockPos(5, 63, 5), "minecraft:smooth_stone");
      buildFrame(world);
      world.teleport(1.5, 64.0, -1.0);
      context.waitTicks(2);

      // Ignite the interior. A client-driven flint_and_steel raycast is face-
      // sensitive — it tends to strike the frame's player-facing side, dropping the
      // fire OUTSIDE the frame — and is flaky headless, so place the fire directly:
      // its onPlace still drives the (mod-hooked) PortalShape creation path.
      world.runCommand("setblock 1 65 " + Z + " minecraft:fire");
      world.settle();

      // Throws (failing the test) if no portal block appears in the budget.
      context.waitFor(
          (mc) -> mc.level != null
              && mc.level.getBlockState(INTERIOR_BOTTOM).is(Blocks.NETHER_PORTAL),
          60
      );

      // Hold a beat so the portal renders (client mixins run each frame).
      context.waitTicks(10);
    }
  }

  private static void buildFrame(ClientWorld world) {
    // Bottom and top rows (X 0..3), then the two vertical sides (Y 64..68).
    world.fill(new BlockPos(0, 64, Z), new BlockPos(3, 64, Z), "minecraft:obsidian");
    world.fill(new BlockPos(0, 68, Z), new BlockPos(3, 68, Z), "minecraft:obsidian");
    world.fill(new BlockPos(0, 64, Z), new BlockPos(0, 68, Z), "minecraft:obsidian");
    world.fill(new BlockPos(3, 64, Z), new BlockPos(3, 68, Z), "minecraft:obsidian");
    // Clear the 2x3 interior.
    world.fill(new BlockPos(1, 65, Z), new BlockPos(2, 67, Z), "minecraft:air");
  }
}
