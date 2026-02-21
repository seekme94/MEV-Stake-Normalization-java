package kiet.mevsimulator;

import kiet.mevsimulator.simulation.*;
import kiet.mevsimulator.util.BlockchainUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@SpringBootApplication
public class Main implements CommandLineRunner {

    private static final int NUM_WHALES = 3;

    private static final int NUM_SHARKS = 5;

    private static final int NUM_DOLPHINS = 8;

    private static final int NUM_FISH = 13;

    private static final int NUM_OCTOPUSES = 21;

    private static final int NUM_CRABS = 34;

    private static final int NUM_SHRIMPS = 500;

    public static final int NUM_BLOCKS = 100;

    @Autowired
    private BlockchainUtil blockchainUtil;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // CAUTION! Docker container hosting Ganache node must be running
        List<Entity> entities = createEntities(
                NUM_WHALES, NUM_SHARKS, NUM_DOLPHINS, NUM_FISH, NUM_OCTOPUSES, NUM_CRABS, NUM_SHRIMPS
        );
        List<Entity> accountEntities = new ArrayList<>();
        List<Entity> validators = new ArrayList<>();
        // Assign blockchain addresses
        for (Entity entity : entities) {
            entity.blockchainAddress = blockchainUtil.createAccount();
            if (!entity.isValidator()) {
                accountEntities.add(entity);
            } else {
                validators.add(entity);
            }
        }

        // Create transactions from the non-staking accounts
        List<Block> blocks = createBlocks(accountEntities);

        // Run Default Simulation
        runSimulation(validators, blocks);

        printReport(validators, "Simulation Results");

