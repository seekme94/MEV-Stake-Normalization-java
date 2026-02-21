package kiet.mevsimulator.simulation;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents a blockchain block consisting of a list of transactions and the total Maximum Extractable Value (MEV)
 * derived from those transactions.
 */
public class Block {
    public List<Transaction> transactions;
    public double totalMEV;

    public Block() {
        this.transactions = new ArrayList<>();
        this.totalMEV = 0.0;
    }

    public Block(List<Transaction> transactions) {
        this.transactions = transactions;
        this.totalMEV = 0.0;
        for (Transaction tx : transactions) {
            this.totalMEV += tx.value * 0.001;
        }
    }

    public void addTransaction(Transaction tx) {
        this.transactions.add(tx);
        this.totalMEV += tx.value * 0.001;
    }
}
