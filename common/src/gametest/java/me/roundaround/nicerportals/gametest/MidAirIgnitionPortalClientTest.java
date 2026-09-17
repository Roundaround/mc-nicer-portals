package me.roundaround.nicerportals.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import me.roundaround.trove.gametest.GameTestAssertionException;
import net.minecraft.world.level.block.Blocks;

/**
 * The ignition gate ({@code BaseFireBlockMixin}), which the other crying-obsidian test never
 * reaches: lighting off a floor block satisfies {@code FireBlock#canSurvive} and short-circuits
 * {@code canBePlacedAt} before {@code BaseFireBlock#isPortal} is consulted. Lighting off an inner
 * side face drops the fire into mid-air, where {@code isPortal}'s adjacent-frame check is the only
 * thing that can allow the placement — and vanilla's check rejects a crying-obsidian frame.
 * Single-player, creative.
 */
@ClientGameTest
public class MidAirIgnitionPortalClientTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().creative().stopTime(true).create()) {
      PortalTests.platform(world);
      PortalTests.standardFrame(world, "minecraft:crying_obsidian");
      world.teleport(1.5, 64.0, -1.0);
      context.waitTicks(2);

      PortalTests.igniteFromSideWithFlintAndSteel(context, world, PortalTests.INTERIOR_MID);

      if (!world.getBlockState(PortalTests.INTERIOR_BOTTOM).is(Blocks.NETHER_PORTAL)) {
        throw new GameTestAssertionException(
            "portalFrameTag: mid-air ignition inside a crying-obsidian frame should light a full portal, but "
                + PortalTests.INTERIOR_BOTTOM.toShortString() + " was "
                + world.getBlock(PortalTests.INTERIOR_BOTTOM));
      }
    }
  }
}
