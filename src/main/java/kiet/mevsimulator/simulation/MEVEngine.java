package kiet.mevsimulator.simulation;

public class MEVEngine {

    public static double extractMEV(Transaction tx) {
        if (!tx.isMEV) {
            return 0.0;
        }
        return tx.value * 0.01;
    }
    public static double extractMEVNormalized(Transaction tx) {

        double baseMEV = extractMEV(tx); // reuse existing logic

        // Example normalization formula
        // (You can change later depending on research)
        double normalizedMEV = baseMEV * 0.7;

        return normalizedMEV;
    }
}
