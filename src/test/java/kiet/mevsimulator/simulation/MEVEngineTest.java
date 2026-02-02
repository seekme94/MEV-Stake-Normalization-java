package kiet.mevsimulator.simulation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MEVEngineTest {

    @Test
    public void testExtractMEV() {
        Entity sender = new Entity("Test", "Test", 100);
        Transaction mevTx = new Transaction(sender, "0x123", 100.0, true);
        Transaction normalTx = new Transaction(sender, "0x123", 100.0, false);

        assertEquals(1.0, MEVEngine.extractMEV(mevTx)); // 1% of 100
        assertEquals(0.0, MEVEngine.extractMEV(normalTx));
    }
}