import simulation.*;
import metrics.*;
import util.BlockchainUtil;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootApplication
public class Main implements CommandLineRunner {

    @Autowired
    private BlockchainUtil blockchainUtil;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Start Ganache if not running
        // Assume docker-compose up is run separately

        // Create entities
        List<Entity> entities = SimulatorSetup.createEntities();

        // Assign blockchain addresses
        for (Entity entity : entities) {
            entity.blockchainAddress = blockchainUtil.createAccount();
        }

        // Create transactions from the 1000 accounts
        List<Entity> accountEntities = entities.subList(entities.size() - 1000, entities.size());
        List<Transaction> transactions = createTransactions(accountEntities, 100000);

        // Simulate default
        simulateMevDefault(entities, transactions);

        // Reset
        for (Entity e : entities) {
            e.mevEarned = 0;
        }

        // Simulate normalized
        simulateMevNormalized(entities, transactions);
    }

    public void simulateMevDefault(List<Entity> entities, List<Transaction> transactions) {
        List<Validator> validators = SimulatorSetup.createValidators(entities);
        runSimulation(validators, transactions, 1000);
        MEVReport.print(entities, "Default MEV Simulation");
    }

    public void simulateMevNormalized(List<Entity> entities, List<Transaction> transactions) {
        StakeNormalizer.normalize(entities, 1600);
        List<Validator> validators = SimulatorSetup.createValidators(entities);
        runSimulation(validators, transactions, 1000);
        MEVReport.print(entities, "Normalized MEV Simulation");
    }

    private List<Transaction> createTransactions(List<Entity> accounts, int numTransactions) {
        List<Transaction> transactions = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < numTransactions; i++) {
            Entity sender = accounts.get(random.nextInt(accounts.size()));
            String to = "0x" + String.format("%040d", random.nextInt(1000000)); // Random address
            double value = random.nextDouble() * 10; // 0-10 ETH
            boolean isMEV = random.nextDouble() < 0.1; // 10% are MEV
            transactions.add(new Transaction(sender, to, value, isMEV));
        }
        return transactions;
    }

    public static void runSimulation(
            List<Validator> validators,
            List<Transaction> transactions,
            int blocks
    ) {
        for (int i = 0; i < blocks; i++) {
            Validator proposer = ProbabilityCalculator.pickProposer(validators);

            for (Transaction tx : transactions) {
                proposer.owner.mevEarned += MEVEngine.extractMEV(tx);
                // Optionally send to blockchain
                // blockchainUtil.sendTransaction(tx.to, BigDecimal.valueOf(tx.value));
            }
        }
    }
}
