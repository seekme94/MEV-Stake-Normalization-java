package metrics;

import simulation.Entity;
import java.util.List;

public class MEVReport {

    public static void print(List<Entity> entities, String title) {
        System.out.println("\n=== " + title + " ===");

        for (Entity e : entities) {
            System.out.printf(
                "%s | Stake: %d | MEV Earned: %.2f\n",
                e.name,
                e.effectiveStake,
                e.mevEarned
            );
        }
    }
}
