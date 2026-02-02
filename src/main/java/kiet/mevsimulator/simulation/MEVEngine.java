package kiet.mevsimulator.simulation;

public class MEVEngine {

    public static double extractMEV(Transaction tx) {
        if (!tx.isMEV) {
            return 0.0;
        }
        return tx.value * 0.01;
    }
}
