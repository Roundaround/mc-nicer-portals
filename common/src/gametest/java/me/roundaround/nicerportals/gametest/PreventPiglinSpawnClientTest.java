package me.roundaround.nicerportals.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.nicerportals.config.NicerPortalsPerWorldConfig;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import me.roundaround.trove.gametest.GameTestAssertionException;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

/**
 * Prevent zombified piglin spawns ({@code preventPortalSpawns}, on by default): a nether portal in
 * the overworld randomly spawns zombified piglins on {@code NetherPortalBlock#randomTick};
 * {@code NetherPortalBlockMixin} forces the spawn check false while the config is on. This runs on the
 * single-player integrated server, where that server-side mixin is live.
 *
 * <p>A negative ("no piglin spawned") is only meaningful if the setup COULD spawn one, so the test
 * proves both directions against the same cranked random-tick rate: with the mod on (default) it waits
 * out a window and asserts no piglin appears, then flips the config off and asserts one does. A 5x5
 * portal plus {@code randomTickSpeed} cranked makes the off-direction spawn near-instant; the
 * on-direction zero is deterministic because the mixin hard-denies the spawn. The config flip restores
 * itself on cleanup. Single-player, creative.
 */
@ClientGameTest
public class PreventPiglinSpawnClientTest implements ClientTest {
  // 5x5 air interior inside a 7x7 obsidian ring in the X/Y plane at Z; more portal blocks = faster spawns.
  private static final int Z = PortalTests.Z;
  private static final BlockPos IGNITE = new BlockPos(1, 65, Z);
  private static final Vec3 PORTAL_CENTER = new Vec3(3.0, 67.0, Z + 0.5);
  private static final double SCAN_RADIUS = 24.0;
  private static final int ON_WINDOW_TICKS = 60;
  private static final int OFF_BUDGET_TICKS = 20 * 30;

  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().creative().stopTime(true).create()) {
      PortalTests.platform(world);
      buildBigFrame(world);
      world.teleport(3.5, 64.0, -2.0);
      context.waitTicks(2);

      PortalTests.igniteAndAwait(context, world, IGNITE, IGNITE);

      // Light up the spawn machinery: portals only spawn piglins when mob spawning is on, and the
      // throwaway world starts with it off. Crank random ticks (26.x id is snake_case) so the
      // off-direction spawn lands fast — the portal's 25 blocks see thousands of random ticks a tick.
      world.runCommand("gamerule spawn_mobs true");
      world.runCommand("gamerule random_tick_speed 4000");

      // On-direction (default): the mixin denies the spawn check, so no piglin can appear.
      context.waitTicks(ON_WINDOW_TICKS);
      if (context.computeOnClient(mc -> piglinNear(mc, PORTAL_CENTER, SCAN_RADIUS))) {
        throw new GameTestAssertionException(
            "preventPortalSpawns on: a zombified piglin spawned from the portal despite the mod");
      }

      // Off-direction: the same setup must actually be able to spawn one, or the check above was
      // vacuous. Flip the config off (restored on cleanup) and wait for a spawn.
      PortalTests.withWorldBoolean(context, (config) -> config.preventPortalSpawns, false);
      context.waitFor((mc) -> piglinNear(mc, PORTAL_CENTER, SCAN_RADIUS), OFF_BUDGET_TICKS);
    }
  }

  /** True if any zombified piglin is within {@code radius} of {@code center} in the client level. */
  private static boolean piglinNear(Minecraft mc, Vec3 center, double radius) {
    if (mc.level == null) {
      return false;
    }
    double radiusSq = radius * radius;
    for (Entity entity : mc.level.entitiesForRendering()) {
      if (entity.getType() == EntityTypes.ZOMBIFIED_PIGLIN && entity.position().distanceToSqr(center) <= radiusSq) {
        return true;
      }
    }
    return false;
  }

  private static void buildBigFrame(ClientWorld world) {
    // 7x7 obsidian ring (x 0..6, y 64..70), 5x5 air interior (x 1..5, y 65..69).
    world.fill(new BlockPos(0, 64, Z), new BlockPos(6, 64, Z), "minecraft:obsidian");
    world.fill(new BlockPos(0, 70, Z), new BlockPos(6, 70, Z), "minecraft:obsidian");
    world.fill(new BlockPos(0, 64, Z), new BlockPos(0, 70, Z), "minecraft:obsidian");
    world.fill(new BlockPos(6, 64, Z), new BlockPos(6, 70, Z), "minecraft:obsidian");
    world.fill(new BlockPos(1, 65, Z), new BlockPos(5, 69, Z), "minecraft:air");
  }
}
