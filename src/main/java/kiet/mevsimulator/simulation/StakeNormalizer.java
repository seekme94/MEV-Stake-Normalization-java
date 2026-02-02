package kiet.mevsimulator.simulation;

import java.util.List;

public class StakeNormalizer {

    public static void normalize(List<Entity> entities, int cap) {
        for (Entity e : entities) {
            e.effectiveStake = Math.min(e.totalStake, cap);
        }
    }
}
