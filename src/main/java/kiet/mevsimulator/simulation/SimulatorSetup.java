package kiet.mevsimulator.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimulatorSetup {

    public static List<Entity> createEntities(int whales, int sharks, int dolphins, int fish, int octopuses, int crabs, int shrimps) {
        List<Entity> entities = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < whales; i++) {
            entities.add(new Entity("Whale_" + i, "Whale", 2048));
        }
        for (int i = 0; i < sharks; i++) {
            entities.add(new Entity("Shark_" + i, "Shark", 1024));
        }
        for (int i = 0; i < dolphins; i++) {
            entities.add(new Entity("Dolphin_" + i, "Dolphin", 512));
        }
        for (int i = 0; i < fish; i++) {
            entities.add(new Entity("Fish_" + i, "Fish", 256));
        }
        for (int i = 0; i < octopuses; i++) {
            entities.add(new Entity("Octopus_" + i, "Octopus", 128));
        }
        for (int i = 0; i < crabs; i++) {
            entities.add(new Entity("Crab_" + i, "Crab", 32));
        }

        // 1000 additional accounts with 0.1 to 1000 ETH, normally distributed
        for (int i = 0; i < 1000; i++) {
            // Normal distribution from 0.1 to 1000 ETH with mean 200 and standard deviation of 100
            double stake = Math.max(0.1, Math.min(1000, random.nextGaussian() * 100 + 200));
            entities.add(new Entity("Account_" + i, "Account", (int) Math.round(stake)));
        }
        return entities;
    }

    public static List<Validator> createEntities(List<Entity> entities) {
//        List<Validator> validators = new ArrayList<>();
//        for (Entity e : entities) {
//            int remaining = e.effectiveStake;
//
//            while (remaining >= 32) {
//                validators.add(new Validator(e, 32));
//                remaining -= 32;
//            }
//        }
//
//        return validators;
//    }
        List<Validator> validators = new ArrayList<>();

        for (Entity e : entities) {

            // Accounts are NOT validators
            if (e.category.equals("Account")) {
                continue;
            }

            int remaining = e.effectiveStake;

            while (remaining >= 32) {
                validators.add(new Validator(e, 32));
                remaining -= 32;
            }
        }

        return validators;
    }
}
