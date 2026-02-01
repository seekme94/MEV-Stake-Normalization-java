package simulation;

import java.util.ArrayList;
import java.util.List;

public class SimulatorSetup {

    public static List<Entity> createEntities() {
        List<Entity> entities = new ArrayList<>();

        // 5 whales
        for (int i = 0; i < 5; i++) {
            entities.add(new Entity("Whale_" + i, 3200));
        }

        // 25 middle investors
        for (int i = 0; i < 25; i++) {
            entities.add(new Entity("Mid_" + i, 800));
        }

        // 70 small investors
        for (int i = 0; i < 70; i++) {
            entities.add(new Entity("Small_" + i, 100));
        }

        return entities;
    }

    public static List<Validator> createValidators(List<Entity> entities) {
        List<Validator> validators = new ArrayList<>();

        for (Entity e : entities) {
            int remaining = e.effectiveStake;

            while (remaining >= 32) {
                validators.add(new Validator(e, 32));
                remaining -= 32;
            }
        }

        return validators;
    }
}
