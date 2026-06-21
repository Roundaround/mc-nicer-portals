package me.roundaround.nicerportals.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.nicerportals.client.PortalBreakTracker;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.GameTestAssertionException;

/**
 * Pins the dedupe DECISION behind "one sound per portal break". When a portal
 * collapses, the break events span the tick they fire on plus the next one;
 * {@code LevelEventHandlerMixin} plays the first and suppresses the rest by asking
 * {@link PortalBreakTracker} whether the tick is already tracked, and
 * {@code LevelRendererMixin} evicts a tracked tick two ticks later. That choice
 * isn't observable from a headless world (see {@link NicerPortalsBreakSoundClientTest}),
 * so this drives the tracker directly: a regression in the suppression window or the
 * cleanup threshold would silently bring the duplicate sounds back with no other test
 * to catch it. It is a client game test because {@code PortalBreakTracker} is
 * client-only code and this stack has no other harness for it. The tick value is far
 * from any real game time so it can't collide with the shared singleton's live state.
 */
@ClientGameTest
public class PortalBreakTrackerTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    PortalBreakTracker tracker = PortalBreakTracker.getInstance();
    long tick = 1_000_000L;

    // A tick no break has touched is open: the first sound on it is allowed to play.
    assertFalse(tracker.isAlreadyTracked(tick), "a fresh tick must not be tracked");

    // Once the first break records the tick, the rest of that burst — and the one
    // that straggles into the following tick — are suppressed.
    tracker.add(tick);
    assertTrue(tracker.isAlreadyTracked(tick), "the tick must be tracked right after add");
    assertTrue(tracker.isAlreadyTracked(tick + 1), "the next tick falls inside the suppression window");

    // The window is only the add tick and the one after it; a later break sounds again.
    assertFalse(tracker.isAlreadyTracked(tick + 2), "two ticks on, suppression must have lapsed");

    // The per-tick cleanup evicts the tracked tick once it is two ticks old, so the
    // set can't grow without bound and a reused tick later is open again.
    tracker.cleanup(tick + 2);
    assertFalse(tracker.isAlreadyTracked(tick), "cleanup must evict a tick that is >= 2 ticks old");
  }

  private static void assertTrue(boolean condition, String message) {
    if (!condition) {
      throw new GameTestAssertionException("PortalBreakTracker: " + message);
    }
  }

  private static void assertFalse(boolean condition, String message) {
    assertTrue(!condition, message);
  }
}
