package kiet.mevsimulator;

import kiet.mevsimulator.metrics.MEVReport;
import kiet.mevsimulator.metrics.ProbabilityCalculator;
import kiet.mevsimulator.simulation.*;
import kiet.mevsimulator.util.BlockchainUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import kiet.mevsimulator.util.CSVExporter;
import kiet.mevsimulator.util.ExcelExporter;
import kiet.mevsimulator.util.ResearchExcelExporter;
import kiet.mevsimulator.util.ResearchExcelExporterPro;
import java.util.stream.Collectors;





import java.math.BigDecimal;
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
        // CAUTION! Docker container hosting Ganache node must be running

        // Create entities following Fibonacci series
        List<Entity> entities = SimulatorSetup.createEntities(3, 5, 8, 13, 21, 34, 55);

        // Assign blockchain addresses
        for (Entity entity : entities) {
            entity.blockchainAddress = blockchainUtil.createAccount();
        }

        // Create transactions from the 1000 accounts
        List<Entity> accountEntities = entities.subList(entities.size() - 1000, entities.size());
        List<Transaction> transactions = createTransactions(accountEntities, 100);

        //________________________________________________________________________________
//        // Simulate default
//        simulateMevDefault(entities, transactions);
////        CSVExporter.exportEntities(entities, "default_mev_results.csv");
////        ExcelExporter.export(entities, "default_results.xlsx");
////        List<Entity> defaultCopy = new ArrayList<>(entities);
////
////        for (Entity e : entities) {
////            e.mevEarned = 0;
////        }
//        List<Entity> defaultCopy = new ArrayList<>(entities);
//        for (Entity e : entities) {
//            e.mevEarned = 0;
//        }
//
//
//        // Reset
//        for (Entity e : entities) {
//            e.mevEarned = 0;
//        }
//
//        // Simulate normalized
//        simulateMevNormalized(entities, transactions);
////        CSVExporter.exportEntities(entities, "normalized_mev_results.csv");
//
////        ExcelExporter.export(entities, "normalized_results.xlsx");
////        ResearchExcelExporter.export(
////                defaultCopy,
////                entities,
////                "research_results.xlsx"
////        );
//        ResearchExcelExporterPro.export(
//                defaultCopy,
//                entities,
//                "MEV_Research_Report.xlsx"
//        );
        //--------------------------------------------------
        simulateMevDefault(entities, transactions);

// deep copy BEFORE resetting
        List<Entity> defaultCopy = entities.stream()
                .map(Entity::new)          // uses the copy constructor
                .collect(Collectors.toList());

// now safe to reset originals
        for (Entity e : entities) {
            e.mevEarned = 0;
        }

        simulateMevNormalized(entities, transactions);

// export using deep-copied default and current normalized
        ResearchExcelExporterPro.export(defaultCopy, entities, "MEV_Research_Report.xlsx");
    }

    public void simulateMevDefault(List<Entity> entities, List<Transaction> transactions) throws Exception {
        List<Validator> validators = SimulatorSetup.createEntities(entities);
        runSimulation(validators, transactions, 5, false);
        MEVReport.print(entities, "Default MEV Simulation");
    }

    public void simulateMevNormalized(List<Entity> entities, List<Transaction> transactions) throws Exception {
        StakeNormalizer.normalize(entities, 1600);
        List<Validator> validators = SimulatorSetup.createEntities(entities);
        runSimulation(validators, transactions, 100,true);
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

    public void runSimulation(
            List<Validator> validators,
            List<Transaction> transactions,
            int blocks,
            boolean normalized
    ) throws Exception {
        for (int i = 0; i < blocks; i++) {
            Validator proposer = ProbabilityCalculator.pickProposer(validators);

            for (Transaction tx : transactions) {
                if(normalized){
                    proposer.owner.mevEarned += MEVEngine.extractMEVNormalized(tx);
                }else{
                    proposer.owner.mevEarned += MEVEngine.extractMEV(tx);
                }
                // Optionally send to blockchain
                blockchainUtil.sendTransaction(tx.to, BigDecimal.valueOf(tx.value));
            }
        }
    }
}
