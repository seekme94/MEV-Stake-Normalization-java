package metrics;

import simulation.Validator;
import java.util.List;
import java.util.Random;

public class ProbabilityCalculator {

    public static Validator pickProposer(List<Validator> validators) {
        int totalStake = 0;
        for (Validator v : validators) {
            totalStake += v.stake;
        }

        int randomPoint = new Random().nextInt(totalStake);
        int cumulative = 0;

        for (Validator v : validators) {
            cumulative += v.stake;
            if (randomPoint < cumulative) {
                return v;
            }
        }
        return validators.get(0);
    }
}
