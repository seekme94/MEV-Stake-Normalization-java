import simulation.*;
import metrics.*;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void runSimulation(
            List<Validator> validators,
            List<Transaction> transactions,
            int blocks
    ) {
        for (int i = 0; i < blocks; i++) {
            Validator proposer = ProbabilityCalculator.pickProposer(validators);

            for (Transaction tx : transactions) {
                proposer.owner.mevEarned += MEVEngine.extractMEV(tx);
            }
        }
    }

    public static void main(String[] args) {

        // Create entities
        List<Entity> entities = SimulatorSetup.createEntities();

        // Create transactions (100,000)
        List<Transaction> transactions = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            transactions.add(new Transaction(10, i % 10 == 0));
        }

        // BEFORE normalization
        List<Validator> validatorsBefore =
                SimulatorSetup.createValidators(entities);

        runSimulation(validatorsBefore, transactions, 1000);
        MEVReport.print(entities, "Before Stake Normalization");

        // Reset MEV counters
        for (Entity e : entities) {
            e.mevEarned = 0;
        }

        // AFTER normalization
        StakeNormalizer.normalize(entities, 1600);
        List<Validator> validatorsAfter =
                SimulatorSetup.createValidators(entities);

        runSimulation(validatorsAfter, transactions, 1000);
        MEVReport.print(entities, "After Stake Normalization");
    }
}
