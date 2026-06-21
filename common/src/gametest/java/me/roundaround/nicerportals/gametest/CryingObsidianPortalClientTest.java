package me.roundaround.nicerportals.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import me.roundaround.trove.gametest.GameTestAssertionException;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;

/**
 * Crying obsidian frames ({@code portalFrameTag}, on by default): the mod swaps vanilla's hard-coded
 * obsidian frame check for the {@code #nicerportals:portal_frame} tag (obsidian + crying obsidian),
 * in both the ignition gate ({@code BaseFireBlockMixin}) and the shape scan ({@code PortalShapeMixin}).
 * Builds the whole 4x5 frame out of crying obsidian — which vanilla rejects outright — lights it, and
 * asserts the interior fills with nether portal blocks. Single-player, creative.
 */
@ClientGameTest
public class CryingObsidianPortalClientTest implements ClientTest {
  // A second interior cell, to confirm the whole interior — not just the ignition point — converted.
  private static final BlockPos INTERIOR_TOP = new BlockPos(2, 67, PortalTests.Z);

  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().creative().stopTime(true).create()) {
      PortalTests.platform(world);
      PortalTests.standardFrame(world, "minecraft:crying_obsidian");
      world.teleport(1.5, 64.0, -1.0);
      context.waitTicks(2);

      // Light it with flint & steel (real item-use + block update), not a setblock.
      PortalTests.igniteWithFlintAndSteel(context, world, PortalTests.INTERIOR_BOTTOM);

      if (!world.getBlockState(INTERIOR_TOP).is(Blocks.NETHER_PORTAL)) {
        throw new GameTestAssertionException(
            "portalFrameTag: a crying-obsidian frame should light a full portal, but " + INTERIOR_TOP.toShortString()
                + " was " + world.getBlock(INTERIOR_TOP));
      }
    }
  }
}
