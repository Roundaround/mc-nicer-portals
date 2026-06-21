package me.roundaround.nicerportals.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import me.roundaround.trove.gametest.GameTestAssertionException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * Drives the mod's client-side feature — dedupe portal break sound — the way it happens in game:
 * builds and lights a real nether portal, then a creative player punches out one obsidian frame
 * block. Removing a frame block collapses the whole portal at once (every portal block reverts to
 * air on the same tick), the burst {@code LevelEventHandlerMixin} collapses to a single break sound,
 * while {@code LevelRendererMixin}'s per-tick {@code PortalBreakTracker.cleanup} runs the whole time.
 *
 * <p>The punch goes through {@code MultiPlayerGameMode#startDestroyBlock} — which sends the real
 * {@code START_DESTROY_BLOCK} action so the server runs an authoritative destroy with neighbour
 * updates — not the harness's creative {@code mineBlock}, whose {@code destroyBlock} only edits the
 * client level and never tells the server (so the server-side portal would never collapse).
 *
 * <p>The sound — and the one-vs-many choice — can't be observed from a headless client, so this
 * asserts the structural outcome (the portal forms, then the whole interior collapses to air) and
 * that the client survives the break path with the client mixins live. The dedupe DECISION is pinned
 * deterministically by {@link PortalBreakTrackerTest}. Single-player, creative.
 */
@ClientGameTest
public class NicerPortalsBreakSoundClientTest implements ClientTest {
  private static final BlockPos FRAME_BLOCK = new BlockPos(0, 66, PortalTests.Z);
  private static final BlockPos INTERIOR_TOP = new BlockPos(2, 67, PortalTests.Z);

  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().creative().stopTime(true).create()) {
      PortalTests.platform(world);
      PortalTests.standardFrame(world, "minecraft:obsidian");
      world.teleport(1.5, 64.0, -1.0);
      context.waitTicks(2);

      PortalTests.igniteAndAwait(context, world, PortalTests.INTERIOR_BOTTOM, PortalTests.INTERIOR_BOTTOM);

      // Punch out a frame block as a player would: aim at it, then START_DESTROY_BLOCK so the server
      // runs the authoritative break and the portal collapses (mineBlock's creative path is client-only).
      world.lookAt(FRAME_BLOCK);
      context.waitTick();
      context.runOnClient((mc) -> {
        Direction face = mc.hitResult instanceof BlockHitResult hit && hit.getType() == HitResult.Type.BLOCK
            ? hit.getDirection()
            : Direction.NORTH;
        mc.gameMode.startDestroyBlock(FRAME_BLOCK, face);
      });
      world.settle();

      // The whole interior should revert to air once the frame is incomplete.
      context.waitFor(
          (mc) -> mc.level != null
              && !mc.level.getBlockState(PortalTests.INTERIOR_BOTTOM).is(Blocks.NETHER_PORTAL),
          100
      );
      if (world.getBlockState(INTERIOR_TOP).is(Blocks.NETHER_PORTAL)) {
        throw new GameTestAssertionException(
            "breaking the frame should collapse the entire portal, but " + INTERIOR_TOP.toShortString()
                + " was still a nether portal");
      }

      // A few more ticks so the cleanup hook runs against the now-drained tracker.
      context.waitTicks(5);
    }
  }
}
