package me.roundaround.nicerportals.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import me.roundaround.trove.gametest.GameTestAssertionException;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;

/**
 * Custom portal shapes ({@code anyShape}, on by default): {@code PortalShapeMixin} flood-fills
 * the enclosed air region instead of demanding a rectangle, then fills every cell it found with
 * portal blocks. This builds a standard 2x3 frame but pokes a one-block bump out the right wall
 * (open the wall at {@code (3,66)}, cap it at {@code (4,66)}) so the interior is no longer a
 * rectangle vanilla would accept, lights it, and asserts a portal block lands in the BUMP at
 * {@code (3,66)} — a position vanilla would never fill. That bump being a nether portal is only
 * possible through the mod's flood-fill creation path. Single-player, creative.
 */
@ClientGameTest
public class AnyShapePortalClientTest implements ClientTest {
  private static final BlockPos BUMP = new BlockPos(3, 66, PortalTests.Z);
  private static final BlockPos BUMP_CAP = new BlockPos(4, 66, PortalTests.Z);

  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().creative().stopTime(true).create()) {
      PortalTests.platform(world);
      PortalTests.standardFrame(world, "minecraft:obsidian");
      // Turn the rectangle into a non-rectangle: open the right wall and cap the bump one out.
      world.setBlock(BUMP, "minecraft:air");
      world.setBlock(BUMP_CAP, "minecraft:obsidian");
      world.teleport(1.5, 64.0, -1.0);
      context.waitTicks(2);

      // Light it with flint & steel (real item-use + block update). The core interior lighting up
      // confirms the frame ignited at all...
      PortalTests.igniteWithFlintAndSteel(context, world, PortalTests.INTERIOR_BOTTOM);

      // ...and the bump being a portal block proves the flood-fill ran past the rectangle.
      if (!world.getBlockState(BUMP).is(Blocks.NETHER_PORTAL)) {
        throw new GameTestAssertionException(
            "anyShape: expected a nether portal block in the non-rectangular bump at " + BUMP.toShortString()
                + " but found " + world.getBlock(BUMP));
      }
    }
  }
}