        // Save results and export
        export(validators, "comparison_results.csv");
    }

    public static List<Entity> createEntities(int whales, int sharks, int dolphins, int fish, int octopuses, int crabs, int shrimps) {
        List<Entity> entities = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < whales; i++) {
            entities.add(new Entity("Whale_" + i, EntityType.WHALE, 2048));
        }
        for (int i = 0; i < sharks; i++) {
            entities.add(new Entity("Shark_" + i, EntityType.SHARK, 1024));
        }
        for (int i = 0; i < dolphins; i++) {
            entities.add(new Entity("Dolphin_" + i, EntityType.DOLPHIN, 512));
        }
        for (int i = 0; i < fish; i++) {
            entities.add(new Entity("Fish_" + i, EntityType.FISH, 256));
        }
        for (int i = 0; i < octopuses; i++) {
            entities.add(new Entity("Octopus_" + i, EntityType.OCTOPUS, 128));
        }
        for (int i = 0; i < crabs; i++) {
            entities.add(new Entity("Crab_" + i, EntityType.CRAB, 32));
        }
        // Additional shrimps accounts with 0.1 to 30ETH, normally distributed with a mean 13
        for (int i = 0; i < shrimps; i++) {
            double stake = Math.max(0.1, Math.min(100, random.nextGaussian() * 13));
            entities.add(new Entity("Account_" + i, EntityType.SHRIMP, (int) Math.round(stake)));
        }
        return entities;
    }

    /**
     * Generates a specified number of blockchain blocks, each containing a random number of transactions.
     * Transactions are created using the provided list of entities as senders, and each transaction is given
     * a random recipient, value, and flag indicating whether it is a Maximum Extractable Value (MEV) transaction.
     *
     * @param accounts the list of entities that can act as senders for the transactions within the blocks.
     *                 Each entity in this list must represent a valid participant in the blockchain.
     * @return a list of blocks, where each block contains a set of randomly generated transactions.
     */
    private List<Block> createBlocks(List<Entity> accounts) {
        List<Block> blocks = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < NUM_BLOCKS; i++) {
            Block block = new Block();
            int numTransactions = 100 + random.nextInt(101); // 100 to 200 transactions
            for (int j = 0; j < numTransactions; j++) {
                Entity sender = accounts.get(random.nextInt(accounts.size()));
                String to = "0x" + String.format("%040d", random.nextInt(1000000));
                double value = random.nextDouble() * 10;
                boolean isMEV = random.nextDouble() < 0.1;
                block.addTransaction(new Transaction(sender, to, value, isMEV));
            }
            blocks.add(block);
        }
        return blocks;
    }


    /**
     * Distributes the Maximum Extractable Value (MEV) of a blockchain block among a list of proposers
     * proportionally based on their total stake.
     *
     * @param block the blockchain block containing the total MEV to be distributed. The block must include
     *              a valid totalMEV value representing the total Maximum Extractable Value realized
     *              by the block's transactions.
     * @param proposers the list of entities eligible to receive a share of the block's MEV. Each entity
     *                  in this list must have a defined totalStake as it determines the proportion of MEV
     *                  they receive. If the total stake is zero, no MEV is distributed.
     */
    public static void extractMEV(Block block, List<Entity> proposers) {
        double totalStake = 0;
        for (Entity p : proposers) {
            totalStake += p.totalStake;
        }
        if (totalStake == 0) return;
        for (Entity p : proposers) {
            p.mevEarned += block.totalMEV * (p.totalStake / totalStake);
        }
    }

    /**
     * Distributes the normalized Maximum Extractable Value (MEV) of a blockchain block among a list of proposers
     * proportionally based on their effective stake. The effective stake for each proposer is computed by applying
     * a logarithmic normalization formula to their total stake. If the total effective stake is zero, no MEV is distributed.
     *
     * @param block the blockchain block containing the total MEV to be distributed. The block must include a valid
     *              `totalMEV` value representing the total Maximum Extractable Value realized by the block's transactions.
     * @param proposers the list of entities eligible to receive a share of the block's normalized MEV. Each entity
     *                  in this list must define a valid total stake, which will be used to calculate the effective stake
     *                  and determine the proportional share of the normalized MEV.
     */
    public static void extractMEVNormalized(Block block, List<Entity> proposers) {
        double totalEffectiveStake = 0;
        double[] effectiveStakes = new double[proposers.size()];
        for (int i = 0; i < proposers.size(); i++) {
            Entity p = proposers.get(i);
            double ratio = p.totalStake / 32.0;
            double log2Ratio = Math.log(ratio) / Math.log(2);
            double effectiveStake = 32 * (1 + log2Ratio);
            effectiveStakes[i] = effectiveStake;
            totalEffectiveStake += effectiveStake;
        }
        if (totalEffectiveStake == 0) return;
        for (int i = 0; i < proposers.size(); i++) {
            proposers.get(i).normalizedMevEarned += block.totalMEV * (effectiveStakes[i] / totalEffectiveStake);
        }
    }

    public static void printReport(List<Entity> validators, String title) {
        System.out.println("\n=== " + title + " ===");
        for (Entity e : validators) {
            System.out.printf(
                    "%s | Type: %s | Stake: %d | MEV Earned: %.2f | Normalized MEV Earned: %.2f\n",
                    e.name,
                    e.type.getDisplayName(),
                    e.totalStake,
                    e.mevEarned,
                    e.normalizedMevEarned
            );
        }
    }

    /**
     * Exports the data of a list of entities to a CSV file. The CSV file includes the entity name, type,
     * total stake, MEV (Maximum Extractable Value) earned, and normalized MEV earned for each entity.
     *
     * @param entities the list of entities to be exported. Each entity in the list must include name, type,
     *                 total stake, MEV values, and other relevant data.
     * @param fileName the name of the CSV file to which the data will be written. This should include the
     *                 file extension (e.g., ".csv").
     */
    public static void export(List<Entity> entities, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // Header for comparison CSV
            writer.write("Name,Type,TotalStake,MEV_Default,MEV_Normalized");
            writer.newLine();

            for (int i = 0; i < entities.size(); i++) {
                Entity d = entities.get(i);
                writer.write(
                        d.name + "," +
                                d.type.getDisplayName() + "," +
                                d.totalStake + "," +
                                String.format("%.8f", d.mevEarned) + "," +
                                String.format("%.8f", d.normalizedMevEarned)
                );
                writer.newLine();
            }
            writer.flush();
            System.out.println("Comparison CSV file created: " + fileName);
        } catch (IOException ex) {
            System.out.println("Error while writing comparison CSV file: " + ex.getMessage());
        }
    }

    public List<Entity> selectProposers(List<Entity> validators, Random random) {
        List<Entity> whales = filterAndShuffle(validators, EntityType.WHALE, random);
        List<Entity> selectedWhales = whales.subList(0, Math.min(2, whales.size()));

        List<Entity> sharks = filterAndShuffle(validators, EntityType.SHARK, random);
        List<Entity> selectedSharks = sharks.subList(0, Math.min(NUM_SHARKS - selectedWhales.size(), sharks.size()));

        List<Entity> dolphins = filterAndShuffle(validators, EntityType.DOLPHIN, random);
        List<Entity> selectedDolphins = dolphins.subList(0, Math.min(NUM_DOLPHINS - selectedSharks.size(), dolphins.size()));

        List<Entity> fish = filterAndShuffle(validators, EntityType.FISH, random);
        List<Entity> selectedFish = fish.subList(0, Math.min(NUM_FISH - selectedDolphins.size(), fish.size()));

        List<Entity> octopuses = filterAndShuffle(validators, EntityType.OCTOPUS, random);
        List<Entity> selectedOctopuses = octopuses.subList(0, Math.min(NUM_OCTOPUSES - selectedFish.size(), octopuses.size()));

        List<Entity> crabs = filterAndShuffle(validators, EntityType.CRAB, random);
        List<Entity> selectedCrabs = crabs.subList(0, Math.min(NUM_CRABS - selectedOctopuses.size(), crabs.size()));

        List<Entity> proposers = new ArrayList<>();
        proposers.addAll(selectedWhales);
        proposers.addAll(selectedSharks);
        proposers.addAll(selectedDolphins);
        proposers.addAll(selectedFish);
        proposers.addAll(selectedOctopuses);
        proposers.addAll(selectedCrabs);
        return proposers;
    }

    public List<Entity> filterAndShuffle(List<Entity> entities, EntityType type, Random random) {
        List<Entity> filtered = new ArrayList<>();
        for (Entity e : entities) {
            if (e.type == type) {
                filtered.add(e);
            }
        }
        Collections.shuffle(filtered, random);
        return filtered;
    }

    /**
     * Runs a blockchain simulation by processing a given list of blocks and updating the MEV (Maximum Extractable Value)
     * metrics for each block's proposer.
     *
     * @param validators the list of entities eligible to propose blocks. Each entity must have a defined stake and
     *                   represent a valid participant in the simulation.
     * @param blocks     the list of blocks to be processed during the simulation. Each block contains a set of
     *                   transactions and an associated total MEV value.
     * @throws Exception if there is an error while sending transactions or performing the simulation steps.
     */
    public void runSimulation(List<Entity> validators, List<Block> blocks) throws Exception {
        Random random = new Random();
        for (Block block : blocks) {
            // Send transactions to blockchain
            for (Transaction tx : block.transactions) {
                blockchainUtil.sendTransaction(tx.to, BigDecimal.valueOf(tx.value));
            }

            // Randomly select proposers based on hierarchical rules
            List<Entity> proposers = selectProposers(validators, random);

            // Calculate MEV for selected proposers
            extractMEV(block, proposers);
            // Calculate normalized MEV for selected proposers
            extractMEVNormalized(block, proposers);
        }
    }
}
