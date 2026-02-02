package simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimulatorSetup {

    public static List<Entity> createEntities() {
        List<Entity> entities = new ArrayList<>();
        Random random = new Random();

        // Whale: 6000 ETH, 3
        for (int i = 0; i < 3; i++) {
            entities.add(new Entity("Whale_" + i, "Whale", 6000));
        }

        // Shark: 3000 ETH, 5
        for (int i = 0; i < 5; i++) {
            entities.add(new Entity("Shark_" + i, "Shark", 3000));
        }

        // Dolphin: 600 ETH, 8
        for (int i = 0; i < 8; i++) {
            entities.add(new Entity("Dolphin_" + i, "Dolphin", 600));
        }

        // Fish: 300 ETH, 13
        for (int i = 0; i < 13; i++) {
            entities.add(new Entity("Fish_" + i, "Fish", 300));
        }

        // Octopus: 60 ETH, 21
        for (int i = 0; i < 21; i++) {
            entities.add(new Entity("Octopus_" + i, "Octopus", 60));
        }

        // Crab: 6 ETH, 34
        for (int i = 0; i < 34; i++) {
            entities.add(new Entity("Crab_" + i, "Crab", 6));
        }

        // Shrimp: 1 ETH, 55
        for (int i = 0; i < 55; i++) {
            entities.add(new Entity("Shrimp_" + i, "Shrimp", 1));
        }

        // 1000 additional accounts with 0.1 to 1000 ETH, normally distributed
        for (int i = 0; i < 1000; i++) {
            // Normal distribution with mean 500, std 200, clamped to 0.1-1000
            double stake = Math.max(0.1, Math.min(1000, random.nextGaussian() * 200 + 500));
            entities.add(new Entity("Account_" + i, "Account", (int) Math.round(stake)));
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
